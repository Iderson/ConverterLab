package com.lesson20.converterlab.json;

import com.lesson20.converterlab.models.OrganizationModel;

import java.util.List;

/**
 * Created by Asus_Dev on 8/28/2015.
 */
public interface CallbackLoading {
    void onSuccess(List<OrganizationModel> _bankModel);
    void onFailure(String errorMessage);
}
