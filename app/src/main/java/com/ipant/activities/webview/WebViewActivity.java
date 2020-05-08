package com.ipant.activities.webview;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.BaseActivity;
import com.ipant.activities.home.HomeActivity;
import com.ipant.databinding.ActivityWebViewBinding;
import com.ipant.databinding.HomeFragmentBinding; // !!
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppDialogs;
import com.trustly.library.TrustlyJavascriptInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WebViewActivity extends BaseActivity implements NetworkConn.onRequestResponse, RequestResultInterface {
    private ActivityWebViewBinding activityWebViewBinding;
    private HomeFragmentBinding homeFragmentBinding; // !!
    private WebViewViewModel webViewViewModel;
    private Dialog dialogLoader;
    private String title;
    private int value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        webViewViewModel = ViewModelProviders.of(this, new WebViewViewModelFactory(getApplication(),
                this, this)).get(WebViewViewModel.class);
        activityWebViewBinding.setViewModel(webViewViewModel);
        activityWebViewBinding.wv.setWebViewClient(new MyWebClient());
        activityWebViewBinding.wv.setHorizontalScrollBarEnabled(false);
        activityWebViewBinding.wv.setVerticalScrollBarEnabled(false);
        // Enable javascript and DOM Storage
        activityWebViewBinding.wv.getSettings().setJavaScriptEnabled(true);
        activityWebViewBinding.wv.getSettings().setDomStorageEnabled(true);

        // Add TrustlyJavascriptInterface
        activityWebViewBinding.wv.addJavascriptInterface(
                new TrustlyJavascriptInterface(this), TrustlyJavascriptInterface.NAME);

        getIntentData();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_web_view;
    }

    @Override
    protected Toolbar getToolbar() {
        activityWebViewBinding = (ActivityWebViewBinding) getBindingObject();
        return activityWebViewBinding.layoutAppbar.toolbarView;
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getIntentData() {

       // title = getIntent().getStringExtra("title");
        value = getIntent().getIntExtra("value",0);
       // webViewViewModel.setTitle(title);
        webViewViewModel.setValue(value);

        activityWebViewBinding.layoutAppbar.toolbarTitle.setText(title);
        dialogLoader = AppDialogs.getInstance().showLoader(this);
        webViewViewModel.attemptRequestWebViewUrl();

    }

    @Override
    public void startNewActivity(Bundle bundle, Class newClass) {

    }

    @Override
    public void onSucess(String eventType) {

    }

    @Override
    public void onRequestRequirementFail(String failureMsg) {

    }

    @Override
    public void onResponse(String response, String eventType) {
//
//        {
//            "status": 1,
//                "error_code": 200,
//                "message": "",
//                "data": [
//            {
//                "url": "https://admin.ipant.se//trustly/payment/www/deposit.php?user_id=S7MyslIyMleyBgA="
//            }
//    ]
//        }

        if (value == 1) {

            try {
                handleAddMoneyResponse(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (value == 2) {

            try {
                handleWithdrawMoneyResponse(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }

    private void handleWithdrawMoneyResponse(String response) throws JSONException {

        JSONObject withdrawMoneyResponseObject = new JSONObject(response);

        JSONArray jsonArray = withdrawMoneyResponseObject.getJSONArray("data");

        JSONObject url = (JSONObject) jsonArray.get(0);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppDialogs.getInstance().hideLoader(dialogLoader);
                activityWebViewBinding.wv.loadUrl(url.optString("url"));
            }
        });

    }

    private void handleAddMoneyResponse(String response) throws JSONException {

        JSONObject addMoneyResponseObject = new JSONObject(response);

        JSONArray jsonArray = addMoneyResponseObject.getJSONArray("data");

        JSONObject url = (JSONObject) jsonArray.get(0);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("///", "handleAddMoneyResponse");
                Log.e("///url", url.optString("url"));
                AppDialogs.getInstance().hideLoader(dialogLoader);
                activityWebViewBinding.wv.loadUrl(url.optString("url"));
            }
        });

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
                appUtil.showMessageError(WebViewActivity.this, activityWebViewBinding.coordinatorLayout, msg);
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

    public class MyWebClient extends WebViewClient implements AppDialogs.CommonDialogCallback {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub

            //   view.loadUrl(url);

            Log.e("///WebViewUrl", url);


            if (url.equalsIgnoreCase("https://admin.ipant.se/backToHome") ||
                    url.equalsIgnoreCase("https://devadmin.ipant.se/backToHome")) {
                onBackPressed();
                return true;
            }



            return false;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.e("///", "onPageFinished");
            Log.e("///", url);
           // super.onPageFinished(view, url);

            if (url.contains("fail.html") || url.contains("fail.html")) {
                AppDialogs.getInstance().commonAlertDialog(WebViewActivity.this, getString(R.string.error_req_failed), "fail", this);
                return ;
            } else if (url.contains("success.html") || url.contains("success.html")) {
                Log.e("///", "PaymentSuccessAlert");
                AppDialogs.getInstance().commonAlertDialog(WebViewActivity.this, getString(R.string.txt_request_successful), "success", this);
                Log.e("///", "PaymentSuccessAlertConfirm");
//                String amount = "";
//                Bundle bundle = getIntent().getExtras();
//                amount = bundle.getString("amount");
//                Log.e("Payment amount", amount);
//                homeFragmentBinding.txtAmount.setText(amount);
                return ;
            }
        }

        @Override
        public void onDialogEvent(Dialog dialog, String event) {
            dialog.dismiss();
            switch (event) {
                case "fail":
                    finish();
                    break;
                case "success":
                    startActivity(new Intent(WebViewActivity.this, HomeActivity.class).putExtra("result", "success"));
                    finish();
                    break;
            }
        }
    }
}
