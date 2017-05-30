package com.etrade.reporting;

import com.etrade.date.settlement.service.SettlementDateServiceImpl;
import com.etrade.domain.model.Currency;
import com.etrade.domain.model.Trade;
import com.etrade.domain.model.Transaction;
import com.etrade.reporting.enumuration.TradeFlow;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TradeReportServiceImplTest {

    private TradeReportService reportService;

    @Before
    public void setup() {
        reportService = new TradeReportServiceImpl(new SettlementDateServiceImpl());
    }

    @Test(expected = TradeReportService.InvalidTradeFlowException.class)
    public void invalidTradeFlowForSettledUSDAmount() throws TradeReportService.InvalidTradeFlowException{
        reportService.settledUSDAmountByDate(null);
    }

    @Test(expected = TradeReportService.InvalidTradeFlowException.class)
    public void invalidTradeFlowForEntityRankings() throws TradeReportService.InvalidTradeFlowException{
        reportService.getEntityRankings(null);
    }

    @Test
    public void incomingUSDAmountForNoTradeIsZero() {
        Map<LocalDate, BigDecimal> incomingAmount = reportService.settledUSDAmountByDate(TradeFlow.INCOMING);
        Map<LocalDate, BigDecimal> outgoingAmount = reportService.settledUSDAmountByDate(TradeFlow.OUTGOING);
        assertEquals(0, incomingAmount.size());
        assertEquals(0, outgoingAmount.size());
    }

    @Test
    public void incomingSettledAmountForSingleTrade() {
        sendTrades(getTrade("foo", LocalDate.parse("2017-05-30"), Transaction.SELL, Currency.GBP, BigDecimal.TEN));
        Map<LocalDate, BigDecimal> incomingAmounts = reportService.settledUSDAmountByDate(TradeFlow.INCOMING);

        assertEquals(1, incomingAmounts.size());
        assertTrue(incomingAmounts.containsKey(LocalDate.parse("2017-05-30")));
        assertEquals(BigDecimal.TEN.multiply(new BigDecimal("100")).multiply(BigDecimal.TEN), incomingAmounts.get(LocalDate.parse("2017-05-30")));
    }

    @Test
    public void outgoingSettledAmountForSingleTrade() {
        sendTrades(getTrade("bar", LocalDate.parse("2017-05-30"), Transaction.BUY, Currency.GBP, BigDecimal.TEN));
        Map<LocalDate, BigDecimal> incomingAmounts = reportService.settledUSDAmountByDate(TradeFlow.OUTGOING);

        assertEquals(1, incomingAmounts.size());
        assertTrue(incomingAmounts.containsKey(LocalDate.parse("2017-05-30")));
        assertEquals(BigDecimal.TEN.multiply(new BigDecimal("100")).multiply(BigDecimal.TEN), incomingAmounts.get(LocalDate.parse("2017-05-30")));
    }

    @Test
    public void settledAmountForMultipleTradesOnSameDay() {
        sendTrades(
                getTrade("foo", LocalDate.parse("2017-05-30"), Transaction.BUY, Currency.GBP, BigDecimal.TEN),
                getTrade("bar", LocalDate.parse("2017-05-30"), Transaction.BUY, Currency.SAR, BigDecimal.TEN)
        );
        Map<LocalDate, BigDecimal> incomingAmounts = reportService.settledUSDAmountByDate(TradeFlow.OUTGOING);

        assertEquals(1, incomingAmounts.size());
        assertTrue(incomingAmounts.containsKey(LocalDate.parse("2017-05-30")));
        assertEquals(new BigDecimal("20000"), incomingAmounts.get(LocalDate.parse("2017-05-30")));

    }

    @Test
    public void tradeSettlingOnNextWorkingDay() {
        sendTrades(
                getTrade("foo", LocalDate.parse("2017-05-30"), Transaction.BUY, Currency.GBP, BigDecimal.TEN),
                getTrade("bar", LocalDate.parse("2017-06-03"), Transaction.BUY, Currency.SAR, BigDecimal.TEN)
        );
        Map<LocalDate, BigDecimal> incomingAmounts = reportService.settledUSDAmountByDate(TradeFlow.OUTGOING);

        assertEquals(2, incomingAmounts.size());
        assertFalse(incomingAmounts.containsKey(LocalDate.parse("2017-06-03")));
        assertTrue(incomingAmounts.containsKey(LocalDate.parse("2017-05-30")));
        assertTrue(incomingAmounts.containsKey(LocalDate.parse("2017-06-04")));
        assertEquals(BigDecimal.TEN.multiply(new BigDecimal("100")).multiply(BigDecimal.TEN), incomingAmounts.get(LocalDate.parse("2017-05-30")));
        assertEquals(BigDecimal.TEN.multiply(new BigDecimal("100")).multiply(BigDecimal.TEN), incomingAmounts.get(LocalDate.parse("2017-06-04")));
    }

    @Test
    public void entityRankingWithNoTrades() {
        List<String> incomingRankings = reportService.getEntityRankings(TradeFlow.INCOMING);
        List<String> outgoingRankings = reportService.getEntityRankings(TradeFlow.OUTGOING);
        assertEquals(0, incomingRankings.size());
        assertEquals(0, outgoingRankings.size());
    }


    @Test
    public void entityRankingForMultipleIncomingTrades() {
        sendTrades(
                getTrade("bar", LocalDate.parse("2017-05-30"), Transaction.SELL, Currency.GBP, BigDecimal.TEN),
                getTrade("foo", LocalDate.parse("2017-05-30"), Transaction.SELL, Currency.GBP, BigDecimal.TEN),
                getTrade("foo", LocalDate.parse("2017-06-03"), Transaction.SELL, Currency.SAR, BigDecimal.TEN)
        );

        List<String> incomingRankings = reportService.getEntityRankings(TradeFlow.INCOMING);
        assertEquals(2, incomingRankings.size());
        assertEquals("foo", incomingRankings.get(0));
        assertEquals("bar", incomingRankings.get(1));
    }

    @Test
    public void entityRankingForMultipleOutgoingTrades() {
        sendTrades(
                getTrade("bar", LocalDate.parse("2017-05-30"), Transaction.BUY, Currency.GBP, BigDecimal.TEN),
                getTrade("foo", LocalDate.parse("2017-05-30"), Transaction.BUY, Currency.GBP, BigDecimal.TEN),
                getTrade("foo", LocalDate.parse("2017-06-03"), Transaction.BUY, Currency.SAR, BigDecimal.TEN),
                getTrade("loream", LocalDate.parse("2017-06-03"), Transaction.BUY, Currency.SAR, new BigDecimal("100")),
                getTrade("ipsum", LocalDate.parse("2017-06-03"), Transaction.BUY, Currency.SAR, new BigDecimal("11.04"))
        );

        List<String> incomingRankings = reportService.getEntityRankings(TradeFlow.OUTGOING);
        assertEquals(4, incomingRankings.size());
        assertEquals("loream", incomingRankings.get(0));
        assertEquals("foo", incomingRankings.get(1));
        assertEquals("ipsum", incomingRankings.get(2));
        assertEquals("bar", incomingRankings.get(3));
    }


    private void sendTrades(Trade... trades) {
        Arrays.stream(trades).forEach(trade -> reportService.add(trade));
    }

    private static Trade getTrade(String entity, LocalDate settlementDate, Transaction transaction, Currency currency, BigDecimal usdExchangeRate) {
        Trade trade = new Trade();
        trade.setEntity(entity);
        trade.setTransaction(transaction);
        trade.setUsdExchangeRate(usdExchangeRate);
        trade.setCurrency(currency);
        trade.setInstructionDate(LocalDate.parse("2017-05-30"));
        trade.setSettlementDate(settlementDate);
        trade.setUnits(100);
        trade.setPricePerUnit(BigDecimal.TEN);
        return trade;
    }
}