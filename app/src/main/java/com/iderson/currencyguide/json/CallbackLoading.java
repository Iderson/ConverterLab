package com.iderson.currencyguide.json;

import com.iderson.currencyguide.models.OrganizationModel;

import java.util.List;

/**
 * Created by Asus_Dev on 8/28/2015.
 */
public interface CallbackLoading {
    void onSuccess(List<OrganizationModel> _organizationModelList);
    void onFailure(String errorMessage);
}
