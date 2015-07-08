package com.zzn.aeassistant.activity.attendance;

import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.View;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.multicolumn.MultiColumnListView;
import com.zzn.aeassistant.view.multicolumn.internal.PLA_AdapterView;
import com.zzn.aeassistant.view.multicolumn.internal.PLA_AdapterView.OnItemClickListener;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.Mode;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshMultiListView;
import com.zzn.aeassistant.vo.HttpResult;

public class SumByUsersActivity extends BaseActivity implements
		OnItemClickListener {
	private PullToRefreshMultiListView pullListView;
	private MultiColumnListView listView;
	private SumProjectAdapter adapter;
	private String startDate, endDate;
	private SumByUserTask sumByUserTask;
	private int page = 0;

	@Override
	protected int layoutResID() {
		return R.layout.activity_multi_list;
	}

	@Override
	protected int titleStringID() {
		return R.string.sum_by_user;
	}

	@Override
	protected void initView() {
		pullListView = (PullToRefreshMultiListView) findViewById(R.id.base_list);
		listView = pullListView.getRefreshableView();
		adapter = new SumProjectAdapter(mContext);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		startDate = getIntent().getStringExtra(CodeConstants.KEY_START_DATE);
		endDate = getIntent().getStringExtra(CodeConstants.KEY_END_DATE);
		initPullToRefresh();
	}

	@Override
	protected boolean needLocation() {
		return false;
	}

	@Override
	protected void onDestroy() {
		if (sumByUserTask != null) {
			sumByUserTask.cancel(true);
			sumByUserTask = null;
		}
		super.onDestroy();
	}

	private void initPullToRefresh() {
		pullListView.setMode(Mode.PULL_FROM_START);
		pullListView
				.setOnRefreshListener(new OnRefreshListener<MultiColumnListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<MultiColumnListView> refreshView) {
						String label = DateUtils.formatDateTime(
								getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						sumByUserTask = new SumByUserTask();
						sumByUserTask
								.execute(new String[] { startDate, endDate });
					}
				});
		pullListView.setRefreshing(true);
	}

	@Override
	public void onItemClick(PLA_AdapterView<?> parent, View view, int position,
			long id) {

	}

	private class SumByUserTask extends AsyncTask<String, Integer, HttpResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String startDate = params[0];
			String endDate = params[1];
			String param = "start_date=" + startDate + "&end_date=" + endDate
					+ "&user_id=" + AEApp.getCurrentUser().getUSER_ID()
					+ "&page=" + page;
			HttpResult result = AEHttpUtil.doPost(
					URLConstants.URL_SUM_BY_USERS, param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				page++;
			} else {
				ToastUtil.show(result.getRES_MESSAGE());
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
	}
}
