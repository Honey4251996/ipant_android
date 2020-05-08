package com.ipant.activities.home.fragment;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.gson.Gson;
import com.ipant.R;
import com.ipant.activities.BaseFragment;
import com.ipant.activities.home.fragment.beans.ContactUsBean;
import com.ipant.activities.home.fragment.factories.ContactUsViewModelFactory;
import com.ipant.activities.home.fragment.viewmodels.ContactUsViewModel;
import com.ipant.databinding.ContactUsFragmentBinding;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppDialogs;

public class ContactUsFragment extends BaseFragment implements NetworkConn.onRequestResponse {
    private ContactUsFragmentBinding contactUsFragmentBinding;
    private ContactUsViewModel mViewModel;
    private Dialog dialogLoader;

    public static ContactUsFragment newInstance() {
        return new ContactUsFragment();
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        contactUsFragmentBinding=(ContactUsFragmentBinding)getBindingObject();
        mViewModel = ViewModelProviders.of(this, new ContactUsViewModelFactory(getActivity().getApplication(), this)).get(ContactUsViewModel.class);
        contactUsFragmentBinding.setViewModel(mViewModel);
        dialogLoader = AppDialogs.getInstance().showLoader(getActivity());
        mViewModel.getContactDetails();
        // TODO: Use the ViewModel
    }

    @Override
    protected int getLayoutResourceId() {
        return  R.layout.contact_us_fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public void onResponse(String response, String eventType) {
        parseResponseAndUpdate(true, response);
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
        parseResponseAndUpdate(false, msg);

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

    private void parseResponseAndUpdate(boolean isParse, String response) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppDialogs.getInstance().hideLoader(dialogLoader);

                if (isParse) {
                    ContactUsBean contactUsBean = new Gson().fromJson(response.toString(), ContactUsBean.class);
                    ContactUsBean.Datum datum = contactUsBean.getData().get(0);
                    contactUsFragmentBinding.txtContactNumber.setText(datum.getCallus());
                    contactUsFragmentBinding.txtEmail.setText(datum.getEmailus());
                    contactUsFragmentBinding.txtWebAddress.setText(datum.getWebsite());

                } else {
                    appUtil.showMessageError(getActivity(), contactUsFragmentBinding.coordinatorLayout, response);
                }
            }
        });
    }
}
