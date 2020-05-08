package com.ipant.activities.home.fragment.factories;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.ipant.RequestResultInterface;
import com.ipant.activities.home.fragment.viewmodels.MyAccountViewModel;
import com.ipant.network_communication.NetworkConn;

public class MyAccountViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {
    private NetworkConn.onRequestResponse onRequestResponse;
    private RequestResultInterface requestResultInterface;
    private Application application;

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link AndroidViewModel}
     */
    public MyAccountViewModelFactory(@NonNull Application application, RequestResultInterface requestResultInterface,NetworkConn.onRequestResponse onRequestResponse) {
        super(application);
        this.application=application;
        this.requestResultInterface=requestResultInterface;
        this.onRequestResponse=onRequestResponse;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MyAccountViewModel(application, requestResultInterface, onRequestResponse);
    }
}
