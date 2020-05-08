package com.ipant.activities.home.fragment.factories;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.ipant.activities.home.fragment.viewmodels.CommonWebViewViewModel;
import com.ipant.network_communication.NetworkConn;

public class CommonWebViewViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private NetworkConn.onRequestResponse onRequestResponse;

    private Application application;

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link AndroidViewModel}
     */
    public CommonWebViewViewModelFactory(@NonNull Application application, NetworkConn.onRequestResponse onRequestResponse) {
        super(application);
        this.application = application;
        this.onRequestResponse = onRequestResponse;

    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CommonWebViewViewModel(application,  onRequestResponse);
    }

}
