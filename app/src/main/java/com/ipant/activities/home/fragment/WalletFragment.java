package com.ipant.activities.home.fragment;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ipant.R;
import com.ipant.activities.BaseFragment;
import com.ipant.activities.home.fragment.adapters.TransactionFragmentAdapter;
import com.ipant.activities.home.fragment.factories.WalletViewModelFactory;
import com.ipant.activities.home.fragment.viewmodels.WalletViewModel;
import com.ipant.databinding.WalletFragmentBinding;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;
import com.ipant.utils.AppDialogs;

public class WalletFragment extends BaseFragment implements NetworkConn.onRequestResponse {

    private WalletViewModel mViewModel;
    private WalletFragmentBinding walletFragmentBinding;
    private TransactionFragmentAdapter mAdapter;
    private Context mContext;
    private Dialog dialogLoader;
    private int pageCount = 0;


    public static WalletFragment newInstance() {
        return new WalletFragment();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mContext = getActivity();
        walletFragmentBinding = (WalletFragmentBinding) getBindingObject();
        mViewModel = ViewModelProviders.of(this, new WalletViewModelFactory(getActivity().getApplication(), this)).get(WalletViewModel.class);
        gettingControls();
        // TODO: Use the ViewModel
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.wallet_fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        walletFragmentBinding.txtWalletBalance.setText(AppConstants.WALLET_AMOUNT);
    }

    private void gettingControls() {
        walletFragmentBinding.transactionTabLayout.addTab(walletFragmentBinding.transactionTabLayout.newTab());
        walletFragmentBinding.transactionTabLayout.addTab(walletFragmentBinding.transactionTabLayout.newTab());
        walletFragmentBinding.transactionTabLayout.addTab(walletFragmentBinding.transactionTabLayout.newTab());
        walletFragmentBinding.transactionTabLayout.addTab(walletFragmentBinding.transactionTabLayout.newTab());
        setupViewPager(walletFragmentBinding.viewPager);
        settingTabSelected();

        dialogLoader = AppDialogs.getInstance().showLoader(getActivity());
        mViewModel.selectTransactionType(pageCount, 0);



    }

    private void setupViewPager(ViewPager viewPager) {

        mAdapter = new TransactionFragmentAdapter(getChildFragmentManager(), true);
        mAdapter.addFragment(mContext.getString(R.string.text_all));
        mAdapter.addFragment(mContext.getString(R.string.text_added));
        mAdapter.addFragment(mContext.getString(R.string.text_sent));
        mAdapter.addFragment(mContext.getString(R.string.txt_withdrawn));

        walletFragmentBinding.viewPager.setAdapter(mAdapter);
        walletFragmentBinding.transactionTabLayout.setupWithViewPager(walletFragmentBinding.viewPager);
        walletFragmentBinding.viewPager.setCurrentItem(0);

        walletFragmentBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                pageCount = 0;
                dialogLoader = AppDialogs.getInstance().showLoader(getActivity());
                mViewModel.selectTransactionType(pageCount, i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }


    private void settingTabSelected() {

        for (int i = 0; i < 4; i++) {
            ViewGroup mainView = (ViewGroup) walletFragmentBinding.transactionTabLayout.getChildAt(0);
            ViewGroup tabView = (ViewGroup) mainView.getChildAt(i);
            View tabViewChild = tabView.getChildAt(1);
            if (i == 0) {
                ((TextView) tabViewChild).setTypeface(ResourcesCompat.getFont(mContext, R.font.bold));
            } else {
                ((TextView) tabViewChild).setTypeface(ResourcesCompat.getFont(mContext, R.font.semibold));
            }
        }

        walletFragmentBinding.transactionTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                walletFragmentBinding.viewPager.setCurrentItem(tab.getPosition());
                ViewGroup mainView = (ViewGroup) walletFragmentBinding.transactionTabLayout.getChildAt(0);
                ViewGroup tabView = (ViewGroup) mainView.getChildAt(tab.getPosition());
                View tabViewChild = tabView.getChildAt(1);
                ((TextView) tabViewChild).setTypeface(ResourcesCompat.getFont(mContext, R.font.bold));

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ViewGroup mainView = (ViewGroup) walletFragmentBinding.transactionTabLayout.getChildAt(0);
                ViewGroup tabView = (ViewGroup) mainView.getChildAt(tab.getPosition());
                View tabViewChild = tabView.getChildAt(1);
                ((TextView) tabViewChild).setTypeface(ResourcesCompat.getFont(mContext, R.font.semibold));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                ViewGroup mainView = (ViewGroup) walletFragmentBinding.transactionTabLayout.getChildAt(0);
                ViewGroup tabView = (ViewGroup) mainView.getChildAt(tab.getPosition());
                View tabViewChild = tabView.getChildAt(1);
                ((TextView) tabViewChild).setTypeface(ResourcesCompat.getFont(mContext, R.font.bold));
            }

        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }


    @Override
    public void onResponse(String response, String eventType) {
        parseResponse(eventType, response);
    }

    private void parseResponse(String eventType, String response) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppDialogs.getInstance().hideLoader(dialogLoader);
                fetchDetails(eventType, response);
                Log.e("BtaoAll",response);
            }
        });
    }

    private void fetchDetails(String eventType, String response) {

        if (eventType.equalsIgnoreCase("transanction_History")) {

            Fragment fragment = getChildFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + walletFragmentBinding.viewPager.getCurrentItem());
            if (fragment != null) {

                ((AllTransactionListFragment) fragment).onPageSelected(response, mViewModel.getPageCount());

            }
        }
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
                appUtil.showMessageError(getActivity(), walletFragmentBinding.coordinatorLayout, msg);
                if (pageCount == 0) {
                    Fragment fragment = getChildFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + walletFragmentBinding.viewPager.getCurrentItem());
                    if (fragment != null) {
                        ((AllTransactionListFragment) fragment).updateUI();
                    }
                }
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

    public void loadMoreOrPullToRefereshFunctionality(boolean b) {
        dialogLoader = AppDialogs.getInstance().showLoader(getActivity());
        mViewModel.loadMoreOrPullToRefereshFunctionality(b);
    }

    public void updateWalletBalance(String walletAmount) {
        walletFragmentBinding.txtWalletBalance.setText(walletAmount);
    }
}
