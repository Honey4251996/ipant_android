package com.ipant.activities.send_money;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;

import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.CustomAndroidViewModel;
import com.ipant.activities.common_payment_option.CommonPaymentOptionActivity;
import com.ipant.activities.login.bean.UserInfo;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class SendMoneyViewModel extends CustomAndroidViewModel {
    private NetworkConn.onRequestResponse onRequestResponse;
    private RequestResultInterface requestResultInterface;
    private DecimalFormat df;

    private String amount = "";
    private String comment = "";
    private String mobileNumber = "";

    public SendMoneyViewModel(@NonNull Application application, RequestResultInterface requestResultInterface, NetworkConn.onRequestResponse onRequestResponse) {
        super(application);
        this.requestResultInterface = requestResultInterface;
        this.onRequestResponse = onRequestResponse;
        df = new DecimalFormat("#.00");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);

    }

    public String getAmount() {
        return amount;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public TextWatcher getMobileNumberTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String number=editable.toString();

                if(number.startsWith("00")){
                    editable.clear();
                }
                mobileNumber = editable.toString();
            }
        };
    }


    public TextWatcher getAmountTextWatcher() {
        return new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                /*  temp = editText.getText().toString().length();*/


            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (s.toString().equalsIgnoreCase("")) {
                    amount = "";
                } else {

                    String sEdtAmount = s.toString();

                    if (sEdtAmount.startsWith(".")) {
                        sEdtAmount = 0 + sEdtAmount;
                    }

                    amount = df.format(Double.parseDouble(sEdtAmount));
                }
            }
        };
    }

    public TextWatcher getCommentTextWatcher() {
        return new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                comment = s.toString();
            }
        };
    }

    private String isFormValid() {
        if (mobileNumber.equalsIgnoreCase("")) {
            return getStringFromVM(R.string.error_mobile_number_empty);
        }

        if (mobileNumber.length() < 9) {
            return getStringFromVM(R.string.error_mobile_number_length);
        }

        if (mobileNumber.startsWith("00")) {
            return getStringFromVM(R.string.error_invalid_mobile_number);
        }

        if (!Patterns.PHONE.matcher(mobileNumber).matches()) {
            return getStringFromVM(R.string.error_invalid_mobile_number);
        }

        if (amount.trim().equalsIgnoreCase("")) {
            return getStringFromVM(R.string.error_enter_amount);
        }

        if (Float.parseFloat(amount) == 0) {
            return getStringFromVM(R.string.error_invalid_amount);
        }
        return "";
    }





    public void checkUserWalletBalanceForAction(UserInfo userInfo) {

        if (Double.parseDouble(amount) <= Double.parseDouble(AppConstants.WALLET_AMOUNT)) {
            attemptSendMoney();
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("amount", amount);
            bundle.putString("requestType", getStringFromVM(R.string.txt_send_money));
            bundle.putString("mobile_email", mobileNumber);
            bundle.putString("comment", comment);
            bundle.putString("name", userInfo.getFirstname() + " " + userInfo.getLastname());
            bundle.putInt("moneyRequestType", 1);
            requestResultInterface.startNewActivity(bundle, CommonPaymentOptionActivity.class);
        }

    }

    public void fireContactPickerEvent() {
        requestResultInterface.onSucess("pickContact");
    }

    public void onSendMoneyClicked() {
        String getFormMsg = isFormValid();

        if (!getFormMsg.equalsIgnoreCase("")) {
            requestResultInterface.onRequestRequirementFail(getFormMsg);
            return;
        }
        requestResultInterface.onSucess("validSendMoneyData");

    }

    public void checkUserExistance() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("emailMobile", mobileNumber);
            jsonObject.put("amount", amount);

            NetworkConn networkConn = NetworkConn.getInstance();
            networkConn.makeRequest(networkConn.createPostRequest(AppConstants.CHECK_USER_EXISTANCE_URL, jsonObject.toString()), "UserExistance", onRequestResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void attemptSendMoney() {

        try {
            JSONObject jsonBody = new JSONObject();

            //Common and Wallet Payment Required Parameters
            jsonBody.put("send_money_method", "5");
            jsonBody.put("mobile_email", mobileNumber);
            jsonBody.put("amount", amount);
            jsonBody.put("comment", comment);
            jsonBody.put("charge", "0");
            jsonBody.put("use_my_wallet", "1");

            NetworkConn networkConn = NetworkConn.getInstance();
            networkConn.makeRequest(networkConn.createPostRequest(AppConstants.SEND_MONEY_URL, jsonBody.toString()), "sendMoney", onRequestResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
