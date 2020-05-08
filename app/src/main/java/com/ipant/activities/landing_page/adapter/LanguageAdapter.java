package com.ipant.activities.landing_page.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipant.R;

public class LanguageAdapter extends ArrayAdapter<String> {
    Context mContext;
    String[] mNames;
    //int[] mImages;

    public LanguageAdapter(@NonNull Context context,String[] names) {
        super(context, R.layout.simple_spinner_item_with_flag,names);
        mContext = context;
        mNames = names;

     }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.simple_spinner_item_with_flag,null);
            TextView name = row.findViewById(R.id.language_code);
           // ImageView flag = row.findViewById(R.id.language_flag);

            name.setText(mNames[position]);
            //flag.setImageResource(mImages[position]);

        return row;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.simple_spinner_item_with_flag,null);
        TextView name = row.findViewById(R.id.language_code);
       // ImageView flag = row.findViewById(R.id.language_flag);

        name.setText(mNames[position]);
       // flag.setImageResource(mImages[position]);

        return row;
    }
}
