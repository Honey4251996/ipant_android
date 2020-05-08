package com.ipant.activities.common_payment_option;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;

import com.google.gson.Gson;
import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.BaseActivity;
import com.ipant.activities.common_payment_option.adapters.CardListAdapter;
import com.ipant.activities.common_payment_option.beans.CardBean;
import com.ipant.activities.common_payment_option.beans.TransactionDetails;
import com.ipant.activities.common_payment_option.beans.TransactionResponseBean;
import com.ipant.databinding.ActivityCommonPaymentOptionBinding;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;
import com.ipant.utils.AppDialogs;
import com.ipant.utils.MonthYearPickerDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CommonPaymentOptionActivity extends BaseActivity implements NetworkConn.onRequestResponse, RequestResultInterface, CardListAdapter.OnClick, AppDialogs.CommonDialogCallback {
    private ActivityCommonPaymentOptionBinding mBinding;
    private CommonPaymentViewModel mViewModel;
    private Dialog dialogLoader;
    private int count = 0;
    private List<CardBean.CardData> cardList;
    private CardListAdapter adapter;
    private int currentItemSelected = 0;
    private String amount, name, mobile_email="";


    public static Intent getInstance(Context context, Bundle bundle) {
        Intent intent = new Intent(context, CommonPaymentOptionActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        cardList = new ArrayList<>();
        mViewModel = ViewModelProviders.of(this, new CommonPaymentViewModelFactory(getApplication(), this, this)).get(CommonPaymentViewModel.class);
        mBinding.setViewModel(mViewModel);
        getIntentData();
        dialogLoader = AppDialogs.getInstance().showLoader(this);
        mViewModel.getCardList();
        mViewModel.getCardNumber().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String cardNumber) {
                cardViewTextLogic(cardNumber);
            }
        });
    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_common_payment_option;
    }

    @Override
    protected Toolbar getToolbar() {
        mBinding = (ActivityCommonPaymentOptionBinding) getBindingObject();
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
    public void startNewActivity(Bundle bundle, Class newClass) {

        startActivity(TransactionStatusConfirmationActivity.newInstance(this, bundle != null ? bundle : null));

    }

    @Override
    public void onSucess(String eventType) {

        clearFocusFromView();
        if (eventType.equalsIgnoreCase("Picker")) {
            showMonthYearPicker();
        } else if (eventType.equalsIgnoreCase("ProceedAddMoney")) {
            dialogLoader = AppDialogs.getInstance().showLoader(this);
            mViewModel.attemptAddMoney();

        } else if (eventType.equalsIgnoreCase("ProceedSendMoney")) {
            //   mViewModel.attemptSendMoney();
            AppDialogs.getInstance().coomonMoneyTransferConfirmDialog(this, amount, name, mobile_email, getAppString(R.string.txt_confirm), getAppString(R.string.txt_cancel), "", "transferMoneyNow", "dismiss", this);

        } else if (eventType.equalsIgnoreCase("1") || eventType.equalsIgnoreCase("2")) {
            int selectedPaymentOption = Integer.parseInt(eventType);
            paymentOptionSelected(selectedPaymentOption);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.txtWalletAmount.setText(AppConstants.WALLET_AMOUNT);
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
                appUtil.showMessageError(CommonPaymentOptionActivity.this, mBinding.coordinatorLayout, msg);
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

    public void paymentOptionSelected(int selectedPaymentOption) {

        switch (selectedPaymentOption) {
            case 1: {
                String title = mBinding.txtPaymentOne.getText().toString().trim();
                mBinding.txtSelectedPaymentOptionTitle.setText(title);
                setNewPaymentMethod();
                break;
            }

            case 2: {


                if (mBinding.txtPaymentTwo.getText().toString().trim().equalsIgnoreCase(getAppString(R.string.txt_saved_cards).toString().trim())) {
                    if (cardList != null && cardList.size() == 0) {
                        AppDialogs.getInstance().commonAlertDialog(this, getAppString(R.string.txt_no_saved_card), "alertNoSavedCardMessage", this);
                        break;
                    }
                }

                String title = mBinding.txtPaymentTwo.getText().toString().trim();
                mBinding.txtSelectedPaymentOptionTitle.setText(title);
                setNewPaymentMethod();
                break;
            }
        }


    }


    private void setNewPaymentMethod() {
        String newPaymentOptionSelected = mBinding.txtSelectedPaymentOptionTitle.getText().toString().trim();
        if (newPaymentOptionSelected.equalsIgnoreCase(getAppString(R.string.txt_saved_cards))) {
            mBinding.rvCards.setVisibility(View.VISIBLE);
            mBinding.rlCardForm.setVisibility(View.GONE);
            mViewModel.setSelectedPaymentOption(4);
            mBinding.txtPaymentOne.setText(getAppString(R.string.txt_credit_card));
            mBinding.txtPaymentTwo.setText(getAppString(R.string.txt_debit_card));
        } else if (newPaymentOptionSelected.equalsIgnoreCase(getAppString(R.string.txt_debit_card))) {
            mBinding.rvCards.setVisibility(View.GONE);
            mBinding.rlCardForm.setVisibility(View.VISIBLE);
            mViewModel.setSelectedPaymentOption(1);
            mBinding.txtPaymentOne.setText(getAppString(R.string.txt_credit_card));
            mBinding.txtPaymentTwo.setText(getAppString(R.string.txt_saved_cards));

        } else if (newPaymentOptionSelected.equalsIgnoreCase(getAppString(R.string.txt_credit_card))) {
            mBinding.rvCards.setVisibility(View.GONE);
            mBinding.rlCardForm.setVisibility(View.VISIBLE);
            mViewModel.setSelectedPaymentOption(2);
            mBinding.txtPaymentOne.setText(getAppString(R.string.txt_debit_card));
            mBinding.txtPaymentTwo.setText(getAppString(R.string.txt_saved_cards));
        }

        resetValues();
    }

    private void showMonthYearPicker() {
        MonthYearPickerDialog pickerDialog = new MonthYearPickerDialog();
        pickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int i2) {

                DecimalFormat decimalFormat = new DecimalFormat("00");
                mViewModel.setMonth(decimalFormat.format(Double.parseDouble(String.valueOf(month))));
                mViewModel.setYear(decimalFormat.format(Double.parseDouble(String.valueOf(year))));
                mBinding.edtExpiryDate.setText(decimalFormat.format(Double.parseDouble(String.valueOf(month))) + "/" + decimalFormat.format(Double.parseDouble(String.valueOf(year))));
            }
        });
        pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");

    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String requestType = bundle.getString("requestType");
            amount = bundle.getString("amount");
            int moneyRequestType = bundle.getInt("moneyRequestType");
            mViewModel.setRequestType(moneyRequestType);
            mViewModel.setAmount(amount);

            if (requestType.equalsIgnoreCase(getAppString(R.string.txt_send_money))) {
                String comment = bundle.getString("comment");
                 mobile_email = bundle.getString("mobile_email");
                name = bundle.getString("name");

                mViewModel.setComment(comment);
                mViewModel.setMobileNumberOrEmail(mobile_email);
                mBinding.txtName.setText(name);
                mBinding.rlWalletOption.setVisibility(View.VISIBLE);
                mBinding.llSendMoney.setVisibility(View.VISIBLE);
                mBinding.txtAddMoney.setVisibility(View.GONE);
                mBinding.txtContactNumber.setText(mobile_email);

            } else if (requestType.equalsIgnoreCase(getAppString(R.string.txt_add_money))) {
                mBinding.llSendMoney.setVisibility(View.GONE);
                mBinding.txtAddMoney.setVisibility(View.VISIBLE);
            }
            mViewModel.setSelectedPaymentOption(1);
            mBinding.txtAmount.setText(amount);
            mBinding.layoutAppbar.toolbarTitle.setText(requestType);
        }
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
        if (eventType.equalsIgnoreCase("cardDetails")) {
            CardBean cardBean = new Gson().fromJson(response.toString(), CardBean.class);
            cardList = cardBean.getData();
            setCardAdapter(cardList);
         /*   if (cardList.size() == 0) {
                mBinding.llLine.setVisibility(View.GONE);
                mBinding.txtPaymentTwo.setVisibility(View.GONE);
            } else {
                setCardAdapter(cardBean.getData());
            }
*/

        } else if (eventType.equalsIgnoreCase("addMoney") || eventType.equalsIgnoreCase("sendMoney")) {
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

    private void cardViewTextLogic(String edtText) {
        if (count <= mBinding.edtCardNumber.getText().toString().length()
                && (mBinding.edtCardNumber.getText().toString().length() == 4
                || mBinding.edtCardNumber.getText().toString().length() == 9
                || mBinding.edtCardNumber.getText().toString().length() == 14)) {
            mBinding.edtCardNumber.setText(mBinding.edtCardNumber.getText().toString().concat(" "));
            int pos = mBinding.edtCardNumber.getText().length();
            mBinding.edtCardNumber.setSelection(pos);
            mViewModel.setCardNumber(mBinding.edtCardNumber.getText().toString());
        } else if (count >= mBinding.edtCardNumber.getText().toString().length()
                && (mBinding.edtCardNumber.getText().toString().length() == 4
                || mBinding.edtCardNumber.getText().toString().length() == 9
                || mBinding.edtCardNumber.getText().toString().length() == 14)) {
            mBinding.edtCardNumber.setText(mBinding.edtCardNumber.getText().toString().substring(0, mBinding.edtCardNumber.getText().toString().length() - 1));
            int pos = mBinding.edtCardNumber.getText().length();
            mBinding.edtCardNumber.setSelection(pos);
            mViewModel.setCardNumber(mBinding.edtCardNumber.getText().toString());
        }
        count = mBinding.edtCardNumber.getText().toString().length();

    }

    private void setCardAdapter(List<CardBean.CardData> cardDataList) {
        adapter = new CardListAdapter(cardDataList, this, this);
        mBinding.rvCards.setAdapter(adapter);

    }


    @Override
    public void onRadioButtonClick(int itemposition, int previouscardselected) {
        cardList.get(previouscardselected).setCardSelected(false);
        cardList.get(itemposition).setCardSelected(true);
        adapter.setPreviousPosition(itemposition);
        adapter.notifyDataSetChanged();
        currentItemSelected = itemposition;
        mViewModel.setCardId(cardList.get(currentItemSelected).getId());
    }

    @Override
    public void getCvvEventListener(int itemposition, String cvv) {
        mViewModel.setCvv(cvv);
    }

    @Override
    public void onProceedPaymentClickListener(int position) {
        mViewModel.onProceedSecurelyClicked();
    }

    private void resetValues() {
        clearFocusFromView();
        mBinding.edtCardNumber.setText("");
        mBinding.edtExpiryDate.setText("");
        mBinding.edtCVVNumber.setText("");
        mBinding.saveCardCheckBox.setChecked(false);
        if (cardList.size() > 0) {
            cardList.get(currentItemSelected).setCardSelected(false);
            currentItemSelected = 0;
            adapter.setPreviousPosition(currentItemSelected);
            adapter.notifyDataSetChanged();
        }

        mViewModel.setCardNumber("");
        mViewModel.setCardId("");
        mViewModel.setCvv("");
        mViewModel.setMonth("");
        mViewModel.setYear("");
        mViewModel.setCheckValue(false);
    }

    private void clearFocusFromView() {
        mBinding.edtCVVNumber.clearFocus();
        mBinding.edtExpiryDate.clearFocus();
        mBinding.edtCVVNumber.clearFocus();
    }

    @Override
    public void onDialogEvent(Dialog dialog, String event) {
        if (event.equalsIgnoreCase("dismiss")) {
            dialog.dismiss();
        } else if (event.equalsIgnoreCase("transferMoneyNow")) {
            dialog.dismiss();
            mViewModel.attemptSendMoney();
        } else if (event.equalsIgnoreCase("alertNoSavedCardMessage")) {
            dialog.dismiss();
        }


    }
}
