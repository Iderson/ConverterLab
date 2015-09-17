package com.lesson20.converterlab;

public class OrganizationModel {
    public String id;
    public String title;
    public String region;
    public String city;
    public String phone;
    public String address;
    public String link;
    public CurrencyModel currencies;

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

    public String getTitle() {
        return title;
    }

    public String getRegion() {
        return region;
    }

    public String getCity() {
        return city;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getLink() {
        return link;
    }
}
