package com.etrade.domain.model;

public enum Transaction {

    BUY("B"), SELL("S");

    private String symbol;

    Transaction(String symbol) {
        this.symbol = symbol;
    }
}
