package com.ipant.activities.home.fragment.viewmodels;

import android.app.Application;
import android.util.Log;

import com.ipant.activities.CustomAndroidViewModel;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class WalletViewModel extends CustomAndroidViewModel {
    private NetworkConn.onRequestResponse onRequestResponse;
    private String transactionType = "";

    private int pageCount = 0;

    public WalletViewModel(Application application, NetworkConn.onRequestResponse onRequestResponse) {
        super(application);
        this.onRequestResponse = onRequestResponse;
    }
    // TODO: Implement the ViewModel


    public int getPageCount() {
        return pageCount;
    }

    public void getTransactionList(int pageCount, String transactionType) {


        try {

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("page_no", String.valueOf(pageCount));
            jsonObject.put("transaction_type", transactionType);
            Log.e("BtaoDetail",jsonObject.toString());

            NetworkConn networkConn = NetworkConn.getInstance();
            networkConn.makeRequest(networkConn.createPostRequest(AppConstants.GET_TRANSACTION_HISTORY_URL, jsonObject.toString()), "transanction_History", onRequestResponse);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public void selectTransactionType(int pageCount, int selectedTabPosition) {

        String transactionType = "";
        switch (selectedTabPosition) {
            case 0:
                transactionType = "";
                break;
            case 1:
                transactionType = "2";
                break;
            case 2:
                transactionType = "3";
                break;
            case 3:
                transactionType = "1";
                break;
        }
        this.transactionType = transactionType;
        this.pageCount=pageCount;
        getTransactionList(pageCount, transactionType);
    }

    public void loadMoreOrPullToRefereshFunctionality(boolean isLoadMore) {

        if (isLoadMore) {
            pageCount = pageCount + 1;

        } else {
            pageCount = 0;

        }
        getTransactionList(pageCount, transactionType);
    }
}
