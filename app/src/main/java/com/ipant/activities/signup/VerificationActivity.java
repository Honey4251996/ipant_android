package com.ipant.activities.signup;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.gson.Gson;
import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.BaseActivity;
import com.ipant.activities.landing_page.LandingActivity;
import com.ipant.activities.reset_password.ResetPasswordActivity;
import com.ipant.activities.signup.beans.SignUpBean;
import com.ipant.databinding.ActivityVerificationBinding;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppDialogs;

public class VerificationActivity extends BaseActivity implements RequestResultInterface, NetworkConn.onRequestResponse, AppDialogs.CommonDialogCallback {
    private ActivityVerificationBinding mBinding;
    private VerificationViewModel verificationViewModel;
    private Dialog dialogLoader;

    private int varification_type = 0;


    public static Intent getInstance(Context context, Bundle bundle) {
        Intent intent = new Intent(context, VerificationActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        verificationViewModel = ViewModelProviders.of(this, new VerificationViewModelFactory(getApplication(), this, this)).get(VerificationViewModel.class);
        mBinding.setViewModel(verificationViewModel);
        getData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_verification;
    }

    @Override
    protected Toolbar getToolbar() {
        mBinding = (ActivityVerificationBinding) getBindingObject();
        mBinding.layoutAppbar.toolbarTitle.setText(getAppString(R.string.txt_enter_verification_code));
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

    @Override
    public void startNewActivity(Bundle bundle, Class newClass) {

        if (newClass == LandingActivity.class) {
            Intent intent = LandingActivity.getInstance(this);
            startActivity(intent);
            finish();
        } else {
            if (newClass == ResetPasswordActivity.class) {
                Intent intent = ResetPasswordActivity.getInstance(VerificationActivity.this, bundle != null ? bundle : null);
                startActivity(intent);
                finish();
            }

        }

    }

    @Override
    public void onSucess(String eventType) {
        enableDisableViews(false);
        appUtil.hideKeyboard(this);
        mBinding.edtNumber.clearFocus();
        dialogLoader = AppDialogs.getInstance().showLoader(this);
        if (eventType.equalsIgnoreCase("resendOtp")) {
            verificationViewModel.attemptResendOtp();
        } else {
            if (eventType.equalsIgnoreCase("verify")) {
                verificationViewModel.attemptVerification();
            }
        }
        enableDisableViews(true);

    }

    @Override
    public void onRequestRequirementFail(String msg) {
        appUtil.hideKeyboard(this);
        mBinding.edtNumber.clearFocus();
        appUtil.showMessageError(this, mBinding.coordinatorLayout, msg);
    }

    @Override
    public void onResponse(String response, String eventType) {

        if (eventType.equalsIgnoreCase("OtpVerfication")) {
            SignUpBean signUpBean = new Gson().fromJson(response.toString(), SignUpBean.class);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AppDialogs.getInstance().hideLoader(dialogLoader);
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", signUpBean.getData().get(0).getUserId());
                    bundle.putInt("varification_type", varification_type);

                    if (varification_type == 0) {

                        showMsgPopup(signUpBean.getMessage());


                    } else {


                        startNewActivity(bundle, ResetPasswordActivity.class);
                    }

                }
            });


        } else {
            if (eventType.equalsIgnoreCase("ResendOtp")) {
                SignUpBean signUpBean = new Gson().fromJson(response.toString(), SignUpBean.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppDialogs.getInstance().hideLoader(dialogLoader);
                        appUtil.showMessageError(VerificationActivity.this, mBinding.coordinatorLayout, signUpBean.getMessage());
                    }
                });
            }
        }

    }

    private void showMsgPopup(String msg) {
        AppDialogs.getInstance().commonAlertDialog(VerificationActivity.this, msg, "startNewActivity", this);

    }

    @Override
    public void onNoNetworkConnectivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppDialogs.getInstance().hideLoader(dialogLoader);
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
                AppDialogs.getInstance().hideLoader(dialogLoader);
                appUtil.showMessageError(VerificationActivity.this, mBinding.coordinatorLayout, msg);
            }
        });

    }


    @Override
    public void onSessionExpire() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppDialogs.getInstance().hideLoader(dialogLoader);
                sessionExpireError();
            }
        });

    }

    @Override
    public void onAppHardUpdate() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppDialogs.getInstance().hideLoader(dialogLoader);
                updateAppVersion();
            }
        });

    }

    private void getData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String user_id = bundle.getString("user_id");

            String mobile_no = bundle.getString("mobile_no");
            String country_code = bundle.getString("country_code");
            varification_type = bundle.getInt("varification_type");
            verificationViewModel.setUserId(user_id);
            verificationViewModel.setMobile_no(mobile_no);
            verificationViewModel.setCountry_code(country_code);
            verificationViewModel.setVarification_type(varification_type);
        }
    }

    private void enableDisableViews(boolean status) {
        mBinding.btnVerify.setEnabled(status);
        mBinding.txtResendVerificationCode.setEnabled(status);
    }

    @Override
    public void onDialogEvent(Dialog dialog, String event) {
        if (event.equalsIgnoreCase("startNewActivity")) {
            dialog.dismiss();

            startNewActivity(null, LandingActivity.class);
        }
    }
}
