package com.etrade.date.settlement.service;


import com.etrade.domain.model.Currency;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Consumer;

public class SettlementDateServiceImpl implements SettlementDateService {

    private static final EnumMap<Currency, List<DayOfWeek>> CURRENCY_NONWORKDAY_MAPPING = new EnumMap<>(Currency.class);

    static {
        Arrays.stream(Currency.values()).forEach(mapCurrencyWithNonWorkingDays());
    }

    private static Consumer<Currency> mapCurrencyWithNonWorkingDays() {
        return currency -> {
            if (currency == Currency.AED || currency == Currency.SAR)
                CURRENCY_NONWORKDAY_MAPPING.put(currency, Arrays.asList(DayOfWeek.FRIDAY, DayOfWeek.SATURDAY));
            else
                CURRENCY_NONWORKDAY_MAPPING.put(currency, Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY));
        };
    }


    @Override
    public LocalDate calculate(LocalDate requestSettlementDate, Currency currency) {
        while (CURRENCY_NONWORKDAY_MAPPING.get(currency).contains(requestSettlementDate.getDayOfWeek())) {
            requestSettlementDate = requestSettlementDate.plus(1, ChronoUnit.DAYS);
        }
        return requestSettlementDate;
    }
}
