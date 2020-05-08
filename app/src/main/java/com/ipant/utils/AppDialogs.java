package com.ipant.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ipant.R;
import com.ipant.activities.login.LoginActivity;
import com.ipant.databinding.CommonMoneyTransferBinding;
import com.ipant.databinding.CommonMsgPopupBinding;
import com.ipant.databinding.MultipleEventPopupBinding;
import com.ipant.databinding.TransactionDetailBinding;

import java.util.Calendar;


public class AppDialogs {

    private static final AppDialogs appDialogsInstance = new AppDialogs();


    public static AppDialogs getInstance() {
        return appDialogsInstance;
    }

    public Dialog showLoader(Context context/*, boolean textStatus, int currentDoc, int totalDoc*/) {
        final Dialog networkDialogLoader = new Dialog(context, R.style.MyTheme);
        String fromTime = "";

        networkDialogLoader.setContentView(R.layout.progress_loader);
        networkDialogLoader.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        networkDialogLoader.setCancelable(false);
        networkDialogLoader.setCanceledOnTouchOutside(false);
        networkDialogLoader.show();
        return networkDialogLoader;

    }

    public Dialog showLoaderForSpecificScreen(Context context) {
        final Dialog networkDialogLoader = new Dialog(context, R.style.MyTheme);
        String fromTime = "";

        networkDialogLoader.setContentView(R.layout.progress_loader);
        networkDialogLoader.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        networkDialogLoader.setCancelable(false);
        networkDialogLoader.setCanceledOnTouchOutside(false);

        return networkDialogLoader;

    }

    public void coomonMoneyTransferConfirmDialog(Context context, String amount, String name, String numberOrEmail, String positiveBtnName, String negativeBtnName, String title, String positiveEventType, String negativeEventType, CommonDialogCallback commonDialogCallback) {
        Dialog multipleEventDialog = new Dialog(context);
        CommonMoneyTransferBinding commonMoneyTransferBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.common_money_transfer, null, false);
        multipleEventDialog.setContentView(commonMoneyTransferBinding.getRoot());
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        multipleEventDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        multipleEventDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        multipleEventDialog.setCanceledOnTouchOutside(false);
        multipleEventDialog.setCancelable(true);

        if (!title.equalsIgnoreCase("")) {
            commonMoneyTransferBinding.txtTitle.setText(title);
        }

        commonMoneyTransferBinding.textName.setText(name);
        commonMoneyTransferBinding.txtWalletAmount.setText(amount);
        commonMoneyTransferBinding.btnPositive.setText(positiveBtnName);
        commonMoneyTransferBinding.btnNegative.setText(negativeBtnName);
        commonMoneyTransferBinding.textNumberOrAccountNumber.setText(numberOrEmail);


        commonMoneyTransferBinding.btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                commonDialogCallback.onDialogEvent(multipleEventDialog, positiveEventType);
            }
        });

        commonMoneyTransferBinding.btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonDialogCallback.onDialogEvent(multipleEventDialog, negativeEventType);
            }
        });

        commonMoneyTransferBinding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonDialogCallback.onDialogEvent(multipleEventDialog, "dismiss");
            }
        });

        multipleEventDialog.show();
    }


    public void commonTransactionDetailDialog(Context context, String msg, String comment, String amount, String eventType, CommonDialogCallback commonDialogCallback) {
        Dialog commonDialog = new Dialog(context);
        TransactionDetailBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.transaction_detail, null, false);
        commonDialog.setContentView(dialogBinding.getRoot());
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        commonDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        commonDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        commonDialog.setCanceledOnTouchOutside(false);
        commonDialog.setCancelable(false);
        dialogBinding.txtTransactionMessage.setText(msg);
        dialogBinding.txtWalletAmount.setText(amount);
        dialogBinding.txtComments.setText(comment);
        dialogBinding.btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonDialogCallback.onDialogEvent(commonDialog, eventType);
            }
        });

        commonDialog.show();
    }


    public void commonAlertDialog(Context context, String msg, String eventType, CommonDialogCallback commonDialogCallback) {
        Dialog commonDialog = new Dialog(context);
        CommonMsgPopupBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.common_msg_popup, null, false);
        commonDialog.setContentView(dialogBinding.getRoot());
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        commonDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        commonDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        commonDialog.setCanceledOnTouchOutside(false);
        commonDialog.setCancelable(false);
        dialogBinding.textMsg.setText(msg);
        dialogBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonDialogCallback.onDialogEvent(commonDialog, eventType);
            }
        });

        commonDialog.show();
    }


    public void commonMultipleEventDialog(Context context, String msg, String positiveBtnName, String negativeBtnName, String title, String positiveEventType, String negativeEventType, CommonDialogCallback commonDialogCallback) {
        Dialog multipleEventDialog = new Dialog(context);
        MultipleEventPopupBinding multipleEventPopupBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.multiple_event_popup, null, false);
        multipleEventDialog.setContentView(multipleEventPopupBinding.getRoot());
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        multipleEventDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        multipleEventDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        multipleEventDialog.setCanceledOnTouchOutside(false);
        multipleEventDialog.setCancelable(false);

        if (!title.equalsIgnoreCase("")) {
            multipleEventPopupBinding.txtTitleBar.setVisibility(View.VISIBLE);
            multipleEventPopupBinding.txtTitle.setText(title);
        }

        multipleEventPopupBinding.btnPositive.setText(positiveBtnName);
        multipleEventPopupBinding.btnNegative.setText(negativeBtnName);

        multipleEventPopupBinding.textMsg.setText(msg);
        multipleEventPopupBinding.btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                commonDialogCallback.onDialogEvent(multipleEventDialog, positiveEventType);
            }
        });

        multipleEventPopupBinding.btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonDialogCallback.onDialogEvent(multipleEventDialog, negativeEventType);
            }
        });

        multipleEventPopupBinding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonDialogCallback.onDialogEvent(multipleEventDialog, "dismiss");
            }
        });

        multipleEventDialog.show();
    }


    public void internetConnectionAlertDialog(Context context, String msg) {
        Dialog commonDialog = new Dialog(context);
        CommonMsgPopupBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.common_msg_popup, null, false);
        commonDialog.setContentView(dialogBinding.getRoot());
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        commonDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        commonDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        commonDialog.setCanceledOnTouchOutside(false);
        commonDialog.setCancelable(false);
        dialogBinding.textMsg.setText(msg);
        dialogBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonDialog.dismiss();
            }
        });

        commonDialog.show();
    }


    public void commonHardUpdateAlertDialog(Context context, String msg) {
        Dialog commonDialog = new Dialog(context);
        CommonMsgPopupBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.common_msg_popup, null, false);
        commonDialog.setContentView(dialogBinding.getRoot());
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        commonDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        commonDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        commonDialog.setCanceledOnTouchOutside(false);
        commonDialog.setCancelable(false);
        dialogBinding.textMsg.setText(msg);
        dialogBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) context).finishAffinity();
                commonDialog.dismiss();
            }
        });

        commonDialog.show();
    }

    public void commonSessionExpireAlertDialog(Context context, String msg) {
        Dialog commonDialog = new Dialog(context);
        CommonMsgPopupBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.common_msg_popup, null, false);
        commonDialog.setContentView(dialogBinding.getRoot());
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        commonDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        commonDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        commonDialog.setCanceledOnTouchOutside(false);
        commonDialog.setCancelable(false);
        dialogBinding.textMsg.setText(msg);
        dialogBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean getStartedCompletedStatus = App_Preferences.loadBooleanPref(context, AppConstants.KEY_GET_STARTED_COMPLETED);
                App_Preferences.clearAllPreferenceData(context);
                App_Preferences.saveBooleanPref(context, AppConstants.KEY_GET_STARTED_COMPLETED, getStartedCompletedStatus);
                Intent intent = LoginActivity.getInstance(context);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ((Activity) context).startActivity(intent);
                ((Activity) context).finish();
                commonDialog.dismiss();
            }
        });

        commonDialog.show();
    }


    public static String[] getYear() {
        String[] yearRangeArray = new String[71];
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i <= 70; i++) {
            yearRangeArray[i] = String.valueOf(year);
            year += 1;
        }
        return yearRangeArray;
    }

    public void hideLoader(final Dialog dialog) {


        dialog.dismiss();
    }

    public interface CommonDialogCallback {
        public void onDialogEvent(Dialog dialog, String event);


    }

}
