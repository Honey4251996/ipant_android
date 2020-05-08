package com.ipant.activities.common_payment_option;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.BaseActivity;
import com.ipant.activities.common_payment_option.beans.TransactionDetails;
import com.ipant.activities.home.HomeActivity;
import com.ipant.databinding.ActivityTransactionStatusConfirmationBinding;

public class TransactionStatusConfirmationActivity extends BaseActivity implements RequestResultInterface {

    private ActivityTransactionStatusConfirmationBinding mBinding;
    private TransactionStatusConfirmationViewModel mViewModel;


    public static Intent newInstance(Context context, Bundle bundle) {
        Intent intent = new Intent(context, TransactionStatusConfirmationActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        mViewModel = ViewModelProviders.of(this, new TransactionStatusConfirmationVIewModelFactory(getApplication(), this)).get(TransactionStatusConfirmationViewModel.class);
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(this);
        getIntentData();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_transaction_status_confirmation;
    }

    @Override
    protected Toolbar getToolbar() {
        mBinding = (ActivityTransactionStatusConfirmationBinding) getBindingObject();
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
    public void startNewActivity(Bundle bundle, Class newClass) {
        startActivity(HomeActivity.getInstance(this, null));
        finish();
    }

    @Override
    public void onSucess(String eventType) {

    }

    @Override
    public void onRequestRequirementFail(String failureMsg) {

    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            TransactionDetails transactionDetails = bundle.getParcelable("transactionObj");
            String eventType = bundle.getString("eventType");

            if (eventType.equalsIgnoreCase("addMoney")) {
                mBinding.transactionTypeMsg.setText(getAppString(R.string.txt_amount_added));
            } else if (eventType.equalsIgnoreCase("sendMoney")) {
                mBinding.transactionTypeMsg.setText(getAppString(R.string.txt_amount_sent));
            } else if(eventType.equalsIgnoreCase("withdrawMoneyEvent")){
                mBinding.transactionTypeMsg.setText(getAppString(R.string.txt_request_successful));
            } else if(eventType.equalsIgnoreCase("donateMoney")){
                mBinding.transactionTypeMsg.setText(getAppString(R.string.txt_donate_success));
            }

            mViewModel.setMutuableTrasactionBean(transactionDetails);
        }
    }
}
