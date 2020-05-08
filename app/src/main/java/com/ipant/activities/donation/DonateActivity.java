package com.ipant.activities.donation;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;

import com.google.gson.Gson;
import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.BaseActivity;
import com.ipant.activities.common_payment_option.TransactionStatusConfirmationActivity;
import com.ipant.activities.common_payment_option.beans.TransactionDetails;
import com.ipant.activities.common_payment_option.beans.TransactionResponseBean;
import com.ipant.custom_views.DecimalDigitsInputFilter;
import com.ipant.databinding.DonateLayoutBinding;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;
import com.ipant.utils.AppDialogs;
import com.ipant.utils.iPantApp;

public class DonateActivity extends BaseActivity implements RequestResultInterface, NetworkConn.onRequestResponse {
    private DonateLayoutBinding mBinding;
    private DonateViewModel mViewModel;
    private Dialog dialogLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, new DonateViewModelFactory(getApplication(), this, this)).get(DonateViewModel.class);
        mBinding.setViewModel(mViewModel);
        mBinding.edtAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.donate_layout;
    }

    @Override
    protected Toolbar getToolbar() {
        mBinding = (DonateLayoutBinding) getBindingObject();
        mBinding.layoutAppbar.toolbarTitle.setText(getAppString(R.string.txt_donate));
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
    protected void onResume() {
        super.onResume();
        mBinding.txtAmount.setText(AppConstants.WALLET_AMOUNT);
    }

    @Override
    public void startNewActivity(Bundle bundle, Class newClass) {
        mBinding.edtAmount.clearFocus();
        appUtil.hideKeyboard(this);


        if (iPantApp.checkNetworkConnectivity()) {
            startActivity(TransactionStatusConfirmationActivity.newInstance(this, bundle != null ? bundle : null));
        } else {
            noNetworkConnectionDialog();
        }

    }

    @Override
    public void onSucess(String eventType) {

        if (eventType.equalsIgnoreCase("DonateNow")) {
            dialogLoader = AppDialogs.getInstance().showLoader(DonateActivity.this);
            mViewModel.attemptDonateMoney();
            return;
        }
        mBinding.edtAmount.setText(eventType);

    }

    @Override
    public void onRequestRequirementFail(String failureMsg) {
        appUtil.showMessageError(this, mBinding.coordinatorLayout, failureMsg);
    }

    @Override
    public void onResponse(String response, String eventType) {
        parseResponse(response, eventType);
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
                appUtil.showMessageError(DonateActivity.this, mBinding.coordinatorLayout, msg);
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

    private void parseResponse(String response, String eventType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppDialogs.getInstance().hideLoader(dialogLoader);
                fetchDetails(response, eventType);
            }
        });
    }

    private void fetchDetails(String response, String eventType) {
        if (eventType.equalsIgnoreCase("donateMoney")) {
            TransactionResponseBean transactionResponseBean = new Gson().fromJson(response, TransactionResponseBean.class);

            if (transactionResponseBean.getData().size() > 0) {
                TransactionDetails transactionDetails = transactionResponseBean.getData().get(0);

                AppConstants.WALLET_AMOUNT = transactionDetails.getWalletBalance();
                Bundle bundle = new Bundle();
                bundle.putParcelable("transactionObj", transactionDetails);
                bundle.putString("eventType", eventType);

                startNewActivity(bundle, TransactionStatusConfirmationActivity.class);
            }
        }

    }
}