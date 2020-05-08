package com.ipant.activities.home.fragment;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ipant.R;
import com.ipant.activities.BaseFragment;
import com.ipant.activities.home.fragment.adapters.AllTransactionListAdapter;
import com.ipant.activities.home.fragment.beans.TransactionBean;
import com.ipant.custom_views.RecyclerSectionItemDecoration;
import com.ipant.databinding.FragmentAllTransactionListBinding;
import com.ipant.utils.AppConstants;
import com.ipant.utils.AppDialogs;
import com.ipant.utils.PaginationScrollListener;

import java.util.ArrayList;
import java.util.List;


public class AllTransactionListFragment extends BaseFragment implements AllTransactionListAdapter.ItemClick, AppDialogs.CommonDialogCallback {


    private static boolean isFragmentBoolean;
    private AllTransactionListAdapter mAdapter;
    private Context mContext;
    private List<TransactionBean.Transaction> myTransactionList;

    private FragmentAllTransactionListBinding mBinding;

    private boolean isLoadMore = false;
    private TextView tv_balance;


    public static AllTransactionListFragment newInstance(int pos, boolean isFragment) {

        AllTransactionListFragment fragment = new AllTransactionListFragment();
        isFragmentBoolean = isFragment;
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();

        mAdapter = new AllTransactionListAdapter(mContext, myTransactionList, this);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        mBinding.rvMyTransation.setHasFixedSize(true);
        mBinding.rvMyTransation.setLayoutManager(manager);
        mBinding.rvMyTransation.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvMyTransation.setAdapter(mAdapter);
        //   mBinding.rvMyTransation.setNestedScrollingEnabled(false);

        RecyclerSectionItemDecoration sectionItemDecoration =
                new RecyclerSectionItemDecoration(getActivity().getResources().getDimensionPixelSize(R.dimen.header),
                        true,
                        getSectionCallback(myTransactionList));
        mBinding.rvMyTransation.addItemDecoration(sectionItemDecoration);

        settingLoadMore(manager);

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_all_transaction_list;
    }


    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final List<TransactionBean.Transaction> transList) {
        return new RecyclerSectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection(int position) {
                return position == 0
                        || (!transList.get(position).getCreatedAt().trim().equalsIgnoreCase(transList.get((position - 1)).getCreatedAt().trim()));
            }

            @Override
            public String getSectionHeader(int position) {
                return transList.get(position)
                        .getCreatedAt();
            }
        };
    }


    private void init() {
        mContext = getActivity();
        myTransactionList = new ArrayList<>();
        mBinding = (FragmentAllTransactionListBinding) getBindingObject();

        /// ADD PULL TO REFERESH FUNCTIONALITY
        pullToRefreshFunctionality();

    }


    /**
     * Setting Load More functionality on the Recyclview
     */
    private void settingLoadMore(RecyclerView.LayoutManager mLayoutManager) {


        mBinding.rvMyTransation.addOnScrollListener(new PaginationScrollListener((LinearLayoutManager) mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                // mocking network delay for API call
                new Handler().postDelayed(() -> {

                    if (isLoadMore) {
                        ((WalletFragment) AllTransactionListFragment.this.getParentFragment()).loadMoreOrPullToRefereshFunctionality(true);

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

    /**
     * Calling pull to refresh API
     */
    private void pullToRefreshFunctionality() {
        mBinding.swipeContainer.setOnRefreshListener(() -> {
            ((WalletFragment) AllTransactionListFragment.this.getParentFragment()).loadMoreOrPullToRefereshFunctionality(false);

        });
        // Configure the refreshing colors
        mBinding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    /**
     * On page selected
     *
     * @param response
     */
    public void onPageSelected(String response, int pageCount) {

        mBinding.swipeContainer.setRefreshing(false);


        Gson gson = new Gson();

        TransactionBean transactionBean = gson.fromJson(response, TransactionBean.class);

        if (transactionBean.getStatus() == 1) {


            // IS LOAD MORE BOOLEAN TO CHECK THE 20 RECORDS FOR LOAD MORE FUNCTIONALITY
            isLoadMore = transactionBean.getData().size() == 20;

            /// PAGE COUNT CHECK TO REFERSH LIST WHEN IT BECOME 0
            if (pageCount == 0) {
                myTransactionList.clear();
            }

            myTransactionList.addAll(transactionBean.getData());

            AppConstants.WALLET_AMOUNT = transactionBean.getWalletBalance();


            ((WalletFragment) AllTransactionListFragment.this.getParentFragment()).updateWalletBalance(AppConstants.WALLET_AMOUNT);


            // CHECK THE LIST DATA TO SHOW THE VISIBILITY

            if (myTransactionList.isEmpty()) {

                isLoadMore = false;
                mBinding.tvNoRecord.setVisibility(View.VISIBLE);
                mBinding.rvMyTransation.setVisibility(View.GONE);
            } else {
                mBinding.tvNoRecord.setVisibility(View.GONE);
                mBinding.rvMyTransation.setVisibility(View.VISIBLE);
            }


            mAdapter.notifyDataSetChanged();

            //   App_Preferences.saveStringPref(mContext, AppConstants.KEY_WALLET_BALANCE, transactionBean.getWalletBalance());


        } else if (myTransactionList.isEmpty()) {

            isLoadMore = false;
            mBinding.tvNoRecord.setVisibility(View.VISIBLE);
            mBinding.rvMyTransation.setVisibility(View.GONE);
        } else {
            isLoadMore = false;
        }


    }


    public void updateUI() {

        isLoadMore = false;
        mBinding.tvNoRecord.setVisibility(View.VISIBLE);
        mBinding.rvMyTransation.setVisibility(View.GONE);
    }

    @Override
    public void onItemClickLIstner(int postition) {

        TransactionBean.Transaction detailObj = myTransactionList.get(postition);

        AppDialogs.getInstance().commonTransactionDetailDialog(mContext, detailObj.getTitle(), detailObj.getMsg(), detailObj.getAmount(), "transDetail", this);

    }

    @Override
    public void onDialogEvent(Dialog dialog, String event) {
        dialog.dismiss();
    }
}


