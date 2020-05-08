package com.ipant.activities.common_payment_option.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ipant.R;
import com.ipant.activities.common_payment_option.beans.CardBean;
import com.ipant.databinding.CardListAdapterBinding;
import com.ipant.network_communication.NetworkConn;
import com.ipant.utils.AppUtil;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.MyViewHolder> {
    private List<CardBean.CardData> mModelList;
    private Context context;
    private OnClick onClick;
    private int previouscardselected = 0;

    private NetworkConn networkConn = NetworkConn.getInstance();

    private AppUtil appUtil;
    private String cardid = "";

    private int item_position;
    private Rect location;


    public CardListAdapter(List<CardBean.CardData> modelList, Context con, OnClick onClick1) {
        mModelList = modelList;
        context = con;

        onClick = onClick1;
        appUtil = AppUtil.getInstance();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        CardBean.CardData saveCardBean = mModelList.get(position);


        holder.mBinding.tvCardName.setText(saveCardBean.getCardNumber());
        holder.mBinding.rbBankName.setText(saveCardBean.getCardType());

        holder.mBinding.etCvvNumber.setText("");

        if (saveCardBean.isCardSelected()) {
            holder.mBinding.rbBankName.setChecked(true);
            holder.mBinding.llCVV.setVisibility(View.VISIBLE);
            holder.mBinding.cvCvv.requestFocus();
            holder.mBinding.btnProceedPayment.setVisibility(View.VISIBLE);

        } else {
            holder.mBinding.etCvvNumber.clearFocus();
            holder.mBinding.rbBankName.setChecked(false);
            holder.mBinding.llCVV.setVisibility(View.GONE);
            holder.mBinding.btnProceedPayment.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return mModelList == null ? 0 : mModelList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        CardListAdapterBinding mBinding;


        private MyViewHolder(View itemView) {
            super(itemView);

            mBinding = DataBindingUtil.bind(itemView);

            mBinding.rbBankName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mBinding.rbBankName.isChecked()) {
                        mBinding.etCvvNumber.clearFocus();
                        onClick.onRadioButtonClick(getAdapterPosition(), previouscardselected);

                    }
                }
            });


            mBinding.etCvvNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    onClick.getCvvEventListener(getAdapterPosition(), s.toString());
                }
            });

            mBinding.btnProceedPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClick.onProceedPaymentClickListener(getAdapterPosition());
                }
            });
        }
    }

    private void showDeleteCardPopup(View view) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);
        TextView ll_item = popupView.findViewById(R.id.tvDeleteCard);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
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
              //  onClick.onRemoveMenuClickListener(item_position);
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

    public void setPreviousPosition(int previouscardselected) {
        this.previouscardselected = previouscardselected;
    }

    public interface OnClick {

        void onRadioButtonClick(int itemposition, int previouscardselected);

        void getCvvEventListener(int itemposition, String cvv);

        void onProceedPaymentClickListener(int position);
    }

}