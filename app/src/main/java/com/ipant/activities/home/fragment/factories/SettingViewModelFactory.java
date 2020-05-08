package com.ipant.activities.home.fragment.factories;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.ipant.RequestResultInterface;
import com.ipant.activities.home.fragment.viewmodels.SettingViewModel;
import com.ipant.network_communication.NetworkConn;

public class SettingViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private NetworkConn.onRequestResponse onRequestResponse;

    private Application application;
    private RequestResultInterface requestResultInterface;

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link AndroidViewModel}
     */
    public SettingViewModelFactory(@NonNull Application application, NetworkConn.onRequestResponse onRequestResponse, RequestResultInterface requestResultInterface) {
        super(application);
        this.application = application;
        this.onRequestResponse = onRequestResponse;
        this.requestResultInterface=requestResultInterface;

    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SettingViewModel(application,  onRequestResponse, requestResultInterface);
    }

}
