package com.ipant.activities.mycards;

import android.app.Application;
import android.util.Log;

import com.ipant.activities.CustomAndroidViewModel;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;

import org.json.JSONObject;

public class MyCardViewModel extends CustomAndroidViewModel {
    private NetworkConn.onRequestResponse onRequestResponse;

    public MyCardViewModel(Application application, NetworkConn.onRequestResponse onRequestResponse) {
        super(application);
        this.onRequestResponse = onRequestResponse;
    }

    public void getCardList(int pageCount) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("page_no", String.valueOf(pageCount));

            NetworkConn networkConn = NetworkConn.getInstance();
            networkConn.makeRequest(networkConn.createPostRequest(AppConstants.GET_CARD_DETAILS, jsonObject.toString()), "cardDetails", onRequestResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteCardOrBank(String id) {
        try {
            JSONObject json = new JSONObject();
            json.put("cardId", id);
            json.put("type", "Card");
            NetworkConn networkConn = NetworkConn.getInstance();
            networkConn.makeRequest(networkConn.createPostRequest(AppConstants.DELETE_CARD_OR_BANK_URL, json.toString()),
                    "deleteCard", onRequestResponse);
        } catch (Exception e) {

            Log.e("Exception", "Exception: " + e.getMessage());
        }

    }
}
