package com.ipant.activities.changePassword;

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
import com.ipant.databinding.ActivityChangePassowordBinding;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppDialogs;

import org.json.JSONObject;

public class ChangePassowordActivity extends BaseActivity implements NetworkConn.onRequestResponse, RequestResultInterface, AppDialogs.CommonDialogCallback {
    private ActivityChangePassowordBinding mBinding;
    private ChangePasswordViewModel changePasswordViewModel;
    private Dialog dialogLoader;

    public static Intent getInstance(Context activity) {
        Intent intent = new Intent(activity, ChangePassowordActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        changePasswordViewModel = ViewModelProviders.of(this, new ChangePasswordViewModelFactory(getApplication(), this, this)).get(ChangePasswordViewModel.class);
        mBinding.setViewModel(changePasswordViewModel);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_change_passoword;
    }

    @Override
    protected Toolbar getToolbar() {
        mBinding = (ActivityChangePassowordBinding) getBindingObject();
        mBinding.layoutAppbar.toolbarTitle.setText(getAppString(R.string.txt_changePassword));
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
        onBackPressed();
        return true;
    }

    @Override
    public void onResponse(String response, String eventType) {


        if (eventType.equalsIgnoreCase("changePassword")) {
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
                appUtil.showMessageError(ChangePassowordActivity.this, mBinding.coordinatorLayout, msg);
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

    }

    @Override
    public void onSucess(String eventType) {
        clearFocusFromViews();
        dialogLoader = AppDialogs.getInstance().showLoader(this);
        changePasswordViewModel.attemptChangePassword();
    }

    @Override
    public void onRequestRequirementFail(String failureMsg) {
        clearFocusFromViews();
        appUtil.showMessageError(ChangePassowordActivity.this, mBinding.coordinatorLayout, failureMsg);
    }

    @Override
    public void onDialogEvent(Dialog dialog, String event) {
        dialog.dismiss();
        onBackPressed();
    }

    private void showMsgDialog(String msg) {
        AppDialogs.getInstance().commonAlertDialog(ChangePassowordActivity.this, msg, "doNothing", this);
    }

    private void clearFocusFromViews() {
        mBinding.edtConfirmNewPassword.clearFocus();
        mBinding.edtNewPassword.clearFocus();
        mBinding.edtOldPassword.clearFocus();
    }


}
