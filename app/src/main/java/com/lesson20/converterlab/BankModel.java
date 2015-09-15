package com.lesson20.converterlab;


public class BankModel {
    private String mName;
    private String mCity;
    private String mRegion;
    private String mPhoneMain;
    private String mAddress;

    public BankModel(String _name, String _city, String _region, String _phoneMain, String _address) {
        mName = _name;
        mCity = _city;
        mRegion = _region;
        mPhoneMain = _phoneMain;
        mAddress = _address;
    }


    public void setName(String _name) {
        mName = _name;
    }
    public void setCity(String _city) {
        mCity = _city;
    }

    public void setRegion(String _region) {
        mRegion = _region;
    }

    public void setPhoneMain(String _phoneMain) {
        mPhoneMain = _phoneMain;
    }

    private void setAddress(String _address) {
        mAddress = _address;
    }

    public String getName() {
        return mName;
    }

    public String getCity() {
        return mCity;
    }

    public String getRegion() {
        return mRegion;
    }

    public String getPhoneMain() {
        return mPhoneMain;
    }

    public String getAddress() {
        return mAddress;
    }
}
