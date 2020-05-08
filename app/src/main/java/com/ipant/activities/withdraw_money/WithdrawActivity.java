package com.ipant.activities.withdraw_money;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.BaseActivity;
import com.ipant.activities.common_payment_option.TransactionStatusConfirmationActivity;
import com.ipant.activities.common_payment_option.beans.TransactionDetails;
import com.ipant.activities.common_payment_option.beans.TransactionResponseBean;
import com.ipant.custom_views.DecimalDigitsInputFilter;
import com.ipant.databinding.ActivityWithdrawBinding;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;
import com.ipant.utils.AppDialogs;

public class WithdrawActivity extends BaseActivity implements NetworkConn.onRequestResponse, RequestResultInterface, AppDialogs.CommonDialogCallback {
    private ActivityWithdrawBinding mBinding;
    private WithdrawActivityViewModel mViewModel;
    private Dialog dialogLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        mViewModel = ViewModelProviders.of(this, new WithdrawMoneyViewModelFactory(getApplication(), this, this)).get(WithdrawActivityViewModel.class);
        mBinding.setViewModel(mViewModel);
        mBinding.edtAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
        spinnerSetup(getAppStringArray(R.array.BankArray));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.txtAmount.setText(AppConstants.WALLET_AMOUNT);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_withdraw;
    }

    @Override
    protected Toolbar getToolbar() {
        mBinding = (ActivityWithdrawBinding) getBindingObject();
        mBinding.layoutAppbar.toolbarTitle.setText(getAppString(R.string.txt_withdrawn_money));
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


    private void spinnerSetup(String[] banNameArray) {
        ArrayAdapter aa = new ArrayAdapter(this, R.layout.spinner_item_view, banNameArray) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                //    ((TextView) v).setTypeface(getFontStyle("roboto_regular.ttf"));//Typeface for normal view
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                //     ((TextView) v).setTypeface(getFontStyle("roboto_regular.ttf"));//Typeface for dropdown view
                if (getPosition(convertView) == 0) {
                    ((TextView) v).setTextColor(ResourcesCompat.getColor(getResources(), R.color.primary_theme_color_one, null));
                    ((TextView) v).setGravity(Gravity.CENTER);
                    ((TextView) v).setTypeface(null, Typeface.BOLD);

                    //  ((TextView) v).setOnClickListener(null);

                } else {
                    ((TextView) v).setTextColor(ResourcesCompat.getColor(getResources(), R.color.light_grey, null));
                }

                ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.edtTxtSize) / getResources().getDisplayMetrics().density);
                ((TextView) v).setBackgroundColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
                return v;
            }
        };
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        mBinding.spGender.setAdapter(aa);

        mBinding.spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                //      selectedPos = i;

                if (mBinding.spGender.getSelectedItemPosition() == 0) {
                    mViewModel.setBankName("");
                    ((TextView) parent.getChildAt(0)).setTextColor(ResourcesCompat.getColor(getResources(), R.color.light_grey, null));
                } else {
                    mViewModel.setBankName(banNameArray[mBinding.spGender.getSelectedItemPosition()]);
                    ((TextView) parent.getChildAt(0)).setTextColor(ResourcesCompat.getColor(getResources(), R.color.light_grey, null));
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void startNewActivity(Bundle bundle, Class newClass) {
        startActivity(TransactionStatusConfirmationActivity.newInstance(this, bundle != null ? bundle : null));
    }

    @Override
    public void onSucess(String eventType) {
        removeFocus();
        if (eventType.equalsIgnoreCase("attemptWithdrawMoney")) {

            AppDialogs.getInstance().coomonMoneyTransferConfirmDialog(this, mViewModel.getAmount(), mViewModel.getAccountHolderName(), mViewModel.getAccountNumber(), getAppString(R.string.txt_confirm), getAppString(R.string.txt_cancel), "", "transferMoneyNow", "dismiss", this);

        }
    }

    @Override
    public void onRequestRequirementFail(String failureMsg) {
        removeFocus();
        appUtil.showMessageError(this, mBinding.coordinatorLayout, failureMsg);
    }

    @Override
    public void onResponse(String response, String eventType) {
        parseResponse(eventType, response);
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
                appUtil.showMessageError(WithdrawActivity.this, mBinding.coordinatorLayout, msg);
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

    private void parseResponse(String eventType, String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppDialogs.getInstance().hideLoader(dialogLoader);
                fetchDetails(eventType, response);
            }
        });
    }

    private void fetchDetails(String eventType, String response) {
        if (eventType.equalsIgnoreCase("withdrawMoneyEvent")) {
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

    @Override
    public void onDialogEvent(Dialog dialog, String event) {
        if (event.equalsIgnoreCase("transferMoneyNow")) {
            dialog.dismiss();
            dialogLoader = AppDialogs.getInstance().showLoader(this);
            mViewModel.attemptWithdrawMoney();
        } else if (event.equalsIgnoreCase("dismiss")) {
            dialog.dismiss();
        }
    }

    private void removeFocus() {
        mBinding.edtAccountHolderName.clearFocus();
        mBinding.edtAccountNumber.clearFocus();
        mBinding.edtAmount.clearFocus();
        mBinding.edtSortCode.clearFocus();
        mBinding.edtComment.clearFocus();
    }
}
