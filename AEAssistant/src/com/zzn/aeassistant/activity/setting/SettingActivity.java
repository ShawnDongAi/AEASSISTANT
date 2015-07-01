package com.zzn.aeassistant.activity.setting;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;

/**
 * 设置页
 * @author Shawn
 */
public class SettingActivity extends BaseActivity {

	@Override
	protected int layoutResID() {
		return R.layout.activity_setting;
	}

	@Override
	protected int titleStringID() {
		return R.string.settings;
	}

	@Override
	protected void initView() {
		
	}

	@Override
	protected boolean needLocation() {
		return false;
	}
}
