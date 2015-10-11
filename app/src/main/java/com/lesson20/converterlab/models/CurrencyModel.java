package com.lesson20.converterlab.models;

public class CurrencyModel {
    private String mName;

    private String mFullName;

    private AskBidModel mCurrency;

    public String getName() {
        return mName;
    }

    public void setName(String _name) {
        mName = _name;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String _fullName) {
        mFullName = _fullName;
    }

    public AskBidModel getCurrency() {
        return mCurrency;
    }

    public void setCurrency(AskBidModel _currency) {
        mCurrency = _currency;
    }
}
