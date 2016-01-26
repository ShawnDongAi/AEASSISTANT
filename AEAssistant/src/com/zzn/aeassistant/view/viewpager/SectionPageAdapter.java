package com.zzn.aeassistant.view.viewpager;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionPageAdapter extends FragmentPagerAdapter {
	private List<SectionPage> datas = null;
	
	public SectionPageAdapter(FragmentManager fm, List<SectionPage> list) {
		super(fm);
		datas = list;
	}

	@Override
	public Fragment getItem(int position) {
		SectionPage page = datas.get(position);
		return page.getFragment();
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return datas.get(position).getTitle();
	}
}