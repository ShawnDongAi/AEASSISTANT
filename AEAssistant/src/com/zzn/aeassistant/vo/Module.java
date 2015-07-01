package com.zzn.aeassistant.vo;

import android.content.Intent;

public class Module {
	private int iconID;
	private int titleID;
	private Intent intent;

	public Module(int iconID, int titleID, Intent intent) {
		this.iconID = iconID;
		this.titleID = titleID;
		this.intent = intent;
	}

	public int getIconID() {
		return iconID;
	}

	public int getTitleID() {
		return titleID;
	}

	public Intent getIntent() {
		return intent;
	}
}
