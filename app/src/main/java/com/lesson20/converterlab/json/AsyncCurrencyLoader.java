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
    private Map<String, String> mJsonCityId;
    private Map<String, String> mJsonRegionId;
    private Map<String, String> mJsonCurrencyId;

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
            mJsonCityId = setIdToMap(jsonString.getJSONObject("cities"));
            mJsonRegionId = setIdToMap(jsonString.getJSONObject("regions"));
            mJsonCurrencyId = setIdToMap(jsonString.getJSONObject("currencies"));

            for (int i = 0; i < jsonOrgArray.length(); i++) {
                OrganizationModel orgModel = getOrganization(jsonOrgArray.getJSONObject(i));
                organizationModels.add(orgModel);
            }
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

            org.setId       ("" + _jOrganization.getString("id"));
            org.setTitle    ("" + _jOrganization.getString("title"));
            org.setRegion   ("" + mJsonRegionId.get(_jOrganization.getString("regionId")));
            org.setCity     ("" + mJsonCityId.get(_jOrganization.getString("cityId")));

            org.setLink     ("" + _jOrganization.getString("link"));
            org.setAddress  ("" + _jOrganization.getString("address"));
            org.setPhone    ("" + _jOrganization.getString("phone"));

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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            org.setCurrencies(currencyList);

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
