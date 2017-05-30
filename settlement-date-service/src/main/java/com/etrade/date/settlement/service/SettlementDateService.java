package com.etrade.date.settlement.service;

import com.etrade.domain.model.Currency;

import java.time.LocalDate;

public interface SettlementDateService {

    LocalDate calculate(LocalDate requestSettlementDate, Currency currency);
}
