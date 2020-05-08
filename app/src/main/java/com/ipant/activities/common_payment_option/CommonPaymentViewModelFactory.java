package com.ipant.activities.common_payment_option;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.ipant.RequestResultInterface;
import com.ipant.network_communication.NetworkConn;

public class CommonPaymentViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {
    private NetworkConn.onRequestResponse onRequestResponse;
    private RequestResultInterface requestResultInterface;
    private Application application;


    public CommonPaymentViewModelFactory(Application application, RequestResultInterface requestResultInterface, NetworkConn.onRequestResponse onRequestResponse) {
        super(application);
        this.application=application;
        this.requestResultInterface=requestResultInterface;
        this.onRequestResponse=onRequestResponse;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CommonPaymentViewModel(application, requestResultInterface,onRequestResponse);
    }
}

