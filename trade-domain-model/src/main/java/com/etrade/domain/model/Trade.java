package com.etrade.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Trade {
    private String entity;
    private Transaction transaction;
    private BigDecimal usdExchangeRate;
    private Currency currency;
    private LocalDate instructionDate;
    private LocalDate settlementDate;
    private int units;
    private BigDecimal pricePerUnit;
    private BigDecimal usdSettledAmount;

    public BigDecimal getUsdSettledAmount() {
        if (usdSettledAmount == null) {
            usdSettledAmount = pricePerUnit.multiply(new BigDecimal(units)).multiply(usdExchangeRate);
        }
        return usdSettledAmount;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public BigDecimal getUsdExchangeRate() {
        return usdExchangeRate;
    }

    public void setUsdExchangeRate(BigDecimal usdExchangeRate) {
        this.usdExchangeRate = usdExchangeRate;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public LocalDate getInstructionDate() {
        return instructionDate;
    }

    public void setInstructionDate(LocalDate instructionDate) {
        this.instructionDate = instructionDate;
    }

    public LocalDate getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(LocalDate settlementDate) {
        this.settlementDate = settlementDate;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }
}
