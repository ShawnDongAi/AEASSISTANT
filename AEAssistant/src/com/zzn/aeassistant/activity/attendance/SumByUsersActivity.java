package com.zzn.aeassistant.activity.attendance;

import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.activity.attendance.SumProjectAdapter.AttendanceItem;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.pinnedsection.PinnedSectionListView;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.Mode;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshPinnedListView;
import com.zzn.aeassistant.vo.HttpResult;

public class SumByUsersActivity extends BaseActivity implements
		OnItemClickListener {
	private PullToRefreshPinnedListView pullListView;
	private PinnedSectionListView listView;
	private SumProjectAdapter adapter;
	private AttendanceItem exceptionSection, normalSection;
	private String startDate, endDate, project_id;
	private SumByProjectTask sumByProTask;

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
	protected void onDestroy() {
		if (sumByProTask != null) {
			sumByProTask.cancel(true);
			sumByProTask = null;
		}
		super.onDestroy();
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
						sumByProTask = new SumByProjectTask();
						sumByProTask.execute(new String[]{startDate, endDate, project_id});
					}
				});
		pullListView.setRefreshing(true);
	}

	private class SumByProjectTask extends AsyncTask<String, Integer, HttpResult> {

		@Override
		protected HttpResult doInBackground(String... params) {
			String startDate = params[0];
			String endDate = params[1];
			String project_id = params[2];
			String param = "start_date=" + startDate + "&end_date=" + endDate
					+ "&project_id=" + project_id;
			HttpResult result = AEHttpUtil.doPost(
					URLConstants.URL_SUM_BY_PROJECT, param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			pullListView.onRefreshComplete();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				
			} else {
				ToastUtil.show(result.getRES_MESSAGE());
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			pullListView.onRefreshComplete();
		}
	}
}
