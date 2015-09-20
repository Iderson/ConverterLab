package com.lesson20.converterlab.json;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.lesson20.converterlab.models.AskBidModel;
import com.lesson20.converterlab.models.CurrencyModel;
import com.lesson20.converterlab.models.OrganizationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class AsyncCurrencyLoader extends AsyncTask<Void, Void, List<OrganizationModel>>{

    private CallbackLoading mCallbackLoading;
    private Context mContext;
    private Map<String, String> jsonCityId;
    private Map<String, String> jsonRegionId;

    public AsyncCurrencyLoader(CallbackLoading callbackLoading, Context context) {
        this.mCallbackLoading   = callbackLoading;
        this.mContext           = context;
    }

    @Override
    protected List<OrganizationModel> doInBackground(Void... params) {
        List<OrganizationModel> organizationModelList = null;

        try {
            organizationModelList = loadData();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return organizationModelList;
    }

    @Override
    protected void onPostExecute(List<OrganizationModel> _organizationModels) {
        super.onPostExecute(_organizationModels);
        if (_organizationModels != null){
            mCallbackLoading.onSuccess(_organizationModels);
        } else mCallbackLoading.onFailure("Error parsing");
    }

    private Map<String, String> setIdToMap(JSONObject j) {
        Map<String, String> map = new HashMap<>();
        Iterator it = j.keys();
        while (it.hasNext()) {
            String n = (String) it.next();
            try {
                map.put(n, j.getString(n));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    private List<OrganizationModel> loadData() throws IOException, JSONException {
        List<OrganizationModel> organizationModels = new ArrayList<>();

        try {
            JSONObject jsonString = JSONfunctions
                    .getJSONfromURL
                            ("http://resources.finance.ua/ru/public/currency-cash.json");
            JSONArray jsonOrgArray = jsonString.getJSONArray("organizations");
            jsonCityId = setIdToMap(jsonString.getJSONObject("cities"));
            jsonRegionId = setIdToMap(jsonString.getJSONObject("regions"));

            for (int i = 0; i < jsonOrgArray.length(); i++) {
                OrganizationModel orgModel = getOrganization(jsonOrgArray.getJSONObject(i));
                organizationModels.add(orgModel);
            }
//            bankModel.header.add(bankModel.organiztions);

        }
        catch (JSONException e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        return organizationModels;
    }

    private OrganizationModel getOrganization(JSONObject _jOrganization) {
        OrganizationModel org = new OrganizationModel();
        try{

            org.setId( "" + _jOrganization.getString("id"));
            org.setTitle("" + _jOrganization.getString("title"));
            org.setRegion("" + jsonRegionId.get(_jOrganization.getString("regionId")));
            org.setCity("" + jsonCityId.get(_jOrganization.getString("cityId")));

            org.setLink("" + _jOrganization.getString("link"));
            org.setAddress("" + _jOrganization.getString("address"));
            org.setPhone("" + _jOrganization.getString("phone"));

            CurrencyModel currencies = new CurrencyModel();
            currencies.USD = new AskBidModel();
            currencies.EUR = new AskBidModel();
            currencies.RUB = new AskBidModel();
            currencies.PLN = new AskBidModel();
            currencies.GBP = new AskBidModel();

            JSONObject jsonCur = _jOrganization.getJSONObject("currencies");
            JSONObject jsonUSD = jsonCur.getJSONObject("USD");
            currencies.USD.ask = jsonUSD.getLong("ask");
            currencies.USD.bid = jsonUSD.getLong("bid");

            JSONObject jsonEUR = jsonCur.getJSONObject("EUR");
            currencies.EUR.ask = jsonEUR.getLong("ask");
            currencies.EUR.bid = jsonEUR.getLong("bid");

            JSONObject jsonRUB = jsonCur.getJSONObject("RUB");
            currencies.RUB.ask = jsonRUB.getLong("ask");
            currencies.RUB.bid = jsonRUB.getLong("bid");

            JSONObject jsonPLN = jsonCur.getJSONObject("PLN");
            currencies.PLN.ask = jsonPLN.getLong("ask");
            currencies.PLN.bid = jsonPLN.getLong("bid");

            JSONObject jsonGBP = jsonCur.getJSONObject("GBP");
            currencies.GBP.ask = jsonGBP.getLong("ask");
            currencies.GBP.bid = jsonGBP.getLong("bid");

            org.setCurrencies(currencies);
    }
    catch (JSONException je){
        Log.e("Error", je.getMessage());
        je.printStackTrace();
    }
    catch (Exception e){
        Log.e("Error", e.getMessage());
        e.printStackTrace();
    }


        return org;
    }

}
