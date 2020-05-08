package com.ipant.activities.bank_Id_integration;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.BaseActivity;
import com.ipant.activities.signup.CompleteProfileActivity;
import com.ipant.databinding.ActivityBankIdLoginBinding;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppDialogs;

import org.json.JSONException;
import org.json.JSONObject;

public class BankIdLogin extends BaseActivity implements NetworkConn.onRequestResponse, RequestResultInterface, AppDialogs.CommonDialogCallback {
    private ActivityBankIdLoginBinding mBinding;
    private BankIdViewModel bankIdViewModel;
    private Dialog dialogLoader;

    private JSONObject authCallResponseObject;

    private boolean sbidClientStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        bankIdViewModel = ViewModelProviders.of(this, new BankIdViewModelFactory(getApplication(), this, this)).get(BankIdViewModel.class);
        mBinding.setViewModel(bankIdViewModel);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sbidClientStarted && authCallResponseObject != null) {
            dialogLoader = AppDialogs.getInstance().showLoader(this);
            bankIdViewModel.startCollectCall();
            sbidClientStarted = false;

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_bank_id_login;
    }

    @Override
    protected Toolbar getToolbar() {
        mBinding = (ActivityBankIdLoginBinding) getBindingObject();
        mBinding.layoutAppbar.toolbarTitle.setText(getAppString(R.string.txt_sign_up_with_bank_id));
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
    public void onResponse(String response, String eventType) {
        Log.e("Response", response + " " + eventType);
        if (eventType.equalsIgnoreCase("callSigniCateApi")) {
            Log.e("I am in", "I am in");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AppDialogs.getInstance().hideLoader(dialogLoader);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        handleAuthResponse(response);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } else if (eventType.equalsIgnoreCase("startCollectCall")) {
            AppDialogs.getInstance().hideLoader(dialogLoader);
            try {
                handleCollectCallResponse(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isPackageExisted(String targetPackage) {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
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
                appUtil.showMessageError(BankIdLogin.this, mBinding.coordinatorLayout, msg);
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
        startActivity(new Intent(this, CompleteProfileActivity.class));
        finish();
    }

    @Override
    public void onSucess(String eventType) {
        mBinding.edtBankId.clearFocus();
        dialogLoader = AppDialogs.getInstance().showLoader(this);
        bankIdViewModel.attemptBankIdAuthentication();
    }

    @Override
    public void onRequestRequirementFail(String failureMsg) {
        mBinding.edtBankId.clearFocus();
        appUtil.showMessageError(this, mBinding.coordinatorLayout, failureMsg);
    }

    private void createAndStartSbidIntent(JSONObject jsonObject) throws JSONException {

        Log.e("createAndStartSbid", jsonObject.toString());

        String autoStartToken = jsonObject.getString("autoStartToken");
        String orderRef = jsonObject.getString("orderRef");
        bankIdViewModel.setOrderRef(orderRef);
        String collectUrl = jsonObject.getString("collectUrl");
        bankIdViewModel.setCollectUrl(collectUrl);

        Intent intent = new Intent();
        intent.setPackage("com.bankid.bus");
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setType("bankid");
        intent.setData(Uri.parse("bankid://autostarttoken=" + autoStartToken + "&redirect=null "));
        startActivityForResult(intent, 0);

        sbidClientStarted = true;
    }


    private void handleAuthResponse(String authResponse) {
        try {
            authCallResponseObject = new JSONObject(authResponse);
            String errorMessage = null;
            if (authCallResponseObject.has("error")) {
                errorMessage = authCallResponseObject.getString("error");

                try{
                    JSONObject jsonObject=authCallResponseObject.getJSONObject("error");

                    String code=jsonObject.getString("code");

                    if(code.equalsIgnoreCase("ALREADY_IN_PROGRESS")){
                        AppDialogs.getInstance().commonAlertDialog(this, getAppString(R.string.error_already_in_progress), "showAlertDialog", this);
                    }


                }catch (Exception ex){
                    ex.printStackTrace();
                }


            }

            if (errorMessage == null || errorMessage.contentEquals("null")) {

                if (isPackageExisted("com.bankid.bus")) {
                    createAndStartSbidIntent(authCallResponseObject);
                } else {
                    AppDialogs.getInstance().commonAlertDialog(this, getAppString(R.string.error_app_not_installed), "showAlertDialog", this);
                    Log.e("AppNotInstalled", "AppNotInstalled");
                }
            } else {
                Log.e("Error01", "Error on auth response");
            }

        } catch (JSONException e) {
            e.printStackTrace();

            Log.e("Error", "Error on auth response");
        }
    }

    private void showAlert() {

    }


    private void handleCollectCallResponse(String answer) throws JSONException {
        JSONObject collectResponseObject = new JSONObject(answer);
        startCompleteCall(collectResponseObject);
    }

    private void startCompleteCall(JSONObject collectResponseObject) throws JSONException {

        String complete = collectResponseObject.getString("progressStatus"); //Todo remove since we don't use it?
        Log.e("Progress Status", complete);

        if(complete.equalsIgnoreCase("Complete")){
            startNewActivity(null, CompleteProfileActivity.class);
        }else{
            AppDialogs.getInstance().commonAlertDialog(this, getAppString(R.string.error_auth_failed), "showAlertDialog", this);
        }
    }

    @Override
    public void onDialogEvent(Dialog dialog, String event) {
        dialog.dismiss();
    }
}
