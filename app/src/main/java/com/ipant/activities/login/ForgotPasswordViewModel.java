package com.ipant.activities.login;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;

import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.CustomAndroidViewModel;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;

import org.json.JSONObject;

public class ForgotPasswordViewModel extends CustomAndroidViewModel {

    private NetworkConn.onRequestResponse onRequestResponse;
    private RequestResultInterface requestResultInterface;
    private String emailOrNumber = "";

    public ForgotPasswordViewModel(@NonNull Application application, RequestResultInterface requestResultInterface, NetworkConn.onRequestResponse onRequestResponse) {
        super(application);
        this.requestResultInterface = requestResultInterface;
        this.onRequestResponse = onRequestResponse;
    }

    public TextWatcher getEmailDialogTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                emailOrNumber = editable.toString();
            }
        };
    }


    public String isValidLoginData() {
        if (TextUtils.isEmpty(emailOrNumber)) {
            return getStringFromVM(R.string.error_mobile_number_empty);
        }

        if (emailOrNumber.length() < 8) {
            return getStringFromVM(R.string.error_mobile_number_length);
        }

        if(!Patterns.PHONE.matcher(emailOrNumber).matches()){
            return getStringFromVM(R.string.error_invalid_mobile_number);
        }
        return "";
    }


    public void onSubmitClicked() {

        if (isValidLoginData().equalsIgnoreCase("")) {
            requestResultInterface.onSucess("ForgotPassword");
        } else {
            requestResultInterface.onRequestRequirementFail(isValidLoginData());
        }
    }


    public void attemptForgotPasswordRequest() {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mobile_no", emailOrNumber);
            jsonObject.put("varification_type", "1");


            Log.e("Number", ""+emailOrNumber);

            NetworkConn networkConn = NetworkConn.getInstance();
            networkConn.makeRequest(networkConn.createPostRequest(AppConstants.USER_SIGN_UP_OR_FORGOT_PASSWORD, jsonObject.toString()), "ForgotPassword", onRequestResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getMobileNumber() {
        return emailOrNumber;
    }
}
