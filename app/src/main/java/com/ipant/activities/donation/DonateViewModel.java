package com.ipant.activities.donation;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;

import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.CustomAndroidViewModel;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class DonateViewModel extends CustomAndroidViewModel {
    private RequestResultInterface requestResultInterface;
    private NetworkConn.onRequestResponse onRequestResponse;
    private DecimalFormat df;

    private String amount = "";

    public DonateViewModel(@NonNull Application application, RequestResultInterface requestResultInterface, NetworkConn.onRequestResponse onRequestResponse) {
        super(application);
        this.requestResultInterface = requestResultInterface;
        this.onRequestResponse=onRequestResponse;
        df = new DecimalFormat("#.00");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);
    }

    public void setAmount(int amount) {
        requestResultInterface.onSucess(df.format(Double.parseDouble(String.valueOf(amount))));
    }

    public TextWatcher getAmountTextWatcher() {
        return new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) { }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (s.toString().equalsIgnoreCase("")) {
                    amount = "";
                } else {

                    String sEdtAmount=s.toString();

                    if(sEdtAmount.startsWith(".")){
                        sEdtAmount=0+sEdtAmount;
                    }
                    amount = df.format(Double.parseDouble(sEdtAmount));
                }

            }
        };
    }

    private String isFormValid() {
        if (amount.equalsIgnoreCase("")) {
            return getStringFromVM(R.string.error_enter_amount);
        }

        if (Float.parseFloat(amount) == 0) {
            return getStringFromVM(R.string.error_invalid_amount);
        }

        if (Double.parseDouble(amount) > Double.parseDouble(AppConstants.WALLET_AMOUNT)) {
            return getStringFromVM(R.string.error_insufficient_balancew);
        }
        return "";
    }

    public void attemptDonateMoney() {

        try {
            JSONObject jsonBody = new JSONObject();

            //Common and Wallet Payment Required Parameters
            jsonBody.put("amount", amount);


            NetworkConn networkConn = NetworkConn.getInstance();
            networkConn.makeRequest(networkConn.createPostRequest(AppConstants.DONATE_MONEY_URL, jsonBody.toString()), "donateMoney", onRequestResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    public void onAddMoneyClicked() {
        String getFormMsg = isFormValid();

        if (!getFormMsg.equalsIgnoreCase("")) {
            requestResultInterface.onRequestRequirementFail(getFormMsg);
            return;
        }

     requestResultInterface.onSucess("DonateNow");

    }
}
