package com.ipant.activities.signup;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.BaseActivity;
import com.ipant.activities.login.LoginActivity;
import com.ipant.activities.signup.beans.SignUpBean;
import com.ipant.databinding.ActivityCompleteProfileBinding;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppDialogs;

public class CompleteProfileActivity extends BaseActivity implements RequestResultInterface, NetworkConn.onRequestResponse, AppDialogs.CommonDialogCallback {
    private ActivityCompleteProfileBinding mBinding;
    private CompleteProfileViewModel completeProfileViewModel;
    private Context context;

    private Dialog dialogLoader;


    public static Intent getInstance(Context context, Bundle bundle) {
        Intent intent = new Intent(context, CompleteProfileActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        context = CompleteProfileActivity.this;
        completeProfileViewModel = ViewModelProviders.of(CompleteProfileActivity.this, new CompleteProfileViewModelFactory(getApplication(), this, this)).get(CompleteProfileViewModel.class);
        mBinding.setViewModel(completeProfileViewModel);

        setTermAndConditions();
    }

    private void setTermAndConditions() {
        SpannableString content = new SpannableString(getString(R.string.privacy_policy_text));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        mBinding.termandcondition.setText(content);

        mBinding.termandcondition.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("https://www.ipant.se/integritetspolicy/"));
                startActivity(browserIntent);
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_complete_profile;
    }

    @Override
    protected Toolbar getToolbar() {
        mBinding = (ActivityCompleteProfileBinding) getBindingObject();
        mBinding.layoutAppbar.toolbarTitle.setText(getAppString(R.string.txt_sign_up));
        mBinding.layoutAppbar.toolbarView.setNavigationIcon(R.drawable.ic_colored_back);
        return mBinding.layoutAppbar.toolbarView;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected boolean toolbarHomeEnable() {
        return true;
    }

    @Override
    protected boolean toolbarTitleEnable() {
        return false;
    }

  /*  @Override
    public void onBackPressed() {
        AppDialogs.getInstance().commonMultipleEventDialog(context, getAppString(R.string.txt_back_exit_msg), getAppString(R.string.txt_confirm), getAppString(R.string.txt_cancel), getAppString(R.string.txt_alert), "onConfirmClick", "onCancelClick", this);
    }*/

    @Override
    public void startNewActivity(Bundle bundle, Class newClass) {
        if (newClass == VerificationActivity.class) {
            Intent intent = VerificationActivity.getInstance(context, bundle);
            startActivity(intent);
        }

    }

    @Override
    public void onSucess(String eventType) {
        enableDisableViews(false);
        removeFocus();
        dialogLoader = AppDialogs.getInstance().showLoader(context);
        completeProfileViewModel.attemptSignUpWithFullDetails();
        enableDisableViews(true);
    }

    @Override
    public void onRequestRequirementFail(String failureMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeFocus();
                appUtil.showMessageError(context, mBinding.coordinatorLayout, failureMsg);
            }
        });

    }

    @Override
    public void onResponse(String response, String eventType) {
        SignUpBean signUpBean = new Gson().fromJson(response.toString(), SignUpBean.class);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppDialogs.getInstance().hideLoader(dialogLoader);
                // showMsgDialog(signUpBean.getMessage());

                Bundle bundle = new Bundle();
                bundle.putString("user_id", signUpBean.getData().get(0).getUserId());
                bundle.putString("mobile_no", mBinding.edtNumber.getText().toString().trim());
                bundle.putString("country_code", mBinding.edtCountryCode.getText().toString().trim());
                bundle.putInt("varification_type", 0);
                startNewActivity(bundle, VerificationActivity.class);

            }
        });

    }

    private void showMsgDialog(String msg) {
        AppDialogs.getInstance().commonAlertDialog(CompleteProfileActivity.this, msg, "startNewActivity", this);
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
                appUtil.showMessageError(context, mBinding.coordinatorLayout, msg);
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
    public void onDialogEvent(Dialog dialog, String event) {
        if (event.equalsIgnoreCase("startNewActivity")) {
            dialog.dismiss();

            startNewActivity(null, VerificationActivity.class);
        } else {
            if (event.equalsIgnoreCase("onConfirmClick")) {
                dialog.dismiss();
                finish();
            } else {
                if (event.equalsIgnoreCase("onCancelClick")) {
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }
            }
        }

    }


    private void removeFocus() {
        appUtil.hideKeyboard(this);
        mBinding.edtConfirmPassword.clearFocus();
        mBinding.edtEmail.clearFocus();
        mBinding.edtPassword.clearFocus();
        mBinding.edtFirstName.clearFocus();
        mBinding.edtLastName.clearFocus();
        mBinding.edtNumber.clearFocus();
    }

    private void enableDisableViews(boolean status) {
        mBinding.btnSubmit.setEnabled(status);

    }

    public void  checkBoxClick(View view){
        if(mBinding.termandConditionCheck.isChecked()) {
            //Toast.makeText(context, "Checked", Toast.LENGTH_SHORT).show();
            completeProfileViewModel.privacyCheck = true;
        }
        else {
           // Toast.makeText(context, "Not Checked", Toast.LENGTH_SHORT).show();
            completeProfileViewModel.privacyCheck = false;
        }
    }
}
