package com.ipant.activities.signup;

import android.app.Application;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;

import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.CustomAndroidViewModel;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;

import org.json.JSONObject;

public class VerificationViewModel extends CustomAndroidViewModel {
    private NetworkConn.onRequestResponse onRequestResponse;
    private RequestResultInterface requestResultInterface;
    private String otp;
    private String userId = "";
    private String mobile_no = "";
    private String country_code = "";
    private int varification_type=0;

    public VerificationViewModel(Application application, RequestResultInterface requestResultInterface, NetworkConn.onRequestResponse onRequestResponse) {
        super(application);
        this.onRequestResponse = onRequestResponse;
        this.requestResultInterface = requestResultInterface;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public void setVarification_type(int varification_type) {
        this.varification_type = varification_type;
    }

    public TextWatcher getOtpTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                otp = editable.toString();
            }
        };
    }

    public String checkValidRequirement() {
        if (TextUtils.isEmpty(otp)) {
            return getStringFromVM(R.string.error_otp_empty);
        }

        if (otp.length() < 6) {
            return getStringFromVM(R.string.error_otp_length);
        }
        return "";
    }


    public void onVerifyClicked() {
        if (checkValidRequirement().equalsIgnoreCase("")) {
            requestResultInterface.onSucess("verify");
        } else {
            requestResultInterface.onRequestRequirementFail(checkValidRequirement());
        }


    }

    public void resendVerificationClicked(){
        requestResultInterface.onSucess("resendOtp");
    }

    public void attemptVerification() {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", userId);
            jsonObject.put("otp", otp);
            jsonObject.put("varification_type", varification_type);


            Log.e("Verification COde", jsonObject.toString());

            NetworkConn networkConn = NetworkConn.getInstance();
            networkConn.makeRequest(networkConn.createPostRequest(AppConstants.OTP_VERIFICATION, jsonObject.toString()), "OtpVerfication", onRequestResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void attemptResendOtp() {
        try {
            JSONObject jsonObject = new JSONObject();
            //jsonObject.put("country_code", country_code);
            jsonObject.put("mobile_no", mobile_no);
            jsonObject.put("varification_type", varification_type);

            NetworkConn networkConn = NetworkConn.getInstance();
            networkConn.makeRequest(networkConn.createPostRequest(AppConstants.USER_RESEND_OTP_URL, jsonObject.toString()), "ResendOtp", onRequestResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
