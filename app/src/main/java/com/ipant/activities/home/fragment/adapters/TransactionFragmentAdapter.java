package com.ipant.activities.home.fragment.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ipant.activities.home.fragment.AllTransactionListFragment;

import java.util.ArrayList;
import java.util.List;


public class TransactionFragmentAdapter extends FragmentPagerAdapter {


    public List<String> fragmentTitles = new ArrayList<>();
    public List<Fragment> fragmentList = new ArrayList<>();

    private boolean isFragment;


    public TransactionFragmentAdapter(FragmentManager fragmentManager, boolean isFragment) {
        super(fragmentManager);

        this.isFragment = isFragment;

    }

    @Override
    public Fragment getItem(int position) {


        return AllTransactionListFragment.newInstance(position, isFragment);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitles.get(position);
    }

    public void addFragment(String name) {

        fragmentTitles.add(name);

    }
}
