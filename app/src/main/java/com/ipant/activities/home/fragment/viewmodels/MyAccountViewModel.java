package com.ipant.activities.home.fragment.viewmodels;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ipant.R;
import com.ipant.RequestResultInterface;
import com.ipant.activities.CustomAndroidViewModel;
import com.ipant.activities.login.bean.UserDataBean;
import com.ipant.activities.login.bean.UserInfo;
import com.ipant.glide_custom.GlideApp;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppConstants;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MyAccountViewModel extends CustomAndroidViewModel {
    private NetworkConn.onRequestResponse onRequestResponse;
    private RequestResultInterface requestResultInterface;
    private MutableLiveData<UserInfo> mutuableUserDataBean = new MutableLiveData<>();
    private MutableLiveData<UserInfo> mutuablePastUserDataBean = new MutableLiveData<>();
    private MutableLiveData<Boolean> viewEnabled = new MutableLiveData<>();

    private MutableLiveData<Integer> viewVisibility = new MutableLiveData<>();
    private String firstName = "", lastName = "", familyName = "", address = "";


    public MyAccountViewModel(@NonNull Application application, RequestResultInterface requestResultInterface, NetworkConn.onRequestResponse onRequestResponse) {
        super(application);
        this.onRequestResponse = onRequestResponse;
        this.requestResultInterface = requestResultInterface;
        viewVisibility.setValue(View.GONE);

        UserInfo userInfo = new UserInfo();
        mutuableUserDataBean.setValue(userInfo);
    }

    public MutableLiveData<UserInfo> getLiveUserInfoData() {
        return mutuableUserDataBean;
    }

    public void setUserDataBean(UserDataBean userDataBean) {
        if (userDataBean == null) {
            userDataBean = new UserDataBean();
        }

        UserInfo userInfo = userDataBean.getData().get(0);
        mutuableUserDataBean.setValue(userInfo);
    }

    public void setPastUserDataBean(UserDataBean userDataBean) {
        if (userDataBean == null) {
            userDataBean = new UserDataBean();
        }

        UserInfo userInfo = userDataBean.getData().get(0);
        mutuablePastUserDataBean.setValue(userInfo);
    }


    public MutableLiveData<Integer> getViewVisibility() {
        return viewVisibility;
    }

    public MutableLiveData<Boolean> isViewEnabled() {
        return viewEnabled;
    }


    public void setViewEnabled(boolean viewEnabled) {
        if (viewEnabled == true) {
            viewVisibility.setValue(View.VISIBLE);
        } else {
            viewVisibility.setValue(View.GONE);
        }

        this.viewEnabled.setValue(viewEnabled);
    }


    public void pickImage() {
        requestResultInterface.onSucess("pickImage");
    }


    private String checkFormValidations() {
        if (TextUtils.isEmpty(firstName)) {
            return getStringFromVM(R.string.error_first_name_empty);
        }

        if (TextUtils.isEmpty(lastName)) {
            return getStringFromVM(R.string.error_last_name_empty);
        }
        return "";


    }

    private String checkFormNewUpdations() {
        UserInfo userInfo = mutuableUserDataBean.getValue();
        UserInfo pastUserInfo = mutuablePastUserDataBean.getValue();


        if (!userInfo.getAddress().equalsIgnoreCase(pastUserInfo.getAddress())
                || !userInfo.getFamilyname().equalsIgnoreCase(pastUserInfo.getFamilyname())
                || !userInfo.getFirstname().equalsIgnoreCase(pastUserInfo.getFirstname())
                || !userInfo.getLastname().equalsIgnoreCase(pastUserInfo.getLastname())
                || !userInfo.getProfilePic().equalsIgnoreCase(pastUserInfo.getProfilePic())) {
            return "";
        }

        return getStringFromVM(R.string.error_no_change_found);
    }

    public void onProfileSavedClicked() {
        String formValidationResult = checkFormValidations();
        if (formValidationResult.equalsIgnoreCase("")) {
            String formNewUpdations = checkFormNewUpdations();

            if (formNewUpdations.equalsIgnoreCase("")) {
                requestResultInterface.onSucess("onProfileSaveClicked");
            } else {
                requestResultInterface.onSucess("noChangeInProfile");
            }
        } else {
            requestResultInterface.onRequestRequirementFail(formValidationResult);
        }
    }


    public void getUserProfile() {
        NetworkConn networkConn = NetworkConn.getInstance();
        networkConn.makeRequest(networkConn.createGetRequest(AppConstants.GET_USER_DETAILS_URL), "get_profile_details", onRequestResponse);
    }


    public void updateUserProfile() {
        RequestBody updateProfile = null;
        String path = mutuableUserDataBean.getValue().getProfilePic();
        if (path.equalsIgnoreCase("") || path.contains("http")) {
            updateProfile = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("profile_image", mutuableUserDataBean.getValue().getProfilePic())
                    .addFormDataPart("firstname", firstName)
                    .addFormDataPart("lastname", lastName)
                    .addFormDataPart("familyname", familyName)
                    .addFormDataPart("address", address)
                    .build();
        } else {
            File uploadFile = new File(path);
            updateProfile = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("profile_image", uploadFile.getName(), RequestBody.create(NetworkConn.MEDIA_TYPE_PNG, uploadFile))
                    .addFormDataPart("firstname", firstName)
                    .addFormDataPart("lastname", lastName)
                    .addFormDataPart("familyname", familyName)
                    .addFormDataPart("address", address)
                    .build();
        }

        NetworkConn networkConn = NetworkConn.getInstance();

        networkConn.makeRequest(networkConn.createFormDataRequest(AppConstants.UPDATE_USER_PROFILE_URL, updateProfile), "profileUpdate", onRequestResponse);


    }


    public TextWatcher getFirstNameTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                mutuableUserDataBean.getValue().setFirstname(editable.toString());
                firstName = editable.toString();
            }
        };
    }


    public TextWatcher getLastNameTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mutuableUserDataBean.getValue().setLastname(editable.toString());
                lastName = editable.toString();
            }
        };
    }


    public TextWatcher getFamilyNameTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                mutuableUserDataBean.getValue().setFamilyname(editable.toString());
                familyName = editable.toString();
            }
        };
    }


    public TextWatcher getAddressTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mutuableUserDataBean.getValue().setAddress(editable.toString());
                address = editable.toString();
            }
        };
    }


    @BindingAdapter({"app:imageUrl"})
    public static void loadImage(CircleImageView view, String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            return;
        }

        GlideApp.with(view.getContext()).load(imageUrl.trim())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(new RequestOptions().circleCrop())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .dontAnimate().into(view);
    }
}