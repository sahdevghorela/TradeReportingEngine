package com.etrade.date.settlement.service;

import com.etrade.domain.model.Currency;
import org.junit.Test;

import java.time.LocalDate;

import static com.etrade.domain.model.Currency.*;
import static org.junit.Assert.*;

public class SettlementDateServiceImplTest {

    SettlementDateService settlementDateService = new SettlementDateServiceImpl();

    @Test
    public void calculateWithinWorkingDay() throws Exception {
        LocalDate requestSettlementDate = LocalDate.parse("2017-05-29");
        assertTrue(requestSettlementDate.equals(calculate("2017-05-29", GBP)));
    }

    @Test
    public void calculateForNonWorkingSunday() throws Exception {
        assertEquals(LocalDate.parse("2017-06-05"),calculate("2017-06-04",USD));
    }

    @Test
    public void calculateForNonWorkingSaturday() throws Exception {
        assertEquals(LocalDate.parse("2017-06-05"),calculate("2017-06-03",PLN));
    }

    @Test
    public void calculateForWorkingSunday() throws  Exception {
        assertEquals(LocalDate.parse("2017-06-04"),calculate("2017-06-04",AED));
    }

    @Test
    public void calculateForNonWorkingFriday() throws  Exception {
        assertEquals(LocalDate.parse("2017-06-04"),calculate("2017-06-02",SAR));
    }

    private LocalDate calculate(String forSettlementDate, Currency withCurrency){
        return settlementDateService.calculate(LocalDate.parse(forSettlementDate),withCurrency);
    }

}