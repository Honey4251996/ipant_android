package com.ipant.activities.home;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.MutableContextWrapper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.ipant.R;
import com.ipant.activities.BaseActivity;
import com.ipant.activities.home.fragment.beans.QRCodeBean;
import com.ipant.databinding.ActivityQrcodeBinding;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;
import com.ipant.utils.AppDialogs;
import com.ipant.utils.AppUtil;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class QRCodeActivity extends BaseActivity implements
        DecoratedBarcodeView.TorchListener, NetworkConn.onRequestResponse, AppDialogs.CommonDialogCallback {
    private CaptureManager capture;

    private ActivityQrcodeBinding mBinding;
    private Dialog dialogLoader;

    private BeepManager beepManager;
    private Context mContext;
    private boolean isScan = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        mContext = QRCodeActivity.this;
        mBinding.zxingBarcodeScanner.setStatusText("");

        capture = new CaptureManager(this, mBinding.zxingBarcodeScanner);
        mBinding.zxingBarcodeScanner.setTorchListener(this);

        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();


        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        mBinding.zxingBarcodeScanner.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        mBinding.zxingBarcodeScanner.initializeFromIntent(getIntent());
        mBinding.zxingBarcodeScanner.decodeContinuous(callback);

        beepManager = new BeepManager(this);


    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_qrcode;
    }

    @Override
    protected Toolbar getToolbar() {
        mBinding = (ActivityQrcodeBinding) getBindingObject();
        mBinding.includeToolbar.toolbarTitle.setText(getAppString(R.string.title_qr_code));

        return mBinding.includeToolbar.toolbarView;
    }

    @Override
    protected boolean toolbarHomeEnable() {
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();

    }

    @Override
    protected boolean toolbarTitleEnable() {
        return false;
    }


    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mBinding.zxingBarcodeScanner.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }


    @Override
    public void onTorchOn() {
        // switchFlashlightButton.setText(R.string.turn_off_flashlight);
    }

    @Override
    public void onTorchOff() {
        //switchFlashlightButton.setText(R.string.turn_on_flashlight);
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null) {
                // Prevent duplicate scans
                return;
            }


            //AppUtil.showToast(mContext,result.getText());


            if (isScan) {

                callQrCodeScanAPI(result.getText());
                beepManager.playBeepSoundAndVibrate();
                isScan = false;

            }

        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    private void callQrCodeScanAPI(String result) {

        if (dialogLoader != null) {
            dialogLoader.show();
        } else {
            dialogLoader = AppDialogs.getInstance().showLoader(mContext);
        }

        Log.e("SCAN REQUEST", result + "");

        if (result.contains("http://139.59.165.125/soap/payout.php?sessionId=") ||
                result.contains("http://admin.ipant.se/ipant/soap/payout.php?sessionId") ||
                result.contains("https://admin.ipant.se/ipant/soap/payout.php?sessionId")) {

            String sessionID = result.substring(result.indexOf("=") + 1);


            if (sessionID.contains("{{")) {
                sessionID = sessionID.substring(2);
            }


            if (sessionID.contains("}}")) {

                sessionID = sessionID.substring(0, sessionID.length() - 2);

            }

            Log.e("REQUEST", result + "     " + sessionID);


            NetworkConn networkConn = NetworkConn.getInstance();
            networkConn.isFailureResponse(true);
            networkConn.makeRequest(networkConn.createGetRequest(AppConstants.QR_CODE_SCAN_API.concat(sessionID)), "QR_CODE_API", this);

        } else {
            AppDialogs.getInstance().hideLoader(dialogLoader);
            AppDialogs.getInstance().commonAlertDialog(mContext, getString(R.string.wrong_qr_code_msg), "dismiss", this);

        }

    }

    @Override
    public void onResponse(String response, String eventType) {

        runOnUiThread(() -> {

            if (dialogLoader != null)
                AppDialogs.getInstance().hideLoader(dialogLoader);

            if (eventType.equalsIgnoreCase("QR_CODE_API")) {

                Gson gson = new Gson();

                QRCodeBean qrCodeBean = gson.fromJson(response, QRCodeBean.class);

                if (qrCodeBean.getStatus() == 1) {

                    AppConstants.WALLET_AMOUNT = qrCodeBean.getWallet_amount();

                    AppDialogs.getInstance().commonAlertDialog(mContext, qrCodeBean.getMessage(), "QR_CODE_CONFIRM", this);

                } else {

                    AppDialogs.getInstance().commonAlertDialog(mContext, qrCodeBean.getMessage(), "FAILURE_SCAN", new AppDialogs.CommonDialogCallback() {
                        @Override
                        public void onDialogEvent(Dialog dialog, String event) {
                            isScan = true;
                            dialog.dismiss();
                        }
                    });
                }


            }

        });

    }

    @Override
    public void onNoNetworkConnectivity() {
        noNetworkConnectionDialog();
    }

    @Override
    public void onRequestRetry() {

    }

    @Override
    public void onRequestFailed(String msg) {




        runOnUiThread(() -> {
            if (dialogLoader != null)
            AppDialogs.getInstance().hideLoader(dialogLoader);

            AppDialogs.getInstance().commonAlertDialog(mContext, msg, "FAILURE_SCAN", new AppDialogs.CommonDialogCallback() {
                @Override
                public void onDialogEvent(Dialog dialog, String event) {
                    isScan = true;
                    dialog.dismiss();
                }
            });

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
    protected void onResume() {
        super.onResume();
        capture.onResume();

    }

    @Override
    public void onDialogEvent(Dialog dialog, String event) {
        dialog.dismiss();

        if (event.equalsIgnoreCase("QR_CODE_CONFIRM") || event.equalsIgnoreCase("dismiss")) {

            finish();
        }


    }
}