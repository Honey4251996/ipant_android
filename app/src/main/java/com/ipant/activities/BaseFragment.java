package com.ipant.activities;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ipant.R;
import com.ipant.utils.AppDialogs;
import com.ipant.utils.AppUtil;
import com.ipant.utils.LocaleManager;

public abstract class BaseFragment extends Fragment {
    public Object object;
    public Activity mActivity;
    public AppUtil appUtil;


    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        object = DataBindingUtil.inflate(inflater, getLayoutResourceId(), container, false);

        View view = ((ViewDataBinding) object).getRoot();

        // set language
        LocaleManager localeManager = new LocaleManager(getActivity());
        localeManager.setLocale(getActivity());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        appUtil = AppUtil.getInstance();

    }

    protected abstract int getLayoutResourceId();

    protected Object getBindingObject() {
        return object;
    }

    public String getFragAppString(int id) {
        return mActivity.getResources().getString(id);
    }

    public View fragFindView(int id) {
        return mActivity.findViewById(id);
    }

    public void sessionExpireError() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                killLocalSession();
            }
        });

    }

    private void killLocalSession() {
        AppDialogs.getInstance().commonSessionExpireAlertDialog(mActivity, getFragAppString(R.string.txt_session_expire));
    }

    public void updateAppVersion() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hardUpdate();
            }
        });
    }

    private void hardUpdate() {
        AppDialogs.getInstance().commonHardUpdateAlertDialog(mActivity, getFragAppString(R.string.txt_update_app));
    }

    public void noNetworkConnectionDialog() {
        AppDialogs.getInstance().internetConnectionAlertDialog(mActivity, getFragAppString(R.string.error_no_internet_connection));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        LocaleManager localeManager = new LocaleManager(activity);
        localeManager.setLocale(activity);
    }
}