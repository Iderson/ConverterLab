package com.lesson20.converterlab.models;

public class CurrencyModel {
    private String name;
    private AskBidModel currency;

    public String getName() {
        return name;
    }

    public void setName(String _name) {
        name = _name;
    }

    public AskBidModel getCurrency() {
        return currency;
    }

    public void setCurrency(AskBidModel _currency) {
        currency = _currency;
    }
}
