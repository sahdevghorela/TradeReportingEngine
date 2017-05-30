package com.etrade.reporting;

import com.etrade.domain.model.Trade;
import com.etrade.reporting.enumuration.TradeFlow;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TradeReportService {

    Map<LocalDate, BigDecimal> settledUSDAmountByDate(TradeFlow tradeFlow);

    List<String> prepareEntityRanking(TradeFlow tradeFlow);

    void add(Trade trade);

}
