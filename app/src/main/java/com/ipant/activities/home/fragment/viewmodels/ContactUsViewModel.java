package com.ipant.activities.home.fragment.viewmodels;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.ipant.activities.CustomAndroidViewModel;
import com.ipant.activities.home.fragment.beans.ContactUsBean;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;

public class ContactUsViewModel extends CustomAndroidViewModel {
    private NetworkConn.onRequestResponse onRequestResponse;
    private MutableLiveData<ContactUsBean.Datum> datum;


    public ContactUsViewModel(@NonNull Application application, NetworkConn.onRequestResponse onRequestResponse) {
        super(application);
        this.onRequestResponse = onRequestResponse;


    }

    public void getContactDetails() {
        NetworkConn networkConn = NetworkConn.getInstance();
        networkConn.makeRequest(networkConn.createGetRequest(AppConstants.CONTACT_US_URL), "contactUsDetails", onRequestResponse);
    }

}
