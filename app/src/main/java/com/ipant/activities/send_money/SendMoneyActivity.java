package com.ipant.activities.send_money;

import android.Manifest;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.View;

import com.google.gson.Gson;
import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.BaseActivity;
import com.ipant.activities.common_payment_option.CommonPaymentOptionActivity;
import com.ipant.activities.common_payment_option.TransactionStatusConfirmationActivity;
import com.ipant.activities.common_payment_option.beans.TransactionDetails;
import com.ipant.activities.common_payment_option.beans.TransactionResponseBean;
import com.ipant.activities.login.bean.UserDataBean;
import com.ipant.activities.login.bean.UserInfo;
import com.ipant.custom_views.DecimalDigitsInputFilter;
import com.ipant.databinding.ActivitySendMoneyBinding;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;
import com.ipant.utils.AppDialogs;
import com.ipant.utils.Image_Picker;

public class SendMoneyActivity extends BaseActivity implements RequestResultInterface, NetworkConn.onRequestResponse, AppDialogs.CommonDialogCallback {
    private ActivitySendMoneyBinding mBinding;
    private SendMoneyViewModel mViewModel;
    private Dialog dialogLoader;
    private Image_Picker image_picker;
    private UserInfo userInfo;
    private final int PERMISSION_REQUEST_CONTACT = 1004;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        image_picker = new Image_Picker(SendMoneyActivity.this);
        mViewModel = ViewModelProviders.of(this, new SendMoneyViewModelFactory(getApplication(), this, this)).get(SendMoneyViewModel.class);
        mBinding.setViewModel(mViewModel);
        mBinding.edtAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_send_money;
    }

    @Override
    protected Toolbar getToolbar() {
        mBinding = (ActivitySendMoneyBinding) getBindingObject();
        mBinding.layoutAppbar.toolbarTitle.setText(getAppString(R.string.txt_send_money));
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
        if (newClass == CommonPaymentOptionActivity.class) {

            appUtil.showMessageError(this, mBinding.coordinatorLayout, getAppString(R.string.error_insufficient_balancew));
            //  startActivity(CommonPaymentOptionActivity.getInstance(SendMoneyActivity.this, bundle != null ? bundle : null));
        } else if (newClass == TransactionStatusConfirmationActivity.class) {
            startActivity(TransactionStatusConfirmationActivity.newInstance(this, bundle != null ? bundle : null));
        }
    }

    @Override
    public void onSucess(String eventType) {
        removeFocus();
        if (eventType.equalsIgnoreCase("validSendMoneyData")) {
            dialogLoader = AppDialogs.getInstance().showLoader(this);
            mViewModel.checkUserExistance();
        } else if (eventType.equalsIgnoreCase("pickContact")) {
            checkContactPermission();
        }

    }

    private void checkContactPermission() {
        if (image_picker.hasPermissions(SendMoneyActivity.this, new String[]{Manifest.permission.READ_CONTACTS})) {
            Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(i, PERMISSION_REQUEST_CONTACT);
        } else {
            ActivityCompat.requestPermissions(SendMoneyActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, Image_Picker.PERMISSION_ALL);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {


            case Image_Picker.PERMISSION_ALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkContactPermission();


                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    AppDialogs.getInstance().commonAlertDialog(SendMoneyActivity.this, getAppString(R.string.error_no_contact_permission), "contactPermission", this);

                }
                return;
            }


        }
    }

    @Override
    public void onRequestRequirementFail(String failureMsg) {
        removeFocus();
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
                appUtil.showMessageError(SendMoneyActivity.this, mBinding.coordinatorLayout, msg);
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
        if (eventType.equalsIgnoreCase("UserExistance")) {


            UserDataBean userDataBean = new Gson().fromJson(response, UserDataBean.class);

            if (userDataBean.getData().size() > 0) {
                userInfo = userDataBean.getData().get(0);

                if (Double.parseDouble(mViewModel.getAmount()) <= Double.parseDouble(AppConstants.WALLET_AMOUNT)) {
                    AppDialogs.getInstance().coomonMoneyTransferConfirmDialog(this, mViewModel.getAmount(), userDataBean.getData().get(0).getFirstname() + " " + userDataBean.getData().get(0).getLastname(), mViewModel.getMobileNumber(), getAppString(R.string.txt_confirm), getAppString(R.string.txt_cancel), "", "transferMoneyNow", "dismiss", this);
                } else {
                    mViewModel.checkUserWalletBalanceForAction(userInfo);
                }
            }
        } else if (eventType.equalsIgnoreCase("sendMoney")) {
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

        if (event.equalsIgnoreCase("contactPermission")) {
            dialog.dismiss();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        } else if (event.equalsIgnoreCase("dismiss")) {
            dialog.dismiss();
        } else if (event.equalsIgnoreCase("transferMoneyNow")) {
            dialog.dismiss();
            dialogLoader = AppDialogs.getInstance().showLoader(this);
            mViewModel.attemptSendMoney();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PERMISSION_REQUEST_CONTACT && resultCode == RESULT_OK) {

            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            mBinding.edtNumber.setText("");
            String number = cursor.getString(column).replace("(", "").replace(")", "").replace(" ", "").replace("-", "");

            if (number.startsWith("0046")) {
                number = number.replaceFirst("0046", "");
            }

            if (number.startsWith("+")) {

                if (!number.startsWith("+46")) {
                    appUtil.showMessageError(this, mBinding.coordinatorLayout, getAppString(R.string.error_select_sweden_number_only));
                    return;
                }
                number = number.replace("+46", "");
            }

            if (number.startsWith("000") || number.startsWith("00")) {
                appUtil.showMessageError(this, mBinding.coordinatorLayout, getAppString(R.string.error_invalid_number));
                return;
            }

            if (number.length() < 9) {
                appUtil.showMessageError(this, mBinding.coordinatorLayout, getAppString(R.string.error_invalid_number));
                return;
            }

            if (number.length() == 9 && number.startsWith("0")) {
                appUtil.showMessageError(this, mBinding.coordinatorLayout, getAppString(R.string.error_invalid_number));
                return;
            }

            if(number.startsWith("0")){
                mBinding.edtNumber.setText(number.substring(number.length() - 10));
            }else{
                mBinding.edtNumber.setText(number.substring(number.length() - 9));
            }

        }
    }

    private void removeFocus() {
        mBinding.edtNumber.clearFocus();
        mBinding.edtAmount.clearFocus();
        mBinding.edtComment.clearFocus();
    }
}
