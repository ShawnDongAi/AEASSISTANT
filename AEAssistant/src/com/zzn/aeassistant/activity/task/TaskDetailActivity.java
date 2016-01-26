package com.zzn.aeassistant.activity.task;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;

public class TaskDetailActivity extends BaseActivity {

	@Override
	protected int layoutResID() {
		return 0;
	}

	@Override
	protected int titleStringID() {
		return R.string.lable_task_detail;
	}

	@Override
	protected void initView() {
	}

	@Override
	protected boolean needLocation() {
		return false;
	}
}