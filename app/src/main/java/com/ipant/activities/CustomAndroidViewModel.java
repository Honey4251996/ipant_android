package com.ipant.activities;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

public class CustomAndroidViewModel  extends AndroidViewModel {



    public CustomAndroidViewModel(@NonNull Application application) {
        super(application);
    }

    public String getStringFromVM(int stringId){
     return getApplication().getResources().getString(stringId);
    }

}
