package com.etrade.reporting.main;


import com.etrade.date.settlement.service.SettlementDateServiceImpl;
import com.etrade.domain.model.Currency;
import com.etrade.domain.model.Trade;
import com.etrade.domain.model.Transaction;
import com.etrade.reporting.TradeReportService;
import com.etrade.reporting.TradeReportServiceImpl;
import com.etrade.reporting.enumuration.TradeFlow;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

public class ReportingMainApp {

    private TradeReportService reportService = new TradeReportServiceImpl(new SettlementDateServiceImpl());

    public static void main(String[] args) {
        new ReportingMainApp().run();
    }

    private void run() {
        sendTrades(
                getTrade("bar", LocalDate.parse("2017-05-30"), Transaction.BUY, Currency.GBP, BigDecimal.TEN),
                getTrade("bar", LocalDate.parse("2017-05-31"), Transaction.SELL, Currency.INR, BigDecimal.TEN),
                getTrade("foo", LocalDate.parse("2017-05-30"), Transaction.BUY, Currency.GBP, BigDecimal.TEN),
                getTrade("foo", LocalDate.parse("2017-06-04"), Transaction.BUY, Currency.SAR, BigDecimal.TEN),
                getTrade("foo", LocalDate.parse("2017-06-03"), Transaction.SELL, Currency.SGP, BigDecimal.TEN),
                getTrade("foo", LocalDate.parse("2017-06-03"), Transaction.SELL, Currency.SAR, BigDecimal.TEN),
                getTrade("loream", LocalDate.parse("2017-06-06"), Transaction.BUY, Currency.AED, new BigDecimal("100")),
                getTrade("loream", LocalDate.parse("2017-06-03"), Transaction.BUY, Currency.PLN, new BigDecimal("100")),
                getTrade("ipsum", LocalDate.parse("2017-06-03"), Transaction.BUY, Currency.SAR, new BigDecimal("11.04")),
                getTrade("ipsum", LocalDate.parse("2017-06-01"), Transaction.SELL, Currency.SAR, new BigDecimal("11.04"))
        );
        printSettledUSDAmountByDate(TradeFlow.INCOMING);
        printSettledUSDAmountByDate(TradeFlow.OUTGOING);
        printEntityRankings(TradeFlow.INCOMING);
        printEntityRankings(TradeFlow.OUTGOING);

    }

    private void printEntityRankings(TradeFlow tradeFlow) {
        System.out.println("====Top performing entities for " + tradeFlow + " trades ====");

        reportService.getEntityRankings(tradeFlow)
                .forEach(System.out::println);
    }

    private void printSettledUSDAmountByDate(TradeFlow tradeFlow) {
        System.out.println("====Daily Settlement Report in USD with  " + tradeFlow + " trades ====");
        System.out.println("Date\t\t\t" + "Amount");

        reportService.settledUSDAmountByDate(tradeFlow).forEach((date, amount) ->
                System.out.println(date + "\t\t" + amount)
        );
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
