package com.zzn.aeassistant.activity.user;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;

import android.widget.TextView;

public class AgreementActivity extends BaseActivity {
	private TextView content;

	@Override
	protected int layoutResID() {
		return R.layout.activity_text;
	}

	@Override
	protected int titleStringID() {
		return R.string.agreement;
	}

	@Override
	protected void initView() {
		content = (TextView) findViewById(R.id.content);
		content.setText(R.string.agreement_content);
	}

	@Override
	protected boolean needLocation() {
		return false;
	}
}
