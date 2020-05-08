package com.ipant.activities.add_money;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.ipant.RequestResultInterface;

public class AddMoneyViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {
    private RequestResultInterface requestResultInterface;
    private Application application;

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link AndroidViewModel}
     */
    public AddMoneyViewModelFactory(@NonNull Application application, RequestResultInterface requestResultInterface) {
        super(application);
        this.application=application;
        this.requestResultInterface=requestResultInterface;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddMoneyViewModel(application,   requestResultInterface);
    }
}
