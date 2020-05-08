package com.ipant.activities.home.fragment;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;

import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.BaseFragment;
import com.ipant.activities.home.fragment.factories.SettingViewModelFactory;
import com.ipant.activities.home.fragment.viewmodels.SettingViewModel;
import com.ipant.databinding.SettingFragmentBinding;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppDialogs;

public class SettingFragment extends BaseFragment implements NetworkConn.onRequestResponse, RequestResultInterface {
    private SettingFragmentBinding mBinding;
    private SettingViewModel mViewModel;
    private Dialog dialogLoader;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mBinding=(SettingFragmentBinding) getBindingObject();
        mViewModel = ViewModelProviders.of(this, new SettingViewModelFactory(getActivity().getApplication(), this, this)).get(SettingViewModel.class);
        mBinding.setViewModel(mViewModel);


        // TODO: Use the ViewModel
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.setting_fragment;
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.setting_menu, menu);
    }

    @Override
    public void onResponse(String response, String eventType) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppDialogs.getInstance().hideLoader(dialogLoader);
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


    @Override
    public void startNewActivity(Bundle bundle, Class newClass) {

    }

    @Override
    public void onSucess(String eventType) {

        dialogLoader = AppDialogs.getInstance().showLoader(getActivity());
        mViewModel.attemptChangeNotificationStatus();
    }

    @Override
    public void onRequestRequirementFail(String failureMsg) {

    }

}
