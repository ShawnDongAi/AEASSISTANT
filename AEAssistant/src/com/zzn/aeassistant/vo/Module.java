package com.zzn.aeassistant.vo;

import com.zzn.aeassistant.fragment.BaseFragment;

public class Module {
	private int iconID;
	private int titleID;
	private BaseFragment fragment;

	public Module(int iconID, int titleID, BaseFragment fragment) {
		this.iconID = iconID;
		this.titleID = titleID;
		this.fragment = fragment;
	}

	public int getIconID() {
		return iconID;
	}

	public int getTitleID() {
		return titleID;
	}

	public BaseFragment getFragment() {
		return fragment;
	}
}
