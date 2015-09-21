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
    private Map<String, String> jsonCurrencyId;

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
            jsonCurrencyId = setIdToMap(jsonString.getJSONObject("currencies"));

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
        ArrayList<CurrencyModel> currencyList = new ArrayList<>();
        CurrencyModel currency = new CurrencyModel();
        AskBidModel askBid = new AskBidModel();

        try{

            org.setId("" + _jOrganization.getString("id"));
            org.setTitle("" + _jOrganization.getString("title"));
            org.setRegion("" + jsonRegionId.get(_jOrganization.getString("regionId")));
            org.setCity("" + jsonCityId.get(_jOrganization.getString("cityId")));

            org.setLink("" + _jOrganization.getString("link"));
            org.setAddress("" + _jOrganization.getString("address"));
            org.setPhone("" + _jOrganization.getString("phone"));

            JSONObject jsonObject = _jOrganization.getJSONObject("currencies");
            Iterator it = (jsonObject.keys());
            while (it.hasNext()) {
                String n = (String) it.next();
                try {
                    currency.setName(n);
                    JSONObject jsonAskBid = jsonObject.getJSONObject(n);
                    askBid.setAsk(jsonAskBid.getLong("ask"));
                    askBid.setBid(jsonAskBid.getLong("bid"));
                    currency.setCurrency(askBid);
                    currencyList.add(currency);
//                    map.put(n, j.getString(n));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            org.setCurrencies(currencyList);
            /*Map<String, String> jsonCur = new HashMap<>();
            jsonCur = setIdToMap(jsonObject);
            for (int i = 0; i < jsonCur.size(); i++) {
                jsonObject.keys()
            }
            JSONObject jsonUSD = jsonCur.getJSONObject("USD");
            askBid.setAsk(jsonUSD.getLong("ask"));
            askBid.setBid(jsonUSD.getLong("bid"));
            currencies.setUSD(askBid);

            JSONObject jsonEUR = jsonCur.getJSONObject("EUR");
            askBid.setAsk(jsonEUR.getLong("ask"));
            askBid.setBid(jsonEUR.getLong("bid"));
            currencies.setEUR(askBid);

            JSONObject jsonRUB = jsonCur.getJSONObject("RUB");
            askBid.setAsk(jsonRUB.getLong("ask"));
            askBid.setBid(jsonRUB.getLong("bid"));
            currencies.setRUB(askBid);

            JSONObject jsonPLN = jsonCur.getJSONObject("PLN");
            askBid.setAsk(jsonPLN.getLong("ask"));
            askBid.setBid(jsonPLN.getLong("bid"));
            currencies.setPLN(askBid);

            JSONObject jsonGBP = jsonCur.getJSONObject("GBP");
            askBid.setAsk(jsonGBP.getLong("ask"));
            askBid.setBid(jsonGBP.getLong("bid"));
            currencies.setGBP(askBid);

            org.setCurrencies(currencies);*/
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
