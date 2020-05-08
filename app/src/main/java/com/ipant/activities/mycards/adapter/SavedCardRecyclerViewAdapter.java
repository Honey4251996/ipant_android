package com.ipant.activities.mycards.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ipant.R;
import com.ipant.activities.mycards.bean.SavedCardBean;
import com.ipant.databinding.SavedCardAdapterLayoutBinding;
import com.ipant.glide_custom.GlideApp;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by agree on 11/1/17.
 */

public class SavedCardRecyclerViewAdapter extends RecyclerView.Adapter<SavedCardRecyclerViewAdapter.MyViewHolder> {
    private List<SavedCardBean.CardData> mModelList;
    private Context mContext;
    private OnClick onClick;


    private int item_position;
    private Rect location;


    public SavedCardRecyclerViewAdapter(List<SavedCardBean.CardData> mModelList, Context mContext, OnClick onClick) {
        this.mModelList = mModelList;
        this.mContext = mContext;
        this.onClick = onClick;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        SavedCardAdapterLayoutBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.saved_card_adapter_layout, parent, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final SavedCardRecyclerViewAdapter.MyViewHolder holder, final int position) {


        holder.mBinding.tvCardName.setText(mModelList.get(position).getCardType());
        holder.mBinding.tvCardNumber.setText(mModelList.get(position).getCardNumber());


        GlideApp.with(mContext).load(mModelList.get(position).getBankImage()).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background).dontAnimate().into(holder.mBinding.ivItemImage);

    }

    @Override
    public int getItemCount() {
        return  mModelList == null ? 0 : mModelList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        SavedCardAdapterLayoutBinding mBinding;

        private MyViewHolder(SavedCardAdapterLayoutBinding binding) {
            super(binding.getRoot());

            mBinding = binding;

            mBinding.tvDeleteMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    item_position = getAdapterPosition();

                    showDeleteCardPopup(mBinding.tvDeleteMenu);
                }
            });


        }
    }

    private void showDeleteCardPopup(View view) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);
        TextView ll_item = popupView.findViewById(R.id.tvDeleteCard);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.setElevation(10f);

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();


                return true;
            }


        });

        location = locateView(view);
        popupWindow.showAtLocation(view, Gravity.TOP | Gravity.LEFT, location.left, location.bottom);
        ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onRemoveMenuClickListener(item_position);
                popupWindow.dismiss();
            }
        });

    }

    public Rect locateView(View v) {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        location = new Rect();
        location.left = (loc_int[0] - v.getWidth());
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = (location.top + v.getHeight()) - 40;
        return location;
    }

    public interface OnClick {
        void onRemoveMenuClickListener(int position);
    }

}
