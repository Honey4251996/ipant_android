package com.ipant.activities.splash;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.BaseActivity;
import com.ipant.activities.get_started.GetStartedActivity;
import com.ipant.activities.home.HomeActivity;
import com.ipant.activities.landing_page.LandingActivity;
import com.ipant.activities.login.LoginActivity;
import com.ipant.activities.login.bean.UserDataBean;
import com.ipant.databinding.ActivitySplashBinding;
import com.ipant.utils.AppConstants;
import com.ipant.utils.AppDialogs;
import com.ipant.utils.App_Preferences;
import com.ipant.utils.LocaleManager;
import com.ipant.utils.iPantApp;

public class SplashActivity extends BaseActivity implements RequestResultInterface, AppDialogs.CommonDialogCallback {
    private SplashViewModel splashViewModel;
    private ActivitySplashBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        Animation an2= AnimationUtils.loadAnimation(this,R.anim.bounce);
        mBinding.spLogo.startAnimation(an2);
        splashViewModel = ViewModelProviders.of(this, new SplashViewModelFactory(getApplication(), this)).get(SplashViewModel.class);
        AppConstants.USER_DATA_BEAN_OBJ = new Gson().fromJson(App_Preferences.loadStringPref(getApplication(), AppConstants.KEY_USER_DATA), UserDataBean.class);
        generateFcmToken();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_splash;
    }

    @Override
    protected Toolbar getToolbar() {
        mBinding=(ActivitySplashBinding) getBindingObject();
        return null;
    }

    @Override
    protected boolean toolbarHomeEnable() {
        return false;
    }

    @Override
    protected boolean toolbarTitleEnable() {
        return false;
    }

    @Override
    public void startNewActivity(Bundle bundle, Class newclass) {
        if (newclass == LoginActivity.class) {
            boolean getStartedCompletedStatus = App_Preferences.loadBooleanPref(SplashActivity.this, AppConstants.KEY_GET_STARTED_COMPLETED);

            if (getStartedCompletedStatus == true) {
                startActivity(LandingActivity.getInstance(this));
            } else {
                startActivity(GetStartedActivity.getInstance(this));
            }

            finish();
        } else {
            startActivity(HomeActivity.getInstance(this));
            finish();
        }

    }

    @Override
    public void onSucess(String eventType) {

    }

    @Override
    public void onRequestRequirementFail(String failireMsg) {

    }


    private void generateFcmToken() {

        if (iPantApp.checkNetworkConnectivity()) {

            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SplashActivity.this, new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    AppConstants.FCM_TOKEN = instanceIdResult.getToken();
                    splashViewModel.processIntentWithHandler();
                }
            });
        } else {
            AppDialogs.getInstance().commonAlertDialog(this, getAppString(R.string.error_no_internet_connection), "", this);
        }

    }

    @Override
    public void onDialogEvent(Dialog dialog, String event) {
        dialog.dismiss();
        finishAffinity();

    }

}
