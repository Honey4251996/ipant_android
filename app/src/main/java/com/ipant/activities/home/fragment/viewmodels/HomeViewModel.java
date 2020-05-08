package com.ipant.activities.home.fragment.viewmodels;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ipant.activities.CustomAndroidViewModel;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;

public class HomeViewModel extends CustomAndroidViewModel {
    private NetworkConn.onRequestResponse onRequestResponse;

    public HomeViewModel(@NonNull Application application, NetworkConn.onRequestResponse onRequestResponse) {
        super(application);
        this.onRequestResponse=onRequestResponse;
    }

    public void getWalletBalance(){
        Log.e("///", "HomeViewModel->getWalletBalance");
        NetworkConn networkConn=NetworkConn.getInstance();
        networkConn.makeRequest(networkConn.createGetRequest(AppConstants.CHECK_CURRENT_WALLET_BALANCE), "wallet_current_balance", onRequestResponse);
    }
}
