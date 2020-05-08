package com.ipant.activities.changePassword;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.CustomAndroidViewModel;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;

import org.json.JSONObject;

public class ChangePasswordViewModel extends CustomAndroidViewModel {

    private NetworkConn.onRequestResponse onRequestResponse;
    private RequestResultInterface requestResultInterface;
    private String newPassowrd = "", confirmNewPassword = "", currentPassword="";
    private String user_id = "";



    private int varification_type=0;

    public ChangePasswordViewModel(@NonNull Application application, RequestResultInterface requestResultInterface, NetworkConn.onRequestResponse onRequestResponse) {
        super(application);
        this.requestResultInterface = requestResultInterface;
        this.onRequestResponse = onRequestResponse;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setVarification_type(int varification_type) {
        this.varification_type = varification_type;
    }


    public TextWatcher getOldPasswordTextWatcher(){
        return  new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentPassword=editable.toString();
            }
        };
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
                newPassowrd = editable.toString();
            }
        };
    }


    public TextWatcher getConfirmPasswordTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                confirmNewPassword = editable.toString();
            }
        };
    }

    private String checkFormValidations() {

        if (TextUtils.isEmpty(currentPassword)) {
            return getStringFromVM(R.string.error_current_password_empty);
        }

        if (currentPassword.length() < 6) {
            return getStringFromVM(R.string.error_current_password_length);
        }

        if (TextUtils.isEmpty(newPassowrd)) {
            return getStringFromVM(R.string.error_password_empty);
        }

        if (newPassowrd.length() < 6) {
            return getStringFromVM(R.string.error_password_length);
        }

        if (TextUtils.isEmpty(confirmNewPassword)) {
            return getStringFromVM(R.string.error_confirm_password_empty);
        }

        if (confirmNewPassword.length() < 6) {
            return getStringFromVM(R.string.error_confirm_password_length);
        }

        if (!newPassowrd.equalsIgnoreCase(confirmNewPassword)) {
            return getStringFromVM(R.string.error_password_not_matched);
        }

        return "";
    }

    public void onSubmitClicked() {
        String formValidationResult = checkFormValidations();
        if (formValidationResult.equalsIgnoreCase("")) {
            requestResultInterface.onSucess("SubmitClicked");
        } else {
            requestResultInterface.onRequestRequirementFail(formValidationResult);
        }
    }


    public void attemptChangePassword() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("old_password", currentPassword);
            jsonObject.put("new_password", newPassowrd);

            NetworkConn networkConn = NetworkConn.getInstance();
            networkConn.makeRequest(networkConn.createPutRequest(AppConstants.CHANGE_PASSWORD_URL, jsonObject.toString()), "changePassword", onRequestResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}