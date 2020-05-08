package com.ipant.activities.add_money;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;

import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.CustomAndroidViewModel;
import com.ipant.activities.common_payment_option.CommonPaymentOptionActivity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class AddMoneyViewModel extends CustomAndroidViewModel {
    private RequestResultInterface requestResultInterface;
    private DecimalFormat df;

    private String amount = "";

    public AddMoneyViewModel(@NonNull Application application, RequestResultInterface requestResultInterface) {
        super(application);
        this.requestResultInterface = requestResultInterface;
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
        return "";
    }

    public void onAddMoneyClicked() {
        String getFormMsg = isFormValid();

        if (!getFormMsg.equalsIgnoreCase("")) {
            requestResultInterface.onRequestRequirementFail(getFormMsg);
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("amount", amount);
        bundle.putString("requestType", getStringFromVM(R.string.txt_add_money));
        bundle.putInt("moneyRequestType", 0);
        requestResultInterface.startNewActivity(bundle, CommonPaymentOptionActivity.class);

    }
}
