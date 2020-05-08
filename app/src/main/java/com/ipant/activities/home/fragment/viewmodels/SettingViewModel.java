package com.ipant.activities.home.fragment.viewmodels;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.ipant.RequestResultInterface;
import com.ipant.activities.CustomAndroidViewModel;
import com.ipant.activities.home.fragment.beans.ContactUsBean;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;

import org.json.JSONObject;

public class SettingViewModel extends CustomAndroidViewModel {
    private NetworkConn.onRequestResponse onRequestResponse;
    private MutableLiveData<ContactUsBean.Datum> datum;
    private RequestResultInterface requestResultInterface;
    private int notificationStatus=0;


    public SettingViewModel(@NonNull Application application, NetworkConn.onRequestResponse onRequestResponse, RequestResultInterface requestResultInterface) {
        super(application);
        this.onRequestResponse = onRequestResponse;
        this.requestResultInterface=requestResultInterface;
    }


    public void onNotificationSettingChange(boolean isCheckChange){

        if(isCheckChange){
            notificationStatus=1;
        }else{
            notificationStatus=0;
        }
        requestResultInterface.onSucess("checkChanged");
    }

    public void attemptChangeNotificationStatus() {
        try{
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("notification_status", notificationStatus);
            NetworkConn networkConn = NetworkConn.getInstance();
            networkConn.makeRequest(networkConn.createPutRequest(AppConstants.UPDATE_NOTIFICATION_STATUS_URL, jsonObject.toString()), "changeNotificationStatus", onRequestResponse);
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}