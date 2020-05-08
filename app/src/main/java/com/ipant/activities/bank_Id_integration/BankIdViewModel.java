package com.ipant.activities.bank_Id_integration;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.CustomAndroidViewModel;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.BankIdConfig;

import org.json.JSONObject;

import java.net.URLEncoder;

import okhttp3.Request;
import okhttp3.RequestBody;

import static com.ipant.network_communication.NetworkConn.JSON;

public class BankIdViewModel extends CustomAndroidViewModel {
    private NetworkConn.onRequestResponse onRequestResponse;
    private RequestResultInterface requestResultInterface;

    private String personalIdNumber = "", orderRef = "", collectUrl = "";


    public BankIdViewModel(@NonNull Application application, RequestResultInterface requestResultInterface, NetworkConn.onRequestResponse onRequestResponse) {
        super(application);
        this.requestResultInterface = requestResultInterface;
        this.onRequestResponse = onRequestResponse;
    }


    public void setPersonalIdNumber(String personalIdNumber) {
        this.personalIdNumber = personalIdNumber;
    }

    public void setOrderRef(String orderRef) {
        this.orderRef = orderRef;
    }

    public void setCollectUrl(String collectUrl) {
        this.collectUrl = collectUrl;
    }


    public TextWatcher getPersonalIdNumber() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                personalIdNumber = editable.toString();
            }
        };
    }

    private String checkBankIdValidation() {
        if (personalIdNumber.equalsIgnoreCase("")) {
            return getStringFromVM(R.string.error_enter_personal_id_number);
        }

        if (personalIdNumber.length() < 12) {
            return getStringFromVM(R.string.error_personal_id_number_length);
        }
        return "";
    }


    public void onSubmitClicked() {
        String formMessage = checkBankIdValidation();

        if (formMessage.equalsIgnoreCase("")) {
            requestResultInterface.onSucess("startAuthentication");
        } else {
            requestResultInterface.onRequestRequirementFail(formMessage);
        }


    }

    public void attemptBankIdAuthentication() {


        NetworkConn networkConn = NetworkConn.getInstance();
        networkConn.makeBankRequest(getApplication(), makeBankIdRequest(), "callSigniCateApi", onRequestResponse);

    }


    public Request makeBankIdRequest() {

        Request request = null;


        try {

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("subject", personalIdNumber);
            jsonObject.put("apiKey", BankIdConfig.SIGNICAT_API_KEY);


            String target = URLEncoder.encode(BankIdConfig.TARGET, "UTF-8");

            String url = BankIdConfig.RP_AUTH_URL + "&target=" + target;

            Log.e("Final Url", url);

            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-type", "application/json")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return request;

    }


    public void startCollectCall() {

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("orderRef", orderRef);
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(collectUrl)
                    .post(body)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-type", "application/json")
                    .build();

            NetworkConn networkConn = NetworkConn.getInstance();
            networkConn.makeBankRequest(getApplication(), request, "startCollectCall", onRequestResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
