package com.ipant.activities.landing_page;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.ipant.R;
import com.ipant.activities.BaseActivity;
import com.ipant.activities.landing_page.adapter.LanguageAdapter;
import com.ipant.activities.login.LoginActivity;
import com.ipant.activities.signup.CompleteProfileActivity;
import com.ipant.activities.splash.SplashActivity;
import com.ipant.databinding.ActivityLandingBinding;
import com.ipant.databinding.ActivityLoginBinding;
import com.ipant.databinding.DonateLayoutBinding;
import com.ipant.utils.AppConstants;
import com.ipant.utils.iPantApp;

public class LandingActivity extends BaseActivity {
    private ActivityLandingBinding mBinding;
    private boolean selectedLan = true;
    private String mCountryCode[] = {"SE \uD83C\uDDF8\uD83C\uDDEA","EN \uD83C\uDDEC\uD83C\uDDE7"};
//    private int mFlag[] = {R.drawable.blue_logo,R.drawable.circle_bg};

    public static Intent getInstance(Context context) {
        Intent intent=new Intent(context, LandingActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = (ActivityLandingBinding) getBindingObject();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setLanguage();
    }

    private void setLanguage() {

        //Creating the ArrayAdapter instance having the country list
        LanguageAdapter adapter = new LanguageAdapter(this,mCountryCode);

        //Setting the ArrayAdapter data on the Spinner
        mBinding.spinnerLang.setAdapter(adapter);
        if(iPantApp.localeManager.getLanguage().equals("en")) {
            mBinding.spinnerLang.setSelection(1);
        }
        else {
            mBinding.spinnerLang.setSelection(0);
        }
        mBinding.spinnerLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(selectedLan) {
                    selectedLan = false;
                    return;
                }
                String language = mCountryCode[i];
                Log.e("Running",""+language);
                if(language.equals("EN \uD83C\uDDEC\uD83C\uDDE7")) {

                    //Change Application level locale
                    iPantApp.localeManager.setNewLocale(LandingActivity.this, "en");

//                    //It is required to recreate the activity to reflect the change in UI.
                    selectedLan = true;
                    AppConstants.LANGUAGE = "en";
                    startActivity(new Intent(LandingActivity.this, SplashActivity.class));
                    finish();

                }
                else {
                    iPantApp.localeManager.setNewLocale(LandingActivity.this, "sw");

                    //It is required to recreate the activity to reflect the change in UI.
                    selectedLan = true;
                    AppConstants.LANGUAGE = "sw";
                    startActivity(new Intent(LandingActivity.this, SplashActivity.class));
                    finish();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_landing;
    }

    @Override
    protected Toolbar getToolbar() {
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

    public void  loginClick(View view){
        Intent intent=new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void  signUpClick(View view){
        /*Intent intent=new Intent(this, BankIdLogin.class);*/

        Intent intent=new Intent(this, CompleteProfileActivity.class);
        startActivity(intent);
    }
}
