package com.ipant.activities.add_money;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;

import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.BaseActivity;
import com.ipant.activities.common_payment_option.CommonPaymentOptionActivity;
import com.ipant.custom_views.DecimalDigitsInputFilter;
import com.ipant.databinding.ActivityAddMoneyBinding;
import com.ipant.utils.AppConstants;
import com.ipant.utils.iPantApp;
public class AddMoneyActivity extends BaseActivity implements RequestResultInterface {
    private ActivityAddMoneyBinding mBinding;
    private AddMoneyViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, new AddMoneyViewModelFactory(getApplication(), this)).get(AddMoneyViewModel.class);
        mBinding.setViewModel(mViewModel);
        mBinding.edtAmount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(5,2)});
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_add_money;
    }

    @Override
    protected Toolbar getToolbar() {
        mBinding = (ActivityAddMoneyBinding) getBindingObject();
        mBinding.layoutAppbar.toolbarTitle.setText(getAppString(R.string.txt_add_money));
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
        if(iPantApp.checkNetworkConnectivity()){
            startActivity(CommonPaymentOptionActivity.getInstance(AddMoneyActivity.this, bundle));
        }else{
            noNetworkConnectionDialog();
        }

    }

    @Override
    public void onSucess(String eventType) {

        mBinding.edtAmount.setText(eventType);

    }

    @Override
    public void onRequestRequirementFail(String failureMsg) {
appUtil.showMessageError(this, mBinding.coordinatorLayout, failureMsg);
    }
}
