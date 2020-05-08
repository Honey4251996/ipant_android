package com.ipant.activities.home.fragment.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ipant.R;
import com.ipant.activities.home.fragment.beans.TransactionBean;
import com.ipant.databinding.AllTransactionListAdapterBinding;

import java.util.List;

/**
 * Created by agree on 11/1/17.
 */

public class AllTransactionListAdapter extends RecyclerView.Adapter<AllTransactionListAdapter.MyViewHolder> {
    private List<TransactionBean.Transaction> mModelList;
    private Context mContext;
    private ItemClick itemClick;


    public AllTransactionListAdapter(Context mContext, List<TransactionBean.Transaction> mModelList, ItemClick itemClick) {
        this.mModelList = mModelList;
        this.mContext = mContext;
        this.itemClick = itemClick;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_transaction_list_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AllTransactionListAdapter.MyViewHolder holder, final int position) {

        TransactionBean.Transaction allTransactionBean = mModelList.get(position);


        holder.mBinding.txtTransactionMessage.setText(allTransactionBean.getTitle());
        holder.mBinding.txtAmount.setText(allTransactionBean.getAmount()+" "+mContext.getResources().getString(R.string.txtCurrencySign));

        holder.mBinding.txtOrderId.setText(mContext.getResources().getString(R.string.txt_order_id) + " " + allTransactionBean.getOrderNumber());
        holder.mBinding.txtStatus.setText(allTransactionBean.getTransactionStatus());

        if (allTransactionBean.getTransactionStatus().equalsIgnoreCase("Success") || allTransactionBean.getTransactionStatus().equalsIgnoreCase("Framgång")) {
            holder.mBinding.txtStatus.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.success_color, null));
        } else if (allTransactionBean.getTransactionStatus().equalsIgnoreCase("Pending") || allTransactionBean.getTransactionStatus().equalsIgnoreCase("Avvaktan")) {
            holder.mBinding.txtStatus.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.orange_color, null));
        } else if (allTransactionBean.getTransactionStatus().equalsIgnoreCase("Failed") || allTransactionBean.getTransactionStatus().equalsIgnoreCase("misslyckades")) {
            holder.mBinding.txtStatus.setTextColor(Color.RED);
        }

        if (allTransactionBean.getTrxType().equalsIgnoreCase("SentMoney")) {
            holder.mBinding.transImg.setImageResource(R.drawable.wallet_send_money_new_color_icon);
        } else if (allTransactionBean.getTrxType().equalsIgnoreCase("Deposit")) {
            holder.mBinding.transImg.setImageResource(R.drawable.wallet_add_money_new_color_icon);
        } else if (allTransactionBean.getTrxType().equalsIgnoreCase("Withdraw")) {
            holder.mBinding.transImg.setImageResource(R.drawable.ic_wallet_withdraw_money_new_color_icon);
        } else if (allTransactionBean.getTrxType().equalsIgnoreCase("MoneyReceived")) {
            holder.mBinding.transImg.setImageResource(R.drawable.wallet_recive_money_new_color);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mModelList == null ? 0 : mModelList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        AllTransactionListAdapterBinding mBinding;

        private MyViewHolder(View itemView) {
            super(itemView);

            mBinding = DataBindingUtil.bind(itemView);
            mBinding.llTransactionItem.setOnClickListener(view -> {

                itemClick.onItemClickLIstner(getAdapterPosition());
            });

        }
    }


    public interface ItemClick {

        void onItemClickLIstner(int postition);

    }


}
