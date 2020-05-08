package com.ipant.activities.withdraw_money;

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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class WithdrawActivityViewModel extends CustomAndroidViewModel {
    private NetworkConn.onRequestResponse onRequestResponse;
    private RequestResultInterface requestResultInterface;
    private DecimalFormat df;


    private String amount = "";
    private String comment = "";
    private String accountHolderName = "";
    private String accountNumber = "";
    private String sortCode = "";
    private String bankName = "";

    public WithdrawActivityViewModel(@NonNull Application application, RequestResultInterface requestResultInterface, NetworkConn.onRequestResponse onRequestResponse) {
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

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public TextWatcher getAccountHolderNameTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                accountHolderName = editable.toString();
            }
        };
    }

    public TextWatcher getAccountNumberTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                accountNumber = editable.toString();
            }
        };
    }

    public TextWatcher getSortCodeTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                sortCode = editable.toString();
            }
        };
    }

    public TextWatcher getAmountTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                comment = editable.toString();
            }
        };
    }

    private String checkFormValidMsg() {

        if (TextUtils.isEmpty(bankName.trim())) {
            return getStringFromVM(R.string.error_select_bank);
        }

        if (TextUtils.isEmpty(accountHolderName)) {
            return getStringFromVM(R.string.error_enter_account_holder_name);
        }

        if (TextUtils.isEmpty(accountNumber)) {
            return getStringFromVM(R.string.error_enter_account_number);
        }

        if (TextUtils.isEmpty(sortCode)) {
            return getStringFromVM(R.string.error_enter_short_code);
        }

        if (accountNumber.length() < 11) {
            return getStringFromVM(R.string.error_invalid_account_number);
        }


        if (TextUtils.isEmpty(amount)) {
            return getStringFromVM(R.string.error_enter_amount);
        }

        if (Float.parseFloat(amount) == 0) {
            return getStringFromVM(R.string.error_invalid_amount);
        }
        return "";


    }

    public void onWithdrawMoneyClicked() {
        String msg = checkFormValidMsg();

        if (msg.equalsIgnoreCase("")) {
            requestResultInterface.onSucess("attemptWithdrawMoney");
        } else {
            requestResultInterface.onRequestRequirementFail(msg);
        }
    }

    public void attemptWithdrawMoney() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("withdraw_money_method", 3);
            jsonObject.put("amount", amount);
            jsonObject.put("charge", 0);
            jsonObject.put("bank_name", bankName);
            jsonObject.put("acc_number", accountNumber);
            jsonObject.put("acc_holder_name", accountHolderName);
            jsonObject.put("comment", comment);
            jsonObject.put("sort_code", sortCode);

            NetworkConn networkConn = NetworkConn.getInstance();
            networkConn.makeRequest(networkConn.createPostRequest(AppConstants.WITHDRAW_MONEY_URL, jsonObject.toString()), "withdrawMoneyEvent", onRequestResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
