package com.example.android.utabazzar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by deviprasadtripathy on 5/3/18.
 */

public class PagerActivity extends FragmentPagerAdapter {
    private Context mContext;
    Drawable myDrawable;

    public PagerActivity(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ProfileFragment();
        } else if (position == 1){
            return new ProfileFragment();
        } else if (position == 2){
            return new ProfileFragment();
        } else {
            return new ProfileFragment();
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return mContext.getString(R.string.category_usefulinfo);
            case 1:
                return mContext.getString(R.string.category_places);
            default:
                return null;
        }
    }

}
