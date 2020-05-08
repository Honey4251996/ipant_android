package com.ipant.activities.login;

import android.app.Application;
import android.os.Bundle;
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

public class LoginViewModel extends CustomAndroidViewModel {
    private NetworkConn.onRequestResponse onRequestResponse;
    private RequestResultInterface requestResultInterface;
    private String emailOrMobile = "", password = "";


    public LoginViewModel(@NonNull Application application, RequestResultInterface requestResultInterface, NetworkConn.onRequestResponse onRequestResponse) {
        super(application);
        this.requestResultInterface = requestResultInterface;
        this.onRequestResponse = onRequestResponse;
    }


    public void mobileTextChanged(Editable editable) {
        Log.e("After Text Changed", editable.toString());
        emailOrMobile = editable.toString();

    }

    public TextWatcher getPasswordTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                password = editable.toString();
            }
        };
    }


    public String isValidLoginData() {
        if (emailOrMobile.equalsIgnoreCase("")) {
            return getStringFromVM(R.string.error_mobile_number_empty);
        }

        if (emailOrMobile.length() < 8) {
            return getStringFromVM(R.string.error_mobile_number_length);
        }

        if (emailOrMobile.startsWith("000")) {
            return getStringFromVM(R.string.error_invalid_mobile_number);
        }

        if (!Patterns.PHONE.matcher(emailOrMobile).matches()) {
            return getStringFromVM(R.string.error_invalid_mobile_number);
        }

        if (TextUtils.isEmpty(password)) {
            return getStringFromVM(R.string.error_password_empty);
        }

        if (password.length() < 6) {
            return getStringFromVM(R.string.error_password_length);
        }

        return "";
    }


    public void onLoginClicked() {

        if (isValidLoginData().equalsIgnoreCase("")) {
            requestResultInterface.onSucess("Login");
        } else {
            requestResultInterface.onRequestRequirementFail(isValidLoginData());
        }
    }

    public void onSignUpClicked() {
        Bundle bundle = new Bundle();
        bundle.putInt("varification_type", 0);
    }

    public void attemptLoginRequest() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mobile_no", emailOrMobile);
            jsonObject.put("password", password);

            NetworkConn networkConn = NetworkConn.getInstance();
            networkConn.makeRequest(networkConn.createPostRequest(AppConstants.USER_LOGIN_URL, jsonObject.toString()), "LoginResponse", onRequestResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }


}
