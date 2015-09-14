package com.zzn.aeassistant.activity.post;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshListView;

public class WorkSpaceActivity extends BaseActivity {
	private PullToRefreshListView mListView;

	@Override
	protected int layoutResID() {
		return R.layout.activity_work_space;
	}

	@Override
	protected int titleStringID() {
		return R.string.title_work_space;
	}

	@Override
	protected void initView() {
		mListView = (PullToRefreshListView) findViewById(R.id.base_list);
	}

	@Override
	protected boolean needLocation() {
		return false;
	}

}
