package com.ipant.activities.mycards;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.gson.Gson;
import com.ipant.R;
import com.ipant.activities.BaseActivity;
import com.ipant.activities.mycards.adapter.SavedCardRecyclerViewAdapter;
import com.ipant.activities.mycards.bean.SavedCardBean;
import com.ipant.databinding.ActivityMyCardsBinding;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppDialogs;
import com.ipant.utils.PaginationScrollListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyCardsActivity extends BaseActivity implements SavedCardRecyclerViewAdapter.OnClick, NetworkConn.onRequestResponse {
    private ActivityMyCardsBinding activityMyCardsBinding;
    private MyCardViewModel mViewModel;
    private SavedCardRecyclerViewAdapter mAdapter;
    private Dialog dialogLoader;
    private Context mContext;
    private boolean isLoadMore = false;
    private int pageCount;
    private int deleteItemPosition;
    private List<SavedCardBean.CardData> savedCardList;

    public static Intent getInstance(Context context) {
        Intent intent = new Intent(context, MyCardsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getControls();
    }

    private void getControls() {
        mContext = MyCardsActivity.this;

        // Bind ViewModel With Activity
        mViewModel = ViewModelProviders.of(this, new MyCardViewModelFactory(getApplication(), this)).get(MyCardViewModel.class);
        savedCardList = new ArrayList<>();
        pageCount = 0;
        mAdapter = new SavedCardRecyclerViewAdapter(savedCardList, mContext, this);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        activityMyCardsBinding.myCardRecyclerView.setHasFixedSize(true);
        activityMyCardsBinding.myCardRecyclerView.setLayoutManager(manager);
        activityMyCardsBinding.myCardRecyclerView.setItemAnimator(new DefaultItemAnimator());
        activityMyCardsBinding.myCardRecyclerView.setAdapter(mAdapter);
        settingLoadMore(manager);
        dialogLoader = AppDialogs.getInstance().showLoader(MyCardsActivity.this);
        mViewModel.getCardList(pageCount);

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_my_cards;
    }

    @Override
    protected Toolbar getToolbar() {
        activityMyCardsBinding = (ActivityMyCardsBinding) getBindingObject();
        activityMyCardsBinding.appToolbar.toolbarTitle.setText(getAppString(R.string.txt_myCards));
        return activityMyCardsBinding.appToolbar.toolbarView;
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRemoveMenuClickListener(int position) {
        deleteItemPosition = position;
        String id = savedCardList.get(position).getId();
        dialogLoader = AppDialogs.getInstance().showLoader(MyCardsActivity.this);
        mViewModel.deleteCardOrBank(id);
    }

    @Override
    public void onResponse(String response, String eventType) {
        parseResponse(eventType, response);
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
        if (eventType.equalsIgnoreCase("cardDetails")) {
            final SavedCardBean cardBean = new Gson().fromJson(response.toString(), SavedCardBean.class);
            if (cardBean.getData().size() == 20) {
                isLoadMore = true;
            }
            savedCardList.addAll(cardBean.getData());


            if (savedCardList.size() == 0) {
                activityMyCardsBinding.tvNoRecord.setVisibility(View.VISIBLE);
                activityMyCardsBinding.myCardRecyclerView.setVisibility(View.GONE);
            } else {
                activityMyCardsBinding.tvNoRecord.setVisibility(View.GONE);
                activityMyCardsBinding.myCardRecyclerView.setVisibility(View.VISIBLE);
            }

            mAdapter.notifyDataSetChanged();
        } else if (eventType.equalsIgnoreCase("deleteCard")) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String msg = jsonObject.getString("message");
                savedCardList.remove(deleteItemPosition);
                mAdapter.notifyDataSetChanged();
                appUtil.showMessageError(MyCardsActivity.this, activityMyCardsBinding.coordinatorLayout, msg);
                if (savedCardList.size() == 0) {
                    activityMyCardsBinding.tvNoRecord.setVisibility(View.VISIBLE);
                    activityMyCardsBinding.myCardRecyclerView.setVisibility(View.GONE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    @Override
    public void onNoNetworkConnectivity() {
        runOnUiThread(new Runnable() {
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppDialogs.getInstance().hideLoader(dialogLoader);

                if (savedCardList.size() == 0) {
                    activityMyCardsBinding.tvNoRecord.setVisibility(View.VISIBLE);
                    activityMyCardsBinding.myCardRecyclerView.setVisibility(View.GONE);
                }

                appUtil.showMessageError(MyCardsActivity.this, activityMyCardsBinding.coordinatorLayout, msg);
            }
        });
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

    private void settingLoadMore(RecyclerView.LayoutManager mLayoutManager) {


        activityMyCardsBinding.myCardRecyclerView.addOnScrollListener(new PaginationScrollListener((LinearLayoutManager) mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                // mocking network delay for API call
                new Handler().postDelayed(() -> {

                    if (isLoadMore) {
                        pageCount += 1;
                        dialogLoader = AppDialogs.getInstance().showLoader(MyCardsActivity.this);
                        mViewModel.getCardList(pageCount);

                        isLoadMore = false;

                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return 0;
            }

            @Override
            public boolean isLastPage() {
                return false;
            }

            @Override
            public boolean isLoading() {
                return false;
            }
        });

    }
}
