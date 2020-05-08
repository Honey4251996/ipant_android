package com.ipant.activities.home.fragment;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ipant.R;
import com.ipant.activities.BaseFragment;
import com.ipant.activities.donation.DonateActivity;
import com.ipant.activities.home.QRCodeActivity;
import com.ipant.activities.home.fragment.beans.QRCodeBean;
import com.ipant.activities.home.fragment.factories.HomeViewModelFactory;
import com.ipant.activities.home.fragment.viewmodels.HomeViewModel;
import com.ipant.activities.send_money.SendMoneyActivity;
import com.ipant.activities.webview.WebViewActivity;
import com.ipant.databinding.HomeFragmentBinding;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;
import com.ipant.utils.AppDialogs;
import com.ipant.utils.AppUtil;
import com.ipant.utils.iPantApp;

import org.json.JSONArray;
import org.json.JSONObject;

public class HomeFragment extends BaseFragment implements NetworkConn.onRequestResponse, AppDialogs.CommonDialogCallback, View.OnClickListener{

    private HomeViewModel mViewModel;
    private HomeFragmentBinding mHomeFragmentBinding;
    private Dialog dialogLoader;
    private final int QRCODE_SCANNER = 33333;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        mHomeFragmentBinding = (HomeFragmentBinding) getBindingObject();
        mViewModel = ViewModelProviders.of(this, new HomeViewModelFactory(getActivity().getApplication(), this)).get(HomeViewModel.class);
        // TODO: Use the ViewModel
        mHomeFragmentBinding.setViewModel(mViewModel);

        mHomeFragmentBinding.llAddMoney.setOnClickListener(this);
        mHomeFragmentBinding.llSendMoney.setOnClickListener(this);
        mHomeFragmentBinding.llWithdrawMoney.setOnClickListener(this);
        mHomeFragmentBinding.llScan.setOnClickListener(this);
        mHomeFragmentBinding.llSkanka.setOnClickListener(this);
        mHomeFragmentBinding.llShoppa.setOnClickListener(this);
        mHomeFragmentBinding.llInvestera.setOnClickListener(this);
        mHomeFragmentBinding.llBetala.setOnClickListener(this);

        dialogLoader = AppDialogs.getInstance().showLoader(getActivity());
        mViewModel.getWalletBalance();

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.home_fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        mHomeFragmentBinding.txtAmount.setText(AppConstants.WALLET_AMOUNT);
    }

    @Override
    public void onResponse(String response, String eventType) {
        getActivity().runOnUiThread(() -> {

            if (dialogLoader != null)
                AppDialogs.getInstance().hideLoader(dialogLoader);

            if (eventType.equalsIgnoreCase("QR_CODE_API")) {

                Gson gson = new Gson();

                QRCodeBean qrCodeBean = gson.fromJson(response, QRCodeBean.class);
                AppDialogs.getInstance().commonAlertDialog(getActivity(), qrCodeBean.getMessage(), "QR_CODE_CONFIRM", this);

                mHomeFragmentBinding.txtAmount.setText(qrCodeBean.getWallet_amount());
            } else {
                Log.e("///", "parseWalletAmount");
                parseResponse(eventType, response);
            }
        });

    }

    @Override
    public void onNoNetworkConnectivity() {
        getActivity().runOnUiThread(new Runnable() {
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppDialogs.getInstance().hideLoader(dialogLoader);
                AppUtil.getInstance().showMessageError(getActivity(), mHomeFragmentBinding.coordinatorLayout, msg);
            }
        });
    }

    @Override
    public void onSessionExpire() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppDialogs.getInstance().hideLoader(dialogLoader);
                sessionExpireError();
            }


        });
    }

    @Override
    public void onAppHardUpdate() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppDialogs.getInstance().hideLoader(dialogLoader);
                updateAppVersion();
            }


        });
    }

    private void parseResponse(String eventType, String response) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("///", "parseResponse->fetchDetails");
                AppDialogs.getInstance().hideLoader(dialogLoader);
                fetchDetails(eventType, response);
            }


        });
    }


    private void fetchDetails(String eventType, String response) {
        if (eventType.equalsIgnoreCase("wallet_current_balance")) {
            try {
                Log.e("///", "fetchDetails->txtAmountSetText");
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if (jsonArray.length() > 0) {
                    AppConstants.WALLET_AMOUNT = jsonArray.getJSONObject(0).getString("current_wallet_balance");
                    Log.e("///WalletAmount", AppConstants.WALLET_AMOUNT);
                    mHomeFragmentBinding.txtAmount.setText(AppConstants.WALLET_AMOUNT);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String contentUrl = result.getContents();

            if (contentUrl != null) {


                if (contentUrl.contains("http://139.59.165.125/soap/payout.php?sessionId=") ||
                        contentUrl.contains("http://admin.ipant.se/ipant/soap/payout.php?sessionId") ||
                        contentUrl.contains("https://admin.ipant.se/ipant/soap/payout.php?sessionId")) {

                    String sessionID = contentUrl.substring(contentUrl.indexOf("=") + 1);


                    if (sessionID.contains("{{")) {
                        sessionID = sessionID.substring(2);
                    }


                    if (sessionID.contains("}}")) {

                        sessionID = sessionID.substring(0, sessionID.length() - 2);

                    }

                    Log.e("REQUEST", contentUrl + "     " + sessionID);

                    callScanQrCodeAPI(sessionID);

                } else {

                    AppDialogs.getInstance().commonAlertDialog(getActivity(), getString(R.string.wrong_qr_code_msg), "dismiss", this);

                }
            }


        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Call Scan Qr code API
     *
     * @param sessionID :- Is the session ID of the QR code
     */
    private void callScanQrCodeAPI(String sessionID) {
        NetworkConn networkConn = NetworkConn.getInstance();

        if (dialogLoader != null) {
            dialogLoader.show();
        }
        networkConn.makeRequest(networkConn.createGetRequest(AppConstants.QR_CODE_SCAN_API.concat(sessionID)), "QR_CODE_API", this);
    }


    @Override
    public void onDialogEvent(Dialog dialog, String event) {

        if (event.equalsIgnoreCase("QR_CODE_CONFIRM")) {
/*
            if (dialogLoader != null) {
                dialogLoader.show();
            }
            mViewModel.getWalletBalance();*/
        }
        dialog.dismiss();
    }

    public void getUpdatedWalletBalanceApi() {
        dialogLoader = AppDialogs.getInstance().showLoader(getActivity());
        Log.e("///", "HomeFragment->getUpdatedWalletBalanceApi");
        mViewModel.getWalletBalance();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.llScan:{
                Intent intent = new Intent(getActivity(), QRCodeActivity.class);
                startActivityForResult(intent,QRCODE_SCANNER);
                break;
            }

            case  R.id.llAddMoney:{
                Log.e("///", "AddMoneyClicked->WebViewActivity");
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
               // intent.putExtra("title", getActivity().getString(R.string.txt_add_money));
                intent.putExtra("value", 1);
                startActivity(intent);
                break;
            }

            case  R.id.llSendMoney:{
                if (iPantApp.checkNetworkConnectivity() == false) {
                    AppDialogs.getInstance().internetConnectionAlertDialog(getActivity(), getFragAppString(R.string.error_no_internet_connection));
                    return;
                }
                startActivity(new Intent(getActivity(), SendMoneyActivity.class));
                break;
            }

            case  R.id.llWithdrawMoney:{
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
               // intent.putExtra("title", getActivity().getString(R.string.txt_withdrawn_money));
                intent.putExtra("value", 2);
                startActivity(intent);
                break;
            }

            case R.id.llSkanka:{
                if (iPantApp.checkNetworkConnectivity() == false) {
                    AppDialogs.getInstance().internetConnectionAlertDialog(getActivity(), getFragAppString(R.string.error_no_internet_connection));
                    return;
                }
                startActivity(new Intent(getActivity(), DonateActivity.class));
                break;
            }

            case R.id.llShoppa:{
                comingSoonDialog();
                break;
            }

            case R.id.llInvestera:{
                comingSoonDialog();
                break;
            }

            case R.id.llBetala:{
                comingSoonDialog();
                break;
            }
        }
    }

    private void comingSoonDialog(){
        AppDialogs.getInstance().commonAlertDialog(mActivity, getFragAppString(R.string.txt_coming_soon), "",this);
    }
}
