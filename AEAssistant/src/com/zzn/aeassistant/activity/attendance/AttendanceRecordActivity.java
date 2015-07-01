package com.zzn.aeassistant.activity.attendance;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;

public class AttendanceRecordActivity extends BaseActivity {

	@Override
	protected int layoutResID() {
		return R.layout.activity_attendance_record;
	}

	@Override
	protected int titleStringID() {
		return R.string.title_attendance_record;
	}

	@Override
	protected void initView() {
		
	}

	@Override
	protected boolean needLocation() {
		return false;
	}

}
