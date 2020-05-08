package com.ipant.activities.home;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ipant.activities.CustomAndroidViewModel;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;

public class HomeActivityViewModel extends CustomAndroidViewModel {
    private NetworkConn.onRequestResponse onRequestResponse;


    public HomeActivityViewModel(@NonNull Application application, NetworkConn.onRequestResponse onRequestResponse) {
        super(application);
        this.onRequestResponse = onRequestResponse;


    }

    public void getWalletBalance() {
        Log.e("///", "HomeActivityViewModel->getWalletBalance");
        NetworkConn networkConn = NetworkConn.getInstance();
        networkConn.makeRequest(networkConn.createGetRequest(AppConstants.CHECK_CURRENT_WALLET_BALANCE), "wallet_current_balance", onRequestResponse);
    }


}
