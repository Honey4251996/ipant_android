package com.ipant.activities.mycards;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.ipant.network_communication.NetworkConn;

public class MyCardViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {
    private NetworkConn.onRequestResponse onRequestResponse;
    private Application application;

    public MyCardViewModelFactory(Application application, NetworkConn.onRequestResponse onRequestResponse) {
        super(application);
        this.application = application;
        this.onRequestResponse = onRequestResponse;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MyCardViewModel(application, onRequestResponse);
    }
}
