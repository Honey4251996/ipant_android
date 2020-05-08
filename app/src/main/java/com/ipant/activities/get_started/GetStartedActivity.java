package com.ipant.activities.get_started;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.BaseSliderView;
import com.glide.slider.library.Tricks.ViewPagerEx;
import com.ipant.R;
import com.ipant.activities.BaseActivity;
import com.ipant.activities.landing_page.LandingActivity;
import com.ipant.custom_views.MyTextViewSlider;
import com.ipant.databinding.ActivityGetStartedBinding;
import com.ipant.utils.AppConstants;
import com.ipant.utils.App_Preferences;

import java.util.HashMap;

public class GetStartedActivity extends BaseActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    private ActivityGetStartedBinding activityGetStartedBinding;
    private boolean isAutoCycleEnabled = true;

    public static Intent getInstance(Context context) {
        Intent intent = new Intent(context, GetStartedActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {

        HashMap<String, Integer> url_maps = new HashMap<String, Integer>();

      //  url_maps.put("0", R.drawable.walkthrough_1);
        url_maps.put("0", R.drawable.walkthrough_2);

        url_maps.put("1", R.drawable.walkthrough_3);


        String titleOfImage="";




        for(int i=0;i<2;i++){
            MyTextViewSlider textSliderView = new MyTextViewSlider(this);
            if(i==0){

//                textSliderView
//                        .image(R.drawable.walkthrough_1)
//                        .description(getAppString(R.string.splash_one_desc))
//                        .setOnSliderClickListener(this);
//
//
//                //add your extra information
//                textSliderView.bundle(new Bundle());
//                textSliderView.getBundle()
//                        .putString("extra", getAppString(R.string.splash_one_title));

                textSliderView
                        .image(R.drawable.walkthrough_2)
                        .description(getAppString(R.string.splash_two_desc))
                        .setOnSliderClickListener(this);


                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra", getAppString(R.string.splash_two_title));


            } else if(i==1){


//                textSliderView
//                        .image(R.drawable.walkthrough_2)
//                        .description(getAppString(R.string.splash_two_desc))
//                        .setOnSliderClickListener(this);
//
//
//                //add your extra information
//                textSliderView.bundle(new Bundle());
//                textSliderView.getBundle()
//                        .putString("extra", getAppString(R.string.splash_two_title));

                textSliderView
                        .image(R.drawable.walkthrough_3)
                        .description(getAppString(R.string.splash_three_desc))
                        .setOnSliderClickListener(this);


                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra", getAppString(R.string.splash_three_title));

            }
//            else if(i==2) {
//
//
//                textSliderView
//                        .image(R.drawable.walkthrough_3)
//                        .description(getAppString(R.string.splash_three_desc))
//                        .setOnSliderClickListener(this);
//
//
//                //add your extra information
//                textSliderView.bundle(new Bundle());
//                textSliderView.getBundle()
//                        .putString("extra", getAppString(R.string.splash_three_title));
//
//            }

            activityGetStartedBinding.slider.addSlider(textSliderView);
        }




      /*  for (String name : url_maps.keySet()) {
            MyTextViewSlider textSliderView = new MyTextViewSlider(this);
            // initialize a SliderLayout

            String desc = "";

            if (name.equalsIgnoreCase("$"+getAppString(R.string.splash_one_title))) {
                desc = getAppString(R.string.splash_one_desc);
            } else if (name.equalsIgnoreCase(" "+getAppString(R.string.splash_two_title))) {
                desc = getAppString(R.string.splash_two_desc);
            } else if (name.equalsIgnoreCase(getAppString(R.string.splash_three_title))) {
                desc = getAppString(R.string.splash_three_desc);
            }












            textSliderView
                    .image(url_maps.get(name))
                    .description(desc)
                    .setOnSliderClickListener(this);


            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);


            activityGetStartedBinding.slider.addSlider(textSliderView);
        }*/




        activityGetStartedBinding.slider.setPresetTransformer(SliderLayout.Transformer.Default);
        activityGetStartedBinding.slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        //   activityGetStartedBinding.slider.setCustomAnimation(new DescriptionAnimation());
        /*   activityGetStartedBinding.slider.setCustomIndicator(activityGetStartedBinding.customIndicator);*/

        activityGetStartedBinding.slider.getPagerIndicator().setDefaultIndicatorColor(ContextCompat.getColor(GetStartedActivity.this, R.color.primary_theme_color_one), ContextCompat.getColor(GetStartedActivity.this, R.color.light_grey));
        activityGetStartedBinding.slider.setDuration(2000);
        activityGetStartedBinding.slider.setCurrentPosition(0);
        activityGetStartedBinding.slider.addOnPageChangeListener(this);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_get_started;
    }

    @Override
    protected Toolbar getToolbar() {
        activityGetStartedBinding = (ActivityGetStartedBinding) getBindingObject();
        return null;
    }

    @Override
    protected boolean toolbarHomeEnable() {
        return false;
    }

    @Override
    protected boolean toolbarTitleEnable() {
        return false;
    }

    @Override
    public void onSliderClick(BaseSliderView baseSliderView) {

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        if (i == 1) {
            activityGetStartedBinding.rlSkip.setVisibility(View.GONE);
            ObjectAnimator animation = ObjectAnimator.ofFloat(activityGetStartedBinding.btnNext, "weightSum", 1.0f);
            animation.setDuration(2000);
            animation.start();
            activityGetStartedBinding.slider.stopAutoCycle();
            isAutoCycleEnabled = false;
            activityGetStartedBinding.btnNext.setText(getAppString(R.string.txt_get_started));
        } else {
            if (isAutoCycleEnabled == false) {
                activityGetStartedBinding.slider.startAutoCycle();
                isAutoCycleEnabled = true;
            }
            activityGetStartedBinding.rlSkip.setVisibility(View.VISIBLE);
            activityGetStartedBinding.btnNext.setText(getAppString(R.string.txt_next));
        }


    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public void onSkipClicked(View view) {
        enableDisableViews(false);
        changeActivity();
        enableDisableViews(true);
    }

    public void onGetStartedClicked(View view) {
        if (activityGetStartedBinding.slider.getCurrentPosition() == 1) {
            changeActivity();
        } else {
            activityGetStartedBinding.slider.moveNextPosition(true);
        }

    }

    private void changeActivity() {
        App_Preferences.saveBooleanPref(GetStartedActivity.this, AppConstants.KEY_GET_STARTED_COMPLETED, true);
        Intent intent = new Intent(GetStartedActivity.this, LandingActivity.class);
        startActivity(intent);
        finish();
    }

    private void enableDisableViews(boolean status) {
        activityGetStartedBinding.rlSkip.setEnabled(status);
        activityGetStartedBinding.btnNext.setEnabled(status);
    }
}
