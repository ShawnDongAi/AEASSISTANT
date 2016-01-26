package com.zzn.aeassistant.view.viewpager;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

public class SectionPage implements Serializable {
	private static final long serialVersionUID = -8308898594230089193L;
	private String title; // 节点标准
	private Bitmap icon; // 图标
	private Fragment fragment;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Bitmap getIcon() {
		return icon;
	}

	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}

	public Fragment getFragment() {
		return fragment;
	}

	public void setFragment(Fragment fragment) {
		this.fragment = fragment;
	}
}