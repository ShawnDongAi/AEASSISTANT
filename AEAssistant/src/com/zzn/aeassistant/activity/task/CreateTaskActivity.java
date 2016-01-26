package com.zzn.aeassistant.activity.task;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.vo.ProjectVO;

import android.widget.ListView;

public class CreateTaskActivity extends BaseActivity {
	private ProjectVO project;
	private ListView listView;

	@Override
	protected int layoutResID() {
		return R.layout.activity_base_pull_list;
	}

	@Override
	protected int titleStringID() {
		return R.string.lable_task_new;
	}

	@Override
	protected void initView() {
		project = (ProjectVO) getIntent().getSerializableExtra(CodeConstants.KEY_PROJECT_VO);
		listView = (ListView) findViewById(R.id.base_list);
	}

	@Override
	protected boolean needLocation() {
		return false;
	}
}