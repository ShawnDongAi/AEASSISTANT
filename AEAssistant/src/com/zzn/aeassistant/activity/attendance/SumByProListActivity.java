package com.zzn.aeassistant.activity.attendance;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
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
import com.zzn.aeassistant.view.pinnedsection.PinnedSectionListView;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.Mode;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshListView;
import com.zzn.aeassistant.view.tree.Node;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.ProAttendanceVO;

public class SumByProListActivity extends BaseActivity implements
		OnItemClickListener {
	private PullToRefreshListView pullListView;
	private ListView listView;
	private String startDate, endDate, project_id;
	private SumByProjectTask sumByProTask;
	private TextView headerLable;

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
		pullListView = (PullToRefreshListView) findViewById(R.id.base_list);
		listView = pullListView.getRefreshableView();
		headerLable = (TextView) findViewById(R.id.lable);
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

	private long lastClickTime = 0;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (System.currentTimeMillis() - lastClickTime < 500) {
			return;
		}
		lastClickTime = System.currentTimeMillis();
		ProAttendanceVO vo = (ProAttendanceVO) (((Node) listView.getAdapter()
				.getItem(position)).getData());
		Intent intent = new Intent(mContext, AttendanceListActivity.class);
		intent.putExtra(CodeConstants.KEY_PROJECT_ID, vo.getProject_id());
		intent.putExtra(CodeConstants.KEY_START_DATE, startDate);
		intent.putExtra(CodeConstants.KEY_END_DATE, endDate);
		intent.putExtra(CodeConstants.KEY_TOTAL_COUNT, vo.getCount());
		intent.putExtra(CodeConstants.KEY_EXCEPTION_COUNT,
				vo.getException_count());
		startActivity(intent);
	}

	private void initPullToRefresh() {
		pullListView.setMode(Mode.PULL_FROM_START);
		pullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(
						getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				sumByProTask = new SumByProjectTask();
				sumByProTask.execute(new String[] { startDate, endDate,
						project_id });
			}
		});
		AEProgressDialog.showLoadingDialog(mContext);
		sumByProTask = new SumByProjectTask();
		sumByProTask.execute(new String[] { startDate, endDate, project_id });
	}

	private class SumByProjectTask extends
			AsyncTask<String, Integer, HttpResult> {

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
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				if (result.getRES_OBJ() != null
						&& !StringUtil.isEmpty(result.getRES_OBJ().toString())) {
					List<ProAttendanceVO> projectList = GsonUtil.getInstance()
							.fromJson(result.getRES_OBJ().toString(),
									new TypeToken<List<ProAttendanceVO>>() {
									}.getType());
					int totalCount = 0;
					int exceptionCount = 0;
					for (ProAttendanceVO vo : projectList) {
						totalCount += vo.getCount();
						exceptionCount += vo.getException_count();
					}
					headerLable.setText(getString(R.string.sum_pro_total,
							totalCount, exceptionCount));
					try {
						ProAttendanceAdapter<ProAttendanceVO> adapter = new ProAttendanceAdapter<ProAttendanceVO>(
								listView, mContext, projectList, true);
						listView.setAdapter(adapter);
						adapter.notifyDataSetChanged();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				ToastUtil.show(result.getRES_MESSAGE());
			}
			AEProgressDialog.dismissLoadingDialog();
			pullListView.onRefreshComplete();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			pullListView.onRefreshComplete();
			AEProgressDialog.dismissLoadingDialog();
		}
	}
}
