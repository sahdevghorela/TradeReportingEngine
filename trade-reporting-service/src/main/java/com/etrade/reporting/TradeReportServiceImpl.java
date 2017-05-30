package com.etrade.reporting;

import com.etrade.date.settlement.service.SettlementDateService;
import com.etrade.domain.model.Trade;
import com.etrade.domain.model.Transaction;
import com.etrade.reporting.enumuration.TradeFlow;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TradeReportServiceImpl implements TradeReportService {

    private static final BiPredicate<TradeFlow, Transaction> FILTER_BY_TRADE_FLOW = (flow, txn) -> {
        if (TradeFlow.INCOMING == flow) {
            return txn == Transaction.SELL;
        } else if (TradeFlow.OUTGOING == flow) {
            return txn == Transaction.BUY;
        }
        return false;
    };

    private SettlementDateService settlementDateService;
    private List<Trade> trades;

    public TradeReportServiceImpl(SettlementDateService settlementDateService) {
        this.settlementDateService = settlementDateService;
        trades = new ArrayList<>();
    }

    @Override
    public Map<LocalDate, BigDecimal> settledUSDAmountByDate(final TradeFlow tradeFlow) {
        return groupAndSumUpTrades(tradeFlow, getSettlementDate());
    }

    @Override
    public List<String> getEntityRankings(TradeFlow tradeFlow) {
        Map<String, BigDecimal> rankingMap = groupAndSumUpTrades(tradeFlow, getEntity());
        return rankingMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public void add(Trade trade) {
        trade.setSettlementDate(settlementDateService.calculate(trade.getSettlementDate(), trade.getCurrency()));
        trades.add(trade);
    }

    private <R> Map<R, BigDecimal> groupAndSumUpTrades(TradeFlow tradeFlow, Function<Trade, R> groupByFunction) {
        return trades.stream()
                .filter(trade -> FILTER_BY_TRADE_FLOW.test(tradeFlow, trade.getTransaction()))
                .collect(
                        Collectors.groupingBy(
                                groupByFunction, Collectors.mapping(
                                        Trade::getUsdSettledAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
    }

    private Function<Trade, String> getEntity() {
        return Trade::getEntity;
    }

    private Function<Trade, LocalDate> getSettlementDate() {
        return Trade::getSettlementDate;
    }
}
