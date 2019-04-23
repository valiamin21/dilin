package com.example.amin.dictionande.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.amin.dictionande.views.fragment.BlankFragment;
import com.example.amin.dictionande.views.fragment.PlusOneFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment f = null;
        if (i == 0) {
            f = new BlankFragment();
        } else if (i == 1) {
            f = new PlusOneFragment();
        }
        return f;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String name = null;
        if(position == 0){
            name = "Blank Fragment";
        }else if(position == 1){
            name = "Plus One Fragment";
        }

        return name;
    }
}
