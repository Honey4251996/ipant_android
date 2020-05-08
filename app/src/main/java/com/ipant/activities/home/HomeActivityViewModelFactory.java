package com.ipant.activities.home;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.ipant.network_communication.NetworkConn;

public class HomeActivityViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {
    private NetworkConn.onRequestResponse onRequestResponse;
    private Application application;
    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link AndroidViewModel}
     */
    public HomeActivityViewModelFactory(@NonNull Application application, NetworkConn.onRequestResponse onRequestResponse) {
        super(application);
        this.application=application;
        this.onRequestResponse=onRequestResponse;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new HomeActivityViewModel(application, onRequestResponse);
    }
}
