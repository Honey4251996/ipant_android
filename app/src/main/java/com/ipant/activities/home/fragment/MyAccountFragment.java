package com.ipant.activities.home.fragment;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.BaseFragment;
import com.ipant.activities.changePassword.ChangePassowordActivity;
import com.ipant.activities.home.fragment.factories.MyAccountViewModelFactory;
import com.ipant.activities.home.fragment.viewmodels.MyAccountViewModel;
import com.ipant.activities.login.bean.UserDataBean;
import com.ipant.activities.login.bean.UserInfo;
import com.ipant.activities.mycards.MyCardsActivity;
import com.ipant.databinding.MyAccountFragmentBinding;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;
import com.ipant.utils.AppDialogs;
import com.ipant.utils.AppUtil;
import com.ipant.utils.App_Preferences;
import com.ipant.utils.Image_Picker;
import com.theartofdev.edmodo.cropper.CropImage;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MyAccountFragment extends BaseFragment implements NetworkConn.onRequestResponse, RequestResultInterface, AppDialogs.CommonDialogCallback {

    private MyAccountViewModel mViewModel;
    private MyAccountFragmentBinding myAccountFragmentBinding;
    private Image_Picker image_picker;
    private Dialog dialogLoader;
    private boolean editStatus = false;
    private UserDataBean userDataBean, pastProfileObj;
    private String firstname = "", lastname = "", profilePicUrl = "";
    private OnDetailUpdate onDetailUpdateImpl;

    public static MyAccountFragment newInstance() {
        return new MyAccountFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onActivityCreated(savedInstanceState);
        image_picker = new Image_Picker(mActivity);
        myAccountFragmentBinding = (MyAccountFragmentBinding) getBindingObject();
        mViewModel = ViewModelProviders.of(this, new MyAccountViewModelFactory(getActivity().getApplication(), this, this)).get(MyAccountViewModel.class);
        myAccountFragmentBinding.setLifecycleOwner(this);
        myAccountFragmentBinding.setViewModel(mViewModel);
        myAccountFragmentBinding.btnMyCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(MyCardsActivity.getInstance(getActivity()));
            }
        });

        myAccountFragmentBinding.btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(ChangePassowordActivity.getInstance(getActivity()));
            }
        });
        // TODO: Use the ViewModel

        dialogLoader = AppDialogs.getInstance().showLoader(mActivity);
        mViewModel.getUserProfile();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.my_account_fragment;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof OnDetailUpdate) {
            onDetailUpdateImpl = (OnDetailUpdate) getActivity();
        } else {
            throw new RuntimeException(activity.toString() + " must implement OnDetailUpdate.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onDetailUpdateImpl = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i("FragCreateList", "onCreateOptionsMenu called");
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.account_menu, menu);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem editMenuItem = menu.findItem(R.id.edit);
        MenuItem saveMenuItem = menu.findItem(R.id.save);
        if (editStatus) {
            saveMenuItem.setVisible(true);
            editMenuItem.setVisible(false);
        } else {
            saveMenuItem.setVisible(false);
            editMenuItem.setVisible(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                appUtil.hideKeyboard(getActivity());
                myAccountFragmentBinding.edtFirstName.clearFocus();
                myAccountFragmentBinding.edtLastName.clearFocus();
                myAccountFragmentBinding.edtAddress.clearFocus();
                myAccountFragmentBinding.edtFamilyName.clearFocus();
                editStatus = true;
                mViewModel.setViewEnabled(true);
                getActivity().invalidateOptionsMenu();

                break;
            case R.id.save:
                appUtil.hideKeyboard(getActivity());
                myAccountFragmentBinding.edtFirstName.clearFocus();
                myAccountFragmentBinding.edtLastName.clearFocus();
                myAccountFragmentBinding.edtAddress.clearFocus();
                myAccountFragmentBinding.edtFamilyName.clearFocus();
                AppUtil.getInstance().hideKeyboard(mActivity);

                mViewModel.onProfileSavedClicked();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);


    }

    @Override
    public void onResponse(String response, String eventType) {
        parseResponse(eventType, response);
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
                appUtil.showMessageError(getActivity(), myAccountFragmentBinding.coordinatorLayout, msg);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Image_Picker.CAMERA_REQUEST) {

            switch (resultCode) {
                case RESULT_OK:

                    image_picker.cropImage(getActivity(), Uri.fromFile(Image_Picker.IMAGE_PATH), 0);

                    break;
                case RESULT_CANCELED:

                    break;
            }


        } else {

            if (requestCode == Image_Picker.GALLERY_REQUEST) {

                switch (resultCode) {

                    case RESULT_OK:


                        Uri selectedImageURI = data.getData();
                        try {
                            if (image_picker.checkURI(selectedImageURI)) {


                                image_picker.cropImage(getActivity(), selectedImageURI, 0);
                            } else {
                                image_picker.cropImage(getActivity(), image_picker.getImageUrlWithAuthority(getActivity(), selectedImageURI), 0);

                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        break;

                    case RESULT_CANCELED:


                        break;
                }

            } else {

                if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        Uri resultUri = result.getUri();
                        final String path = image_picker.getRealPathFromURI(resultUri);
                        UserInfo userInfo = mViewModel.getLiveUserInfoData().getValue();
                        userInfo.setProfilePic(path);
                        mViewModel.getLiveUserInfoData().setValue(userInfo);
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Image_Picker.PERMISSION_ALL && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (image_picker.hasPermissions(mActivity, Image_Picker.PERMISSIONS)) {
                image_picker.imageOptions();
            }

        }


    }


    private void parseResponse(String eventType, String response) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppDialogs.getInstance().hideLoader(dialogLoader);
                fetchDetails(eventType, response);
            }
        });
    }

    private void fetchDetails(String eventType, String response) {

        if (eventType.equalsIgnoreCase("get_profile_details")) {
            userDataBean = new Gson().fromJson(response, UserDataBean.class);
            pastProfileObj = new Gson().fromJson(response, UserDataBean.class);


            if (userDataBean.getData().get(0).getAuthorization().length() > 0) {
                profilePicUrl = userDataBean.getData().get(0).getProfilePic();
                firstname = userDataBean.getData().get(0).getFirstname();
                lastname = userDataBean.getData().get(0).getLastname();
                AppConstants.USER_DATA_BEAN_OBJ = userDataBean;
                onDetailUpdateImpl.onDetailsChanged(AppConstants.USER_DATA_BEAN_OBJ);
                App_Preferences.saveStringPref(mActivity, AppConstants.KEY_USER_DATA, response);
                mViewModel.setUserDataBean(userDataBean);
                mViewModel.setPastUserDataBean(pastProfileObj);

            }
        } else if (eventType.equalsIgnoreCase("profileUpdate")) {

            userDataBean = new Gson().fromJson(response, UserDataBean.class);
            pastProfileObj = new Gson().fromJson(response, UserDataBean.class);

            if (userDataBean.getData().get(0).getAuthorization().length() > 0) {
                profilePicUrl = userDataBean.getData().get(0).getProfilePic();
                firstname = userDataBean.getData().get(0).getFirstname();
                lastname = userDataBean.getData().get(0).getLastname();
                AppConstants.USER_DATA_BEAN_OBJ = userDataBean;
                onDetailUpdateImpl.onDetailsChanged(AppConstants.USER_DATA_BEAN_OBJ);
                App_Preferences.saveStringPref(mActivity, AppConstants.KEY_USER_DATA, response);
                mViewModel.setUserDataBean(userDataBean);
                mViewModel.setPastUserDataBean(pastProfileObj);
                updateUionResponse(AppConstants.USER_DATA_BEAN_OBJ.getMessage());

            }

        }
    }


    private void updateUionResponse(String msg) {
        editStatus = false;
        mViewModel.setViewEnabled(false);
        getActivity().invalidateOptionsMenu();

        AppDialogs.getInstance().commonAlertDialog(mActivity, msg, "dialogAlert", this);
    }


    @Override
    public void startNewActivity(Bundle bundle, Class newClass) {

    }

    @Override
    public void onSucess(String eventType) {
        if (eventType.equalsIgnoreCase("pickImage")) {
            appUtil.hideKeyboard(mActivity);
            myAccountFragmentBinding.edtFirstName.clearFocus();
            myAccountFragmentBinding.edtLastName.clearFocus();
            if (image_picker.hasPermissions(mActivity, Image_Picker.PERMISSIONS)) {
                image_picker.imageOptions();
            } else {
                image_picker.startDialog();
            }
        } else if (eventType.equalsIgnoreCase("onProfileSaveClicked")) {
            mViewModel.updateUserProfile();
        } else if(eventType.equalsIgnoreCase("noChangeInProfile")){
            updateUionResponse(getFragAppString(R.string.error_no_change_found));

        }

    }

    @Override
    public void onRequestRequirementFail(String failureMsg) {
        appUtil.showMessageError(mActivity, myAccountFragmentBinding.coordinatorLayout, failureMsg);
    }

    @Override
    public void onDialogEvent(Dialog dialog, String event) {
        if (event.equalsIgnoreCase("dialogAlert")) {
            dialog.dismiss();
        }
    }

    public boolean checkAndCancelEditForm() {

        if (editStatus) {
            if (userDataBean != null) {
                if (userDataBean.getData().get(0).getAuthorization().length() > 0) {
                    userDataBean.getData().get(0).setProfilePic(profilePicUrl);
                    userDataBean.getData().get(0).setFirstname(firstname);
                    userDataBean.getData().get(0).setLastname(lastname);
                    AppConstants.USER_DATA_BEAN_OBJ = userDataBean;
                    mViewModel.setUserDataBean(userDataBean);
                }
            }

            editStatus = false;
            myAccountFragmentBinding.edtFirstName.clearFocus();
            myAccountFragmentBinding.edtLastName.clearFocus();
            mViewModel.setViewEnabled(false);
            getActivity().invalidateOptionsMenu();
            return true;
        }
        return false;
    }

    public interface OnDetailUpdate {
        public void onDetailsChanged(UserDataBean userDataBean);
    }

}


