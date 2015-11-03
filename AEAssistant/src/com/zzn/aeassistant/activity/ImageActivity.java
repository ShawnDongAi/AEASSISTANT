package com.zzn.aeassistant.activity;

import java.util.List;

import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.view.touchimage.GalleryWidget.GalleryViewPager;
import com.zzn.aeassistant.view.touchimage.GalleryWidget.UrlPagerAdapter;

public class ImageActivity extends BaseActivity {
	private List<String> images;

	@Override
	protected int layoutResID() {
		return R.layout.activity_image;
	}

	@Override
	protected int titleStringID() {
		return 0;
	}

	@Override
	protected void initView() {
		images = getIntent().getStringArrayListExtra(CodeConstants.KEY_IMG_URL);
		int position = getIntent().getIntExtra(CodeConstants.KEY_POSITION, 0);
		UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(this, images);
		GalleryViewPager mViewPager = (GalleryViewPager) findViewById(R.id.viewer);
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setAdapter(pagerAdapter);
		title.setText((position + 1) + "/" + images.size());
		mViewPager.setCurrentItem(position);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				title.setText((arg0 + 1) + "/" + images.size());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	protected boolean needLocation() {
		return false;
	}
}