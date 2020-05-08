package com.ipant.activities.login;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.BaseActivity;
import com.ipant.activities.home.HomeActivity;
import com.ipant.activities.landing_page.LandingActivity;
import com.ipant.activities.login.bean.UserDataBean;
import com.ipant.activities.signup.VerificationActivity;
import com.ipant.activities.signup.beans.SignUpBean;
import com.ipant.databinding.ActivityLoginBinding;
import com.ipant.databinding.DialogForgetPasswordBinding;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;
import com.ipant.utils.AppDialogs;
import com.ipant.utils.AppUtil;
import com.ipant.utils.App_Preferences;

public class LoginActivity extends BaseActivity implements NetworkConn.onRequestResponse, RequestResultInterface {
    private ActivityLoginBinding mBinding;
    private LoginViewModel loginViewModel;
    private Dialog loadingDialog, forgotPasswordDialog;
    private ForgotPasswordViewModel forgotPasswordViewModel;
    private DialogForgetPasswordBinding dialogBinding;

    public static Intent getInstance(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory(getApplication(), this, this)).get(LoginViewModel.class);
        mBinding.setViewModel(loginViewModel);

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected Toolbar getToolbar() {
        mBinding = (ActivityLoginBinding) getBindingObject();
        mBinding.layoutAppbar.toolbarTitle.setText(getAppString(R.string.txt_login));
        mBinding.layoutAppbar.toolbarView.setNavigationIcon(R.drawable.ic_colored_back);
        return mBinding.layoutAppbar.toolbarView;
    }

    @Override
    protected boolean toolbarHomeEnable() {
        return true;
    }

    @Override
    protected boolean toolbarTitleEnable() {
        return false;
    }

    public void dialogForgetPassword(View view) {
        appUtil.hideKeyboard(this);
        mBinding.edtNumber.clearFocus();
        mBinding.edtPassword.clearFocus();

      /*  Bundle bundle = new Bundle();
        bundle.putInt("varification_type", 1);
        startNewActivity(bundle, SignUpActivity.class);*/

        forgotPasswordDialog = new Dialog(this);
        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_forget_password, null, false);
        forgotPasswordViewModel = ViewModelProviders.of(this, new ForgotPasswordViewModelFactory(getApplication(), this, this)).get(ForgotPasswordViewModel.class);
        dialogBinding.setViewModel(forgotPasswordViewModel);
        forgotPasswordDialog.setContentView(dialogBinding.getRoot());
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        forgotPasswordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        forgotPasswordDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        forgotPasswordDialog.show();
    }

    public void dismissForgotPasswordDialog(View view) {
        AppUtil.getInstance().hideKeyboard(this);

        forgotPasswordDialog.dismiss();
    }


    @Override
    public void onResponse(String response, String eventType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppDialogs.getInstance().hideLoader(loadingDialog);
                if (eventType.equalsIgnoreCase("LoginResponse")) {
                    UserDataBean userDataBean = new Gson().fromJson(response, UserDataBean.class);

                    if (userDataBean.getData().get(0).getAuthorization().length() > 0) {
                        AppConstants.USER_DATA_BEAN_OBJ = userDataBean;
                        App_Preferences.saveStringPref(getApplication(), AppConstants.KEY_USER_DATA, response);
                        startNewActivity(null, HomeActivity.class);
                    }
                } else {
                    if (eventType.equalsIgnoreCase("ForgotPassword")) {
                        SignUpBean signUpBean = new Gson().fromJson(response.toString(), SignUpBean.class);

                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", signUpBean.getData().get(0).getUserId());
                        bundle.putString("mobile_no", forgotPasswordViewModel.getMobileNumber());
                        bundle.putInt("varification_type", 1);
                        startNewActivity(bundle, VerificationActivity.class);
                    }
                }
            }
        });


    }


    @Override
    public void onBackPressed() {
        AppUtil.getInstance().hideKeyboard(this);
        startActivity(LandingActivity.getInstance(this));
        finish();
    }

    @Override
    public void onNoNetworkConnectivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppDialogs.getInstance().hideLoader(loadingDialog);
                noNetworkConnectionDialog();
            }
        });
    }

    @Override
    public void onRequestRetry() {

    }

    @Override
    public void onRequestFailed(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppDialogs.getInstance().hideLoader(loadingDialog);
                appUtil.showMessageError(LoginActivity.this, mBinding.coordinatorLayout, msg);
            }
        });
    }

    @Override
    public void onSessionExpire() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppDialogs.getInstance().hideLoader(loadingDialog);
                sessionExpireError();
            }
        });

    }

    @Override
    public void onAppHardUpdate() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppDialogs.getInstance().hideLoader(loadingDialog);
                updateAppVersion();
            }
        });

    }

    @Override
    public void startNewActivity(Bundle bundle, Class newClass) {
        mBinding.edtNumber.clearFocus();
        mBinding.edtPassword.clearFocus();
        appUtil.hideKeyboard(this);
        enableDisableViews(false);
        if (newClass == VerificationActivity.class) {
            startActivity(VerificationActivity.getInstance(this, bundle != null ? bundle : null));
        } else {
            Intent intent = HomeActivity.getInstance(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        enableDisableViews(true);

    }

    @Override
    public void onSucess(String eventType) {
        enableDisableViews(false);


        if (eventType.equalsIgnoreCase("Login")) {
            loadingDialog = AppDialogs.getInstance().showLoader(this);
            appUtil.hideKeyboard(this);
            mBinding.edtNumber.clearFocus();
            mBinding.edtPassword.clearFocus();
            loginViewModel.attemptLoginRequest();
        } else {
            mBinding.edtNumber.clearFocus();
            mBinding.edtPassword.clearFocus();
            forgotPasswordDialog.dismiss();
            appUtil.hideKeyboard(this);
            loadingDialog = AppDialogs.getInstance().showLoader(this);
            forgotPasswordViewModel.attemptForgotPasswordRequest();
        }

        enableDisableViews(true);

    }

    @Override
    public void onRequestRequirementFail(String failureMsg) {
        appUtil.hideKeyboard(this);
        mBinding.edtNumber.clearFocus();
        mBinding.edtPassword.clearFocus();
        appUtil.showMessageError(LoginActivity.this, mBinding.coordinatorLayout, failureMsg);
    }

    private void enableDisableViews(boolean status) {
        mBinding.btnSubmit.setEnabled(status);
        mBinding.btnSignUpNow.setEnabled(status);
    }
}
