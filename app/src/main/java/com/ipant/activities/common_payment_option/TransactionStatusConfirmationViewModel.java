package com.ipant.activities.common_payment_option;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.ipant.RequestResultInterface;
import com.ipant.activities.CustomAndroidViewModel;
import com.ipant.activities.common_payment_option.beans.TransactionDetails;
import com.ipant.activities.home.HomeActivity;

public class TransactionStatusConfirmationViewModel extends CustomAndroidViewModel {
    private RequestResultInterface requestResultInterface;


    private MutableLiveData<TransactionDetails> mutuableTrasactionBean = new MutableLiveData<>();

    public TransactionStatusConfirmationViewModel(@NonNull Application application, RequestResultInterface requestResultInterface) {
        super(application);
        this.requestResultInterface=requestResultInterface;
    }

    public MutableLiveData<TransactionDetails> getMutuableTrasactionBean() {
        return mutuableTrasactionBean;
    }

    public void setMutuableTrasactionBean(TransactionDetails trasactionBean) {

        if (trasactionBean == null) {
            trasactionBean = new TransactionDetails();
        }
        this.mutuableTrasactionBean.setValue(trasactionBean);
    }


    public void onDoneClicked() {

        requestResultInterface.startNewActivity(null, HomeActivity.class);

    }


}
