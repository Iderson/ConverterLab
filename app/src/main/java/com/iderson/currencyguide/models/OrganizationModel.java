package com.iderson.currencyguide.models;

import java.util.ArrayList;

public class OrganizationModel {
    private String id;
    private String title;
    private String region;
    private String city;
    private String phone;
    private String address;
    private String link;
    private ArrayList<CurrencyModel> currencies;

    public OrganizationModel() {
    }

    public OrganizationModel(String _id, String _title, String _region, String _city, String _phone, String _address, String _link) {
        id = _id;
        title = _title;
        region = _region;
        city = _city;
        phone = _phone;
        address = _address;
        link = _link;
    }

    public ArrayList<CurrencyModel> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(ArrayList<CurrencyModel> _currencies) {
        currencies = _currencies;
    }

    public void setCurrencies(CurrencyModel _currencies) {
    }

    public String getId() {
        return id;
    }

    public void setId(String _id) {
        id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String _title) {
        title = _title;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String _region) {
        region = _region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String _city) {
        city = _city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String _phone) {
        phone = _phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String _address) {
        address = _address;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String _link) {
        link = _link;
    }

}
