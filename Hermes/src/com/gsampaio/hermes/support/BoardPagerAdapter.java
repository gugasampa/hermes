package com.gsampaio.hermes.support;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class BoardPagerAdapter extends FragmentPagerAdapter{
	
	private List<Fragment> fragments;
    
	public BoardPagerAdapter(FragmentManager fm, List<Fragment> fragments){
		super(fm);
		this.fragments = fragments;
	}
    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

}
