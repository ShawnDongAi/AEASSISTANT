package com.zzn.aeassistant.activity.attendance;

import java.util.List;

import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.pla.MultiColumnListView;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.Mode;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshPLAListView;
import com.zzn.aeassistant.vo.AttendanceVO;
import com.zzn.aeassistant.vo.HttpResult;

public class AttendanceListActivity extends BaseActivity {
	private PullToRefreshPLAListView pullListView;
	private MultiColumnListView listView;
	private TextView headerLable;
	private SumUserAdapter adapter;
	private String project_id;
	private String startDate, endDate;
	private int page = 0;
	private boolean hasMore = true;
	private SumByProTask sumByProTask;

	@Override
	protected int layoutResID() {
		return R.layout.activity_staggered_list;
	}

	@Override
	protected int titleStringID() {
		return R.string.sum_by_project;
	}

	@Override
	protected void initView() {
		pullListView = (PullToRefreshPLAListView) findViewById(R.id.base_list);
		listView = pullListView.getRefreshableView();
		headerLable = (TextView) findViewById(R.id.lable);

		adapter = new SumUserAdapter(mContext);
		listView.setAdapter(adapter);
		int totalCount = getIntent().getIntExtra(CodeConstants.KEY_TOTAL_COUNT,
				0);
		int exceptionCount = getIntent().getIntExtra(
				CodeConstants.KEY_EXCEPTION_COUNT, 0);
		headerLable.setText(getString(R.string.sum_pro_total, totalCount,
				exceptionCount));
		project_id = getIntent().getStringExtra(CodeConstants.KEY_PROJECT_ID);
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
		if (sumByProTask != null) {
			sumByProTask.cancel(true);
			sumByProTask = null;
		}
		super.onDestroy();
	}

	private void initPullToRefresh() {
		pullListView.setMode(Mode.BOTH);
		pullListView
				.setOnRefreshListener(new OnRefreshListener2<MultiColumnListView>() {
					@Override
					public void onPullDownToRefresh(
							final PullToRefreshBase<MultiColumnListView> refreshView) {
						String label = DateUtils.formatDateTime(
								getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						page = 0;
						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy(true, false)
								.setLastUpdatedLabel(label);
						sumByProTask = new SumByProTask();
						sumByProTask
								.execute(new String[] { startDate, endDate });
					}

					@Override
					public void onPullUpToRefresh(
							final PullToRefreshBase<MultiColumnListView> refreshView) {
						if (hasMore) {
							sumByProTask = new SumByProTask();
							sumByProTask.execute(new String[] { startDate,
									endDate });
						} else {
							refreshView.onRefreshComplete();
						}
					}
				});
		AEProgressDialog.showLoadingDialog(mContext);
		sumByProTask = new SumByProTask();
		sumByProTask.execute(new String[] { startDate, endDate });
	}

	private class SumByProTask extends AsyncTask<String, Integer, HttpResult> {

		@Override
		protected HttpResult doInBackground(String... params) {
			String startDate = params[0];
			String endDate = params[1];
			String param = "start_date=" + startDate + "&end_date=" + endDate
					+ "&project_id=" + project_id + "&page=" + page;
			HttpResult result = AEHttpUtil.doPost(
					URLConstants.URL_SUM_LIST_BY_PROJECT, param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				if (result.getRES_OBJ() != null
						&& !StringUtil.isEmpty(result.getRES_OBJ().toString())) {
					try {
						List<AttendanceVO> attendances = GsonUtil.getInstance()
								.fromJson(result.getRES_OBJ().toString(),
										new TypeToken<List<AttendanceVO>>() {
										}.getType());
						if (page == 0) {
							adapter.clear();
						}
						adapter.addData(attendances);
						if (attendances.size() < 20) {
							hasMore = false;
						}
						page++;
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					hasMore = false;
				}
			} else {
				ToastUtil.show(result.getRES_MESSAGE());
			}
			AEProgressDialog.dismissLoadingDialog();
			pullListView.onRefreshComplete();
			if (!hasMore) {
				pullListView
						.getLoadingLayoutProxy(false, true)
						.setPullLabel(
								getString(R.string.pull_to_refresh_from_bottom_null_data));
				pullListView
						.getLoadingLayoutProxy(false, true)
						.setRefreshingLabel(
								getString(R.string.pull_to_refresh_from_bottom_null_data));
				pullListView
						.getLoadingLayoutProxy(false, true)
						.setReleaseLabel(
								getString(R.string.pull_to_refresh_from_bottom_null_data));
				pullListView.getLoadingLayoutProxy(false, true)
						.setLoadingDrawable(null);
			} else {
				pullListView
						.getLoadingLayoutProxy(false, true)
						.setPullLabel(
								getString(R.string.pull_to_refresh_from_bottom_pull_label));
				pullListView
						.getLoadingLayoutProxy(false, true)
						.setRefreshingLabel(
								getString(R.string.pull_to_refresh_from_bottom_refreshing_label));
				pullListView
						.getLoadingLayoutProxy(false, true)
						.setReleaseLabel(
								getString(R.string.pull_to_refresh_from_bottom_release_label));
				pullListView.getLoadingLayoutProxy(false, true)
						.setLoadingDrawable(
								getResources().getDrawable(
										R.drawable.default_ptr_rotate));
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
			pullListView.onRefreshComplete();
		}
	}
}