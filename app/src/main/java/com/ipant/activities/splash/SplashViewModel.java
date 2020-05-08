package com.ipant.activities.splash;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.ipant.RequestResultInterface;
import com.ipant.activities.home.HomeActivity;
import com.ipant.activities.login.LoginActivity;
import com.ipant.utils.AppConstants;

public class SplashViewModel extends AndroidViewModel {
    private RequestResultInterface performTaskInterface;


    public SplashViewModel(@NonNull Application application, RequestResultInterface performTaskInterface) {
        super(application);
        this.performTaskInterface = performTaskInterface;
    }


    protected void processIntentWithHandler() {
        new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run() {

                                          if (AppConstants.USER_DATA_BEAN_OBJ != null && AppConstants.USER_DATA_BEAN_OBJ.getData().get(0).getAuthorization().length() > 0) {
                                              performTaskInterface.startNewActivity(null, HomeActivity.class);
                                              return;
                                          }
                                          performTaskInterface.startNewActivity(null, LoginActivity.class);
                                      }
                                  },
                3000);
    }
}
