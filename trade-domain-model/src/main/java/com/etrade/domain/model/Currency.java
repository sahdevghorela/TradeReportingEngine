package com.etrade.domain.model;

public enum Currency {

    USD("usd"), INR("inr"), SGP("sgp"), AED("aed"), SAR("sar"), GBP("gbp"), PLN("pln");

    private String code;

    Currency(String code) {
        this.code = code;
    }
}
