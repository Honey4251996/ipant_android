package com.ipant.activities.home;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ipant.R;
import com.ipant.activities.BaseActivity;
import com.ipant.activities.home.fragment.CommonWebViewFragment;
import com.ipant.activities.home.fragment.ContactUsFragment;
import com.ipant.activities.home.fragment.HomeFragment;
import com.ipant.activities.home.fragment.MyAccountFragment;
import com.ipant.activities.home.fragment.SettingFragment;
import com.ipant.activities.home.fragment.WalletFragment;
import com.ipant.activities.landing_page.adapter.LanguageAdapter;
import com.ipant.activities.login.LoginActivity;
import com.ipant.activities.login.bean.UserDataBean;
import com.ipant.activities.login.bean.UserInfo;
import com.ipant.activities.splash.SplashActivity;
import com.ipant.databinding.ActivityHomeBinding;
import com.ipant.glide_custom.GlideApp;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;
import com.ipant.utils.AppDialogs;
import com.ipant.utils.App_Preferences;
import com.ipant.utils.Image_Picker;
import com.ipant.utils.iPantApp;

import org.json.JSONArray;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, AppDialogs.CommonDialogCallback, NetworkConn.onRequestResponse, MyAccountFragment.OnDetailUpdate {

    private ActivityHomeBinding mBinding;
    private HomeActivityViewModel homeActivityViewModel;
    private Fragment fragment;
    private Context mContext;
    private Dialog dialogLoader;
    private int selectedMenuItem = 0;
    private boolean selectedLan = true;
    private String mCountryCode[] = {"SE \uD83C\uDDF8\uD83C\uDDEA","EN \uD83C\uDDEC\uD83C\uDDE7"};

    public static Intent getInstance(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        return intent;
    }

    public static Intent getInstance(Context context, Bundle bundle) {
        Intent intent = new Intent(context, HomeActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        return intent;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null) {
            if (intent.getExtras() != null) {
                if (intent.getStringExtra("result").equals("success")) {
                    if (fragment instanceof HomeFragment) {
                        Log.e("///", "getUpdatedWalletBalanceApi");
                        ((HomeFragment) fragment).getUpdatedWalletBalanceApi();
                    }
                }
            }
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = HomeActivity.this;
        homeActivityViewModel = ViewModelProviders.of(this, new HomeActivityViewModelFactory(getApplication(), this)).get(HomeActivityViewModel.class);
        mBinding.setViewModel(homeActivityViewModel);
        mBinding.setLifecycleOwner(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mBinding.aapBarHome.toolbar.setNavigationIcon(R.drawable.ic_menu_icon);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        onDetailsChanged(AppConstants.USER_DATA_BEAN_OBJ);
        fragment = HomeFragment.newInstance();
        addOrReplaceFragment(fragment, 1);
        mBinding.navView.getMenu().getItem(0).setChecked(true);
        isTitleVisible(false, getAppString(R.string.txt_home));
        setLanguage();


        mBinding.aapBarHome.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fragment instanceof HomeFragment) {

                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    } else {
                        mBinding.navView.getMenu().getItem(0).setChecked(true);
                        drawer.openDrawer((int) Gravity.START);
                    }
                } else {
                    setHomeFragment();
                }
            }
        });
    }

    private void setHomeFragment() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        mBinding.aapBarHome.aapTool.setBackgroundColor(ResourcesCompat.getColor(getResources(), android.R.color.transparent, null));
        mBinding.aapBarHome.aapTool.setElevation(0.0f);
        mBinding.drawerLayout.setBackgroundResource(R.drawable.home_bg);
        isTitleVisible(false, getAppString(R.string.txt_home));
        mBinding.aapBarHome.toolbar.setNavigationIcon(R.drawable.ic_menu_icon);
        mBinding.aapBarHome.toolbarTitle.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
        mBinding.navView.getMenu().getItem(0).setChecked(true);
        fragment = HomeFragment.newInstance();
        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mBinding.aapBarHome.spinnerLang.setVisibility(View.VISIBLE);
        addOrReplaceFragment(fragment, 2);
    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }

    @Override
    protected Toolbar getToolbar() {
        mBinding = (ActivityHomeBinding) getBindingObject();
        return mBinding.aapBarHome.toolbar;
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
    public void onBackPressed() {
        if (fragment instanceof HomeFragment) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                if (fragment instanceof MyAccountFragment) {
                    if (((MyAccountFragment) fragment).checkAndCancelEditForm()) {
                        return;
                    }
                }

                ///DEVICE BACK FUNCTIONALITY
                //super.onBackPressed();
                onPressBackFunctionality();
            }
        } else {
            setHomeFragment();
        }


    }

    private int counter;

    /**
     * Device back functionality
     */
    private void onPressBackFunctionality() {

        counter++;
        if (counter > 1) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Touch again to exit", Toast.LENGTH_SHORT).show();
        }

        final long DELAY_TIME = 8000L;
        new Thread(() -> {
            try {
                Thread.sleep(DELAY_TIME);
                counter = 0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (item.getTitle().toString().equalsIgnoreCase(mBinding.aapBarHome.toolbarTitle.getText().toString())) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        if (item.getTitle().toString().equalsIgnoreCase(getAppString(R.string.txt_home))) {
            isTitleVisible(false, item.getTitle().toString());
        } else {
            isTitleVisible(true, item.getTitle().toString());
        }


        if (id == R.id.nav_home) {
            if ((fragment instanceof HomeFragment) == false) {
                selectedMenuItem = 0;
                fragment = HomeFragment.newInstance();
                mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                mBinding.aapBarHome.spinnerLang.setVisibility(View.VISIBLE);
                addOrReplaceFragment(fragment, 2);
            }
        } else if (id == R.id.nav_my_account) {
            if ((fragment instanceof MyAccountFragment) == false) {
                selectedMenuItem = 1;

                mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                fragment = MyAccountFragment.newInstance();
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                mBinding.aapBarHome.toolbar.setNavigationIcon(R.drawable.ic_colored_back);
                mBinding.aapBarHome.aapTool.setBackgroundColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
                mBinding.drawerLayout.setBackgroundColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
                mBinding.aapBarHome.toolbarTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.primary_theme_color_one, null));
                mBinding.aapBarHome.aapTool.setElevation(getResources().getDimensionPixelSize(R.dimen.bar_elevation));
                mBinding.aapBarHome.spinnerLang.setVisibility(View.GONE);
                addOrReplaceFragment(fragment, 2);
            }

        } else if (id == R.id.nav_wallet) {
            if ((fragment instanceof WalletFragment) == false) {
                selectedMenuItem = 2;
                mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                fragment = WalletFragment.newInstance();
                mBinding.aapBarHome.toolbar.setNavigationIcon(R.drawable.ic_back);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                mBinding.aapBarHome.aapTool.setBackgroundColor(ResourcesCompat.getColor(getResources(), android.R.color.transparent, null));
                mBinding.aapBarHome.toolbarTitle.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
                mBinding.aapBarHome.aapTool.setElevation(0.0f);
                mBinding.drawerLayout.setBackgroundResource(R.drawable.home_bg);
                mBinding.aapBarHome.spinnerLang.setVisibility(View.GONE);


                addOrReplaceFragment(fragment, 2);
            }
        } else if (id == R.id.nav_logout) {
            // Call Logout Dialog
            AppDialogs.getInstance().commonMultipleEventDialog(mContext, getAppString(R.string.txt_logout_msg), getAppString(R.string.txt_yes), getAppString(R.string.txt_no), getAppString(R.string.txt_logout), "onYesClicked", "onNoClicked", this);
        } else if (id == R.id.nav_about_app) {
            selectedMenuItem = 4;
            Bundle bundle = new Bundle();
           // bundle.putString("title", item.getTitle().toString());
            bundle.putInt("value", 1);
            fragment = CommonWebViewFragment.newInstance(bundle);
            addOrReplaceFragment(fragment, 2);
        } else if (id == R.id.nav_contact_us) {

            if ((fragment instanceof ContactUsFragment) == false) {
                selectedMenuItem = 5;
                mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                fragment = ContactUsFragment.newInstance();
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                mBinding.aapBarHome.toolbar.setNavigationIcon(R.drawable.ic_colored_back);
                mBinding.aapBarHome.aapTool.setBackgroundColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
                mBinding.drawerLayout.setBackgroundColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
                mBinding.aapBarHome.toolbarTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.primary_theme_color_one, null));
                mBinding.aapBarHome.aapTool.setElevation(getResources().getDimensionPixelSize(R.dimen.bar_elevation));
                mBinding.aapBarHome.spinnerLang.setVisibility(View.GONE);
                addOrReplaceFragment(fragment, 2);

            }

        } else if (id == R.id.nav_invite_friends) {
            inviteFriends();
        } else if (id == R.id.nav_privacy_policy) {
            selectedMenuItem = 6;
            Bundle bundle = new Bundle();
           // bundle.putString("title", item.getTitle().toString());
            bundle.putInt("value", 2);
            fragment = CommonWebViewFragment.newInstance(bundle);
            addOrReplaceFragment(fragment, 2);
        } else if (id == R.id.nav_settings) {
            selectedMenuItem = 3;
            fragment = new SettingFragment();
            addOrReplaceFragment(fragment, 2);
        } else if (id == R.id.nav_terms_and_conditions) {
            selectedMenuItem = 7;
            Bundle bundle = new Bundle();
            //bundle.putString("title", item.getTitle().toString());
            bundle.putInt("value", 3);
            fragment = CommonWebViewFragment.newInstance(bundle);
            addOrReplaceFragment(fragment, 2);
        } else if (id == R.id.nav_faq) {
            selectedMenuItem = 8;
            mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            Bundle bundle = new Bundle();
          //  bundle.putString("title", item.getTitle().toString());
            bundle.putInt("value", 4);
            fragment = CommonWebViewFragment.newInstance(bundle);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            mBinding.aapBarHome.toolbar.setNavigationIcon(R.drawable.ic_colored_back);
            mBinding.aapBarHome.aapTool.setBackgroundColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
            mBinding.drawerLayout.setBackgroundColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
            mBinding.aapBarHome.toolbarTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.primary_theme_color_one, null));
            mBinding.aapBarHome.aapTool.setElevation(getResources().getDimensionPixelSize(R.dimen.bar_elevation));
            mBinding.aapBarHome.spinnerLang.setVisibility(View.GONE);
            addOrReplaceFragment(fragment, 2);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        mBinding.navView.getMenu().getItem(0).setChecked(true);
        return true;
    }


    private void addOrReplaceFragment(Fragment fragment, int addOrReplaceType) {

        switch (addOrReplaceType) {
            case 1: {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = manager.beginTransaction();
                fragmentTransaction.add(mBinding.aapBarHome.contentHome.fragmentContainer.getId(), fragment);
                fragmentTransaction.commit();
                manager.executePendingTransactions();
                break;
            }

            case 2: {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = manager.beginTransaction();

                if (!(fragment instanceof HomeFragment)) {
                    fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.left_out);

                } else {
                    fragmentTransaction.setCustomAnimations(R.anim.left_in, R.anim.right_out);
                }

                fragmentTransaction.replace(mBinding.aapBarHome.contentHome.fragmentContainer.getId(), fragment);
                fragmentTransaction.commit();
                manager.executePendingTransactions();
                break;
            }

        }


    }


    @Override
    public void onDialogEvent(Dialog dialog, String event) {


        if (event.equalsIgnoreCase("onYesClicked")) {
            dialog.dismiss();
            boolean getStartedCompletedStatus = App_Preferences.loadBooleanPref(HomeActivity.this, AppConstants.KEY_GET_STARTED_COMPLETED);
            App_Preferences.clearAllPreferenceData(mContext);
            App_Preferences.saveBooleanPref(HomeActivity.this, AppConstants.KEY_GET_STARTED_COMPLETED, getStartedCompletedStatus);
            Intent intent = LoginActivity.getInstance(mContext);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            if (event.equalsIgnoreCase("onNoClicked") || event.equalsIgnoreCase("dismiss")) {
                dialog.dismiss();
            }
        }
    }

    private void inviteFriends() {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, getAppString(R.string.app_name));
            String strShareMessage = "\nLet me recommend you this application\n\n";
            strShareMessage = strShareMessage + "https://play.google.com/store/apps/details?id=" + getPackageName();
            i.putExtra(Intent.EXTRA_TEXT, strShareMessage);
            startActivity(Intent.createChooser(i, "Share via"));
        } catch (Exception e) {
            //e.toString();
        }
    }

    private void isTitleVisible(boolean isVisible, String title) {

        if (!title.equalsIgnoreCase(getAppString(R.string.txt_logout)) && !title.equalsIgnoreCase(getAppString(R.string.txt_invite_friends))) {
            mBinding.aapBarHome.toolbarTitle.setText(title);
        }

        if (isVisible) {
            mBinding.aapBarHome.barImg.setVisibility(View.GONE);
            mBinding.aapBarHome.toolbarTitle.setVisibility(View.VISIBLE);
        } else {
            mBinding.aapBarHome.barImg.setVisibility(View.VISIBLE);
            mBinding.aapBarHome.toolbarTitle.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResponse(String response, String eventType) {
        //   parseResponse(eventType, response);
    }

    @Override
    public void onNoNetworkConnectivity() {

    }

    @Override
    public void onRequestRetry() {

    }

    @Override
    public void onRequestFailed(String msg) {

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (fragment instanceof MyAccountFragment) {
            ((MyAccountFragment) fragment).onActivityResult(requestCode, resultCode, data);
        } else if (fragment instanceof HomeFragment) {
            ((HomeFragment) fragment).onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Image_Picker.PERMISSION_ALL && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (fragment instanceof MyAccountFragment) {
                ((MyAccountFragment) fragment).onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
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
        if (eventType.equalsIgnoreCase("wallet_current_balance")) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if (jsonArray.length() > 0) {
                    Log.e("///", "fetchDetails->current_wallent_balance");
                    AppConstants.WALLET_AMOUNT = jsonArray.getJSONObject(0).getString("current_wallet_balance");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onDetailsChanged(UserDataBean userDataBean) {
        CircleImageView circleImageView = (CircleImageView) mBinding.navView.getHeaderView(0).findViewById(R.id.userImage);
        TextView textView = (TextView) mBinding.navView.getHeaderView(0).findViewById(R.id.txtName);
        UserInfo userInfo = userDataBean.getData().get(0);
        textView.setText(userInfo.getFirstname() + " " + userInfo.getLastname());

        GlideApp.with(this).load(userInfo.getProfilePic())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(new RequestOptions().circleCrop())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .dontAnimate().into(circleImageView);

    }
    private void setLanguage() {

        //Creating the ArrayAdapter instance having the country list
        LanguageAdapter adapter = new LanguageAdapter(this,mCountryCode);

        //Setting the ArrayAdapter data on the Spinner
        mBinding.aapBarHome.spinnerLang.setAdapter(adapter);
        if(iPantApp.localeManager.getLanguage().equals("en")) {
            mBinding.aapBarHome.spinnerLang.setSelection(1);
        }
        else {
            mBinding.aapBarHome.spinnerLang.setSelection(0);
        }
        mBinding.aapBarHome.spinnerLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    iPantApp.localeManager.setNewLocale(HomeActivity.this, "en");

//                    //It is required to recreate the activity to reflect the change in UI.
                    selectedLan = true;
                    AppConstants.LANGUAGE = "en";
                    startActivity(new Intent(HomeActivity.this, SplashActivity.class));
                    finish();

                }
                else {
                    iPantApp.localeManager.setNewLocale(HomeActivity.this, "sw");

                    //It is required to recreate the activity to reflect the change in UI.
                    selectedLan = true;
                    AppConstants.LANGUAGE = "sw";
                    startActivity(new Intent(HomeActivity.this, SplashActivity.class));
                    finish();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
}
