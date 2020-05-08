package com.ipant.activities.home.fragment;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.ipant.R;
import com.ipant.activities.BaseFragment;
import com.ipant.activities.home.fragment.factories.CommonWebViewViewModelFactory;
import com.ipant.activities.home.fragment.viewmodels.CommonWebViewViewModel;
import com.ipant.activities.webview.bean.CommonBean;
import com.ipant.databinding.CommonWebViewFragmentBinding;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppDialogs;

public class CommonWebViewFragment extends BaseFragment implements NetworkConn.onRequestResponse {
    private CommonWebViewFragmentBinding mBinding;
    private CommonWebViewViewModel mViewModel;
    private Dialog dialogLoader;

    public static CommonWebViewFragment newInstance(Bundle bundle) {
        CommonWebViewFragment commonWebViewFragment = new CommonWebViewFragment();
        if (bundle != null) {
            commonWebViewFragment.setArguments(bundle);
        }
        return commonWebViewFragment;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mBinding = (CommonWebViewFragmentBinding) getBindingObject();
        mViewModel = ViewModelProviders.of(this, new CommonWebViewViewModelFactory(getActivity().getApplication(), this)).get(CommonWebViewViewModel.class);
        // TODO: Use the ViewModel
        mBinding.setViewModel(mViewModel);

        mBinding.wv.getSettings().setJavaScriptEnabled(true);
        mBinding.wv.setWebViewClient(new MyWebClient());

        getArgumentData();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.common_web_view_fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    private void getArgumentData() {
        Bundle bundle = getArguments();
       // String title = bundle.getString("title");
        int value = bundle.getInt("value",0);
        //mViewModel.setTitle(title);
        mViewModel.setValue(value);
        dialogLoader = AppDialogs.getInstance().showLoader(getActivity());
        mViewModel.attemptRequestWebViewUrl();
    }


    @Override
    public void onResponse(String response, String eventType) {

        if (eventType.equalsIgnoreCase("requestPageUrl")) {
            CommonBean commonBean = new Gson().fromJson(response.toString(), CommonBean.class);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AppDialogs.getInstance().hideLoader(dialogLoader);
                    mBinding.wv.loadUrl(commonBean.getData().get(0).getUrl());
                }
            });
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
                appUtil.showMessageError(getActivity(), mBinding.coordinatorLayout, msg);
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

    public class MyWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub

            view.loadUrl(url);
            return true;

        }
    }

}
