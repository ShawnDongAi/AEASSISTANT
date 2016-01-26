package com.zzn.aeassistant.activity;

import java.util.ArrayList;
import java.util.List;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.view.viewpager.PagerSlidingTabStrip;
import com.zzn.aeassistant.view.viewpager.SectionPage;
import com.zzn.aeassistant.view.viewpager.SectionPageAdapter;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public abstract class BasePageActivity extends BaseActivity implements OnPageChangeListener {
	protected PagerSlidingTabStrip tab;
	protected ViewPager pager;
	protected List<SectionPage> pageList = new ArrayList<SectionPage>();
	protected SectionPageAdapter adapterPages;

	@Override
	protected int layoutResID() {
		return R.layout.activity_base_page;
	}

	@Override
	protected void initView() {
		tab = (PagerSlidingTabStrip) findViewById(R.id.base_page);
		pager = (ViewPager) findViewById(R.id.base_tab);
	}
}