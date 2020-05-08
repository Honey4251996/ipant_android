package com.ipant.activities;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ipant.R;
import com.ipant.utils.AppDialogs;
import com.ipant.utils.AppUtil;
import com.ipant.utils.LocaleManager;

public abstract class BaseActivity extends AppCompatActivity {

    public Object object;

    public AppUtil appUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        super.onCreate(savedInstanceState);
        appUtil = AppUtil.getInstance();

        object = DataBindingUtil.setContentView(this, getLayoutResourceId());

        setSupportActionBar(getToolbar());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(toolbarTitleEnable());
            getSupportActionBar().setDisplayHomeAsUpEnabled(toolbarHomeEnable());
            getSupportActionBar().setDisplayShowHomeEnabled(toolbarHomeEnable());
        }

        // set language
        LocaleManager localeManager = new LocaleManager(this);
        localeManager.setLocale(this);
    }

    protected abstract int getLayoutResourceId();

    protected abstract Toolbar getToolbar();

    protected abstract boolean toolbarHomeEnable();

    protected abstract boolean toolbarTitleEnable();

    protected Object getBindingObject() {
        return object;
    }


    public String getAppString(int id) {
        return getResources().getString(id);
    }

    public String[] getAppStringArray(int id) {
        return getResources().getStringArray(id);
    }


    @Override
    public void onBackPressed() {
        appUtil.hideKeyboard(this);
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }


    public void sessionExpireError() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                killLocalSession();
            }
        });

    }

    private void killLocalSession() {
        AppDialogs.getInstance().commonSessionExpireAlertDialog(this, getAppString(R.string.txt_session_expire));
    }

    public void updateAppVersion() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hardUpdate();
            }
        });
    }

    private void hardUpdate() {
        AppDialogs.getInstance().commonHardUpdateAlertDialog(this, getAppString(R.string.txt_update_app));
    }

    public void noNetworkConnectionDialog(){
        AppDialogs.getInstance().internetConnectionAlertDialog(this, getAppString(R.string.error_no_internet_connection));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        LocaleManager localeManager = new LocaleManager(this);
        localeManager.setLocale(this);
    }

}
