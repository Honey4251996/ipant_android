package com.ipant.activities.webview;

import android.app.Application;
import android.support.annotation.NonNull;

import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.CustomAndroidViewModel;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;

public class WebViewViewModel extends CustomAndroidViewModel {
    private NetworkConn.onRequestResponse onRequestResponse;
    private RequestResultInterface requestResultInterface;

    private String title = "", url = "";
    private int value = 0;

    public WebViewViewModel(@NonNull Application application, RequestResultInterface requestResultInterface, NetworkConn.onRequestResponse onRequestResponse) {
        super(application);
        this.requestResultInterface = requestResultInterface;
        this.onRequestResponse = onRequestResponse;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setValue(int value) {
        this.value = value;
    }


    public void attemptRequestWebViewUrl() {
        try {

            NetworkConn networkConn = NetworkConn.getInstance();

//            if (title.equalsIgnoreCase(getStringFromVM(R.string.txt_add_money))) {
//                url = AppConstants.ADDMONEYUSINGNETBANKING;
//            } else {
//                if (title.equalsIgnoreCase(getStringFromVM(R.string.txt_withdrawn_money))) {
//                    url = AppConstants.WITHDRAWMONEYUSINGNETBANKING;
//                }
//            }

            if (value == 1) {
                url = AppConstants.ADDMONEYUSINGNETBANKING;
            } else {
                if (value == 2) {
                    url = AppConstants.WITHDRAWMONEYUSINGNETBANKING;
                }
            }

            networkConn.makeRequest(networkConn.createGetRequest(url), "requestPageUrl", onRequestResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
