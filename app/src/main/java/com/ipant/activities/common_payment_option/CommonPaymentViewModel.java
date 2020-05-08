package com.ipant.activities.common_payment_option;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.CustomAndroidViewModel;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;

import org.json.JSONObject;

public class CommonPaymentViewModel extends CustomAndroidViewModel {
    private RequestResultInterface requestResultInterface;
    private NetworkConn.onRequestResponse onRequestResponse;
    private String month = "", year = "";
    private int count = 0;
    private int selectedPaymentOption = 1;
    private MutableLiveData<String> cardNumber = new MutableLiveData<>();

    private String cvv = "", mobileNumberOrEmail = "", amount = "", comment = "", charge = "0", checkValue = "0", cardId = "", useMYWallet = "1";


    private int requestType = 0;

    public CommonPaymentViewModel(Application application, RequestResultInterface requestResultInterface, NetworkConn.onRequestResponse onRequestResponse) {
        super(application);
        this.requestResultInterface = requestResultInterface;
        this.onRequestResponse = onRequestResponse;
        cardNumber.setValue("");
    }

    public void setSelectedPaymentOption(int selectedPaymentOption) {
        this.selectedPaymentOption = selectedPaymentOption;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setMobileNumberOrEmail(String mobileNumberOrEmail) {
        this.mobileNumberOrEmail = mobileNumberOrEmail;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public void setCheckValue(boolean newCheckState) {
        if (newCheckState) {
            this.checkValue = "1";
        } else {
            this.checkValue = "0";
        }
    }

    public void setUseMyWallet(boolean newCheckState) {
        if (newCheckState) {
            this.useMYWallet = "1";
        } else {
            this.useMYWallet = "0";
        }
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public MutableLiveData<String> getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String value) {
        cardNumber.setValue(value);
    }

    public void showPicker() {
        requestResultInterface.onSucess("Picker");
    }


    public void newPaymentOptionSelected(int selectedPaymentOption) {
        requestResultInterface.onSucess(String.valueOf(selectedPaymentOption));
    }

    public TextWatcher getCardNumberTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                cardNumber.setValue(editable.toString());
            }
        };
    }

    public TextWatcher getCVVTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                cvv = editable.toString();
            }
        };
    }


    private String checkSendMoneyRequirement() {

        if (selectedPaymentOption == 5) {
            return "";
        } else if (selectedPaymentOption == 1 || (selectedPaymentOption == 2)) {

            if (cardNumber.getValue() == null || TextUtils.isEmpty(cardNumber.getValue())) {
                return getStringFromVM(R.string.error_enter_card_msg);
            }

            if (cardNumber.getValue().length() < 19) {
                return getStringFromVM(R.string.error_invalid_card_number);
            }

            if (cardNumber.getValue().replace(" ", "").equalsIgnoreCase("0000000000000000")) {
                return getStringFromVM(R.string.error_invalid_card_number);
            }

            if (TextUtils.isEmpty(month)) {
                return getStringFromVM(R.string.error_select_card_expiry_month_year);
            }
        }

        if (TextUtils.isEmpty(cvv.trim())) {
            return getStringFromVM(R.string.error_enter_cvv);
        }

        if (cvv.trim().length() < 3) {
            return getStringFromVM(R.string.error_invalid_cvv);
        }

        return "";

    }

    private String checkAddMoneyRequirement() {
        if (selectedPaymentOption == 1 || (selectedPaymentOption == 2)) {

            if (cardNumber.getValue() == null || TextUtils.isEmpty(cardNumber.getValue())) {
                return getStringFromVM(R.string.error_enter_card_msg);
            }

            if (cardNumber.getValue().length() < 19) {
                return getStringFromVM(R.string.error_invalid_card_number);
            }

            if (cardNumber.getValue().replace(" ", "").equalsIgnoreCase("0000000000000000")) {
                return getStringFromVM(R.string.error_invalid_card_number);
            }

            if (TextUtils.isEmpty(month)) {
                return getStringFromVM(R.string.error_select_card_expiry_month_year);
            }
        }

        if (selectedPaymentOption == 4) {
            if (cardId.equalsIgnoreCase("")) {
                return getStringFromVM(R.string.error_please_select_card);
            }
        }

        if (TextUtils.isEmpty(cvv.trim())) {
            return getStringFromVM(R.string.error_enter_cvv);
        }

        if (cvv.trim().length() < 3) {
            return getStringFromVM(R.string.error_invalid_cvv);
        }

        return "";
    }


    public void attemptSendMoney() {

        try {
            JSONObject jsonBody = new JSONObject();

            //Common and Wallet Payment Required Parameters
            jsonBody.put("send_money_method", String.valueOf(selectedPaymentOption));
            jsonBody.put("mobile_email", mobileNumberOrEmail);
            jsonBody.put("amount", amount);
            jsonBody.put("comment", comment);
            jsonBody.put("charge", charge);
            jsonBody.put("use_my_wallet", useMYWallet);

            if (selectedPaymentOption == 1 || (selectedPaymentOption == 2)) {

                // Card Payment Required Parameters
                jsonBody.put("security_code", cvv);
                jsonBody.put("card_number", cardNumber.getValue().replace(" ", ""));
                jsonBody.put("expiry_month", month);
                jsonBody.put("expiry_year", year);
                jsonBody.put("save_card_check", checkValue);

            } else if (selectedPaymentOption == 4) {

                // Saved Card PaymentOption Required Parameters
                jsonBody.put("security_code", cvv);
                jsonBody.put("save_card_id", cardId);
            }
            NetworkConn networkConn = NetworkConn.getInstance();
            networkConn.makeRequest(networkConn.createPostRequest(AppConstants.SEND_MONEY_URL, jsonBody.toString()), "sendMoney", onRequestResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void attemptAddMoney() {


        try {
            JSONObject jsonBody = new JSONObject();

            //Common Required Parameters
            jsonBody.put("add_money_method", String.valueOf(selectedPaymentOption));
            jsonBody.put("amount", amount);
            jsonBody.put("charge", charge);

            if (selectedPaymentOption == 1 || (selectedPaymentOption == 2)) {

                // Card Payment Required Parameters
                jsonBody.put("security_code", cvv);
                jsonBody.put("card_number", cardNumber.getValue().replace(" ", ""));
                jsonBody.put("expiry_month", month);
                jsonBody.put("expiry_year", year);
                jsonBody.put("save_card_check", checkValue);

            } else if (selectedPaymentOption == 4) {

                // Saved Card PaymentOption Required Parameters
                jsonBody.put("security_code", cvv);
                jsonBody.put("save_card_id", cardId);
            }

            NetworkConn networkConn = NetworkConn.getInstance();
            networkConn.makeRequest(networkConn.createPostRequest(AppConstants.ADD_MONEY_URL, jsonBody.toString()), "addMoney", onRequestResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void onProceedSecurelyClicked() {
        if (requestType == 0) {
            String formValidationMsg = checkAddMoneyRequirement();
            if (!formValidationMsg.equalsIgnoreCase("")) {
                requestResultInterface.onRequestRequirementFail(formValidationMsg);
                return;
            }
            requestResultInterface.onSucess("ProceedAddMoney");
        } else if (requestType == 1) {
            String formValidationMsg = checkSendMoneyRequirement();
            if (!formValidationMsg.equalsIgnoreCase("")) {
                requestResultInterface.onRequestRequirementFail(formValidationMsg);
                return;
            }
            requestResultInterface.onSucess("ProceedSendMoney");
        }
    }


    public void getCardList() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("page_no", 0);

            NetworkConn networkConn = NetworkConn.getInstance();
            networkConn.makeRequest(networkConn.createPostRequest(AppConstants.GET_CARD_DETAILS, jsonObject.toString()), "cardDetails", onRequestResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
