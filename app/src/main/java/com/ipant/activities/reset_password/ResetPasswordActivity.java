package com.ipant.activities.reset_password;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.BaseActivity;
import com.ipant.activities.landing_page.LandingActivity;
import com.ipant.activities.login.LoginActivity;
import com.ipant.databinding.ActivityResetPasswordBinding;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppDialogs;

import org.json.JSONObject;

public class ResetPasswordActivity extends BaseActivity implements NetworkConn.onRequestResponse, RequestResultInterface, AppDialogs.CommonDialogCallback {
    private ActivityResetPasswordBinding mBinding;
    private ResetPasswordViewModel resetPasswordViewModel;
    private Dialog dialogLoader;

    private int varification_type;

    public static Intent getInstance(Context context, Bundle bundle) {
        Intent intent = new Intent(context, ResetPasswordActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        resetPasswordViewModel = ViewModelProviders.of(ResetPasswordActivity.this, new ResetPasswordViewModelFactory(getApplication(), this, this)).get(ResetPasswordViewModel.class);
        mBinding.setViewModel(resetPasswordViewModel);
        getIntentData();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_reset_password;
    }

    @Override
    protected Toolbar getToolbar() {
        mBinding = (ActivityResetPasswordBinding) getBindingObject();
        mBinding.layoutAppbar.toolbarTitle.setText(getAppString(R.string.txt_reset_password));
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
    public boolean onSupportNavigateUp() {

        if (varification_type == 0) {
            startActivity(LandingActivity.getInstance(this));
            finish();
        } else {
            onBackPressed();
        }

        return true;
    }

    @Override
    public void onResponse(String response, String eventType) {

        if (eventType.equalsIgnoreCase("ResetPassword")) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String msg = jsonObject.getString("message");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppDialogs.getInstance().hideLoader(dialogLoader);
                        showMsgDialog(msg);
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


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
                appUtil.showMessageError(ResetPasswordActivity.this, mBinding.coordinatorLayout, msg);
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

    @Override
    public void startNewActivity(Bundle bundle, Class newClass) {
        if (newClass == LoginActivity.class) {
            Intent intent = LoginActivity.getInstance(ResetPasswordActivity.this);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onSucess(String eventType) {

        dialogLoader = AppDialogs.getInstance().showLoader(this);
        resetPasswordViewModel.attemptResetPassword();
    }

    @Override
    public void onRequestRequirementFail(String failureMsg) {
        appUtil.showMessageError(ResetPasswordActivity.this, mBinding.coordinatorLayout, failureMsg);
    }

    @Override
    public void onDialogEvent(Dialog dialog, String event) {
        if (event.equalsIgnoreCase("startNewActivity")) {
            dialog.dismiss();
            startNewActivity(null, LoginActivity.class);
        }

    }

    private void showMsgDialog(String msg) {
        AppDialogs.getInstance().commonAlertDialog(ResetPasswordActivity.this, msg, "startNewActivity", this);
    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        varification_type = bundle.getInt("varification_type");
        String user_id = bundle.getString("user_id");

        resetPasswordViewModel.setVarification_type(varification_type);
        resetPasswordViewModel.setUser_id(user_id);

    }
}
