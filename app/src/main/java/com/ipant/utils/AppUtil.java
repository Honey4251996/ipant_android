package com.ipant.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.ipant.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by krishnapal on 15/12/2017.
 */

public class AppUtil {
    private static final AppUtil ourInstance = new AppUtil();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
    private static Toast toast;
    private static Snackbar snackbar;
    private static CoordinatorLayout.LayoutParams params;

    public static AppUtil getInstance() {
        return ourInstance;
    }

    private AppUtil() {
    }


    public void showMessageError(Context context, CoordinatorLayout view, String message) {

        if (snackbar != null && snackbar.getView().isShown()) {
            snackbar.dismiss();
        }
        snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);
        params = (CoordinatorLayout.LayoutParams) snackbar.getView().getLayoutParams();
        params.gravity = Gravity.TOP;
        snackbar.getView().setLayoutParams(params);
        View snackbarView = snackbar.getView();
        TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);

        // Change the Snackbar default background color
        snackbarView.setBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.light_grey, null));
        snackbar.show();

    }


    public void hideKeyboard(@NonNull Activity activity) {
        try {
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
