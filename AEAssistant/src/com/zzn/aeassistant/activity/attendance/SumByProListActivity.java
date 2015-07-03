package com.zzn.aeassistant.activity.attendance;

import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.activity.attendance.SumProjectAdapter.AttendanceItem;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.view.pinnedsection.PinnedSectionListView;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.Mode;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshPinnedListView;

public class SumByProListActivity extends BaseActivity implements
		OnItemClickListener {
	private PullToRefreshPinnedListView pullListView;
	private PinnedSectionListView listView;
	private SumProjectAdapter adapter;
	private AttendanceItem exceptionSection, normalSection;
	private String startDate, endDate, project_id;

	@Override
	protected int layoutResID() {
		return R.layout.activity_base_list;
	}

	@Override
	protected int titleStringID() {
		return R.string.sum_by_project;
	}

	@Override
	protected void initView() {
		pullListView = (PullToRefreshPinnedListView) findViewById(R.id.base_list);
		listView = pullListView.getRefreshableView();
		adapter = new SumProjectAdapter(mContext);
		exceptionSection = new AttendanceItem(AttendanceItem.SECTION,
				getString(R.string.sum_exception_null), "");
		normalSection = new AttendanceItem(AttendanceItem.SECTION,
				getString(R.string.sum_normal_null), "");
		adapter.addItem(exceptionSection);
		adapter.addItem(normalSection);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		startDate = getIntent().getStringExtra(CodeConstants.KEY_START_DATE);
		endDate = getIntent().getStringExtra(CodeConstants.KEY_END_DATE);
		project_id = getIntent().getStringExtra(CodeConstants.KEY_PROJECT_ID);
		initPullToRefresh();
	}

	@Override
	protected boolean needLocation() {
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

	}
	
	private void initPullToRefresh() {
		pullListView.setMode(Mode.PULL_FROM_START);
		pullListView
				.setOnRefreshListener(new OnRefreshListener<PinnedSectionListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<PinnedSectionListView> refreshView) {
						String label = DateUtils.formatDateTime(
								getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						//刷新
					}
				});
		pullListView.setRefreshing(true);
	}
}
