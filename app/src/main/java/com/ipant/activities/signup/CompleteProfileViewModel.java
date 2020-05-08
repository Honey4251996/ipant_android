package com.ipant.activities.signup;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;

import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.CustomAndroidViewModel;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;

import org.json.JSONObject;

public class CompleteProfileViewModel extends CustomAndroidViewModel {
    private NetworkConn.onRequestResponse onRequestResponse;
    private RequestResultInterface requestResultInterface;
    private String firstName = "", lastName = "", email = "", password = "", confirmPassword = "", mobileNumber = "",countryCode = "";
    public boolean privacyCheck = false;


    public CompleteProfileViewModel(@NonNull Application application, RequestResultInterface requestResultInterface, NetworkConn.onRequestResponse onRequestResponse) {
        super(application);
        this.onRequestResponse = onRequestResponse;
        this.requestResultInterface = requestResultInterface;
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

    // changes done by vikash

    public TextWatcher getCountryCodeTextWatcher() {
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
                countryCode = editable.toString();
            }
        };
    }

    public TextWatcher getFirstNameTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                firstName = editable.toString();
            }
        };
    }


    public TextWatcher getLastNameTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                lastName = editable.toString();
            }
        };
    }

    public TextWatcher getEmailTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                email = editable.toString().trim();
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
                password = editable.toString();
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
                confirmPassword = editable.toString();
            }
        };
    }


    private String checkFormValidations() {
        if (TextUtils.isEmpty(firstName)) {
            return getStringFromVM(R.string.error_first_name_empty);
        }

        if (TextUtils.isEmpty(lastName)) {
            return getStringFromVM(R.string.error_last_name_empty);
        }

        if (countryCode.equalsIgnoreCase("")) {
            return getStringFromVM(R.string.country_code_empty);
        }


        if (mobileNumber.equalsIgnoreCase("")) {
            return getStringFromVM(R.string.error_mobile_number_empty);
        }

        if (mobileNumber.length() < 9) {
            return getStringFromVM(R.string.error_mobile_number_length);
        }

        if (mobileNumber.startsWith("000") || mobileNumber.startsWith("00") || mobileNumber.startsWith("0  ")) {
            return getStringFromVM(R.string.error_invalid_mobile_number);
        }

        if (!Patterns.PHONE.matcher(mobileNumber).matches()) {
            return getStringFromVM(R.string.error_invalid_mobile_number);
        }


        if (!email.trim().equalsIgnoreCase("")) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                return getStringFromVM(R.string.error_email_not_valid);
            }
        }

     /*   if (TextUtils.isEmpty(email)) {
            return getStringFromVM(R.string.error_email_empty);
        }*/


        if (TextUtils.isEmpty(password)) {
            return getStringFromVM(R.string.error_password_empty);
        }

        if (password.length() < 6) {
            return getStringFromVM(R.string.error_password_length);
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            return getStringFromVM(R.string.error_confirm_password_empty);
        }

        if (confirmPassword.length() < 6) {
            return getStringFromVM(R.string.error_confirm_password_length);
        }

        if (!password.equalsIgnoreCase(confirmPassword)) {
            return getStringFromVM(R.string.error_password_not_matched);
        }

        if (!privacyCheck) {
            return getStringFromVM(R.string.error_privacy_policy_uncheck);
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

    public void attemptSignUpWithFullDetails() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("firstname", firstName);
            jsonObject.put("lastname", lastName);
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            jsonObject.put("mobile_no", mobileNumber);

            // added file by vikash
            jsonObject.put("country_code", countryCode);


            NetworkConn networkConn = NetworkConn.getInstance();
            networkConn.makeRequest(networkConn.createPostRequest(AppConstants.COMPLETE_SIGN_UP_URL, jsonObject.toString()), "CompleteSignUp", onRequestResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
