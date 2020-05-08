package com.ipant.custom_views;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.glide.slider.library.SliderTypes.BaseSliderView;
import com.ipant.R;

public class MyTextViewSlider extends BaseSliderView {
    public MyTextViewSlider(Context context) {
        super(context);
    }

    public View getView() {
        View v = LayoutInflater.from(this.getContext()).inflate(R.layout.my_text_slider_view, (ViewGroup)null);
        AppCompatImageView target = (AppCompatImageView)v.findViewById(R.id.glide_slider_image);
        AppCompatTextView description = (AppCompatTextView)v.findViewById(R.id.description);
        AppCompatTextView title=(AppCompatTextView) v.findViewById(R.id.title);
        String titleName=this.getBundle().getString("extra");
        title.setText(titleName.trim());
        description.setText(this.getDescription());
        this.bindEventAndShow(v, target);
        return v;
    }
}
