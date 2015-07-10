package com.zzn.aeassistant.activity.attendance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.reflect.TypeToken;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.activity.attendance.SumProjectAdapter.AttendanceItem;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.pinnedsection.PinnedSectionListView;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.Mode;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshPinnedListView;
import com.zzn.aeassistant.vo.AttendanceVO;
import com.zzn.aeassistant.vo.HttpResult;

public class SumByProListActivity extends BaseActivity implements
		OnItemClickListener {
	private PullToRefreshPinnedListView pullListView;
	private PinnedSectionListView listView;
	private SumProjectAdapter adapter;
	private AttendanceItem exceptionSection, normalSection, exceptionItem,
			normalItem;
	private String startDate, endDate, project_id;
	private SumByProjectTask sumByProTask;
	private SimpleDateFormat timeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	protected int layoutResID() {
		return R.layout.activity_pinned_list;
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
				getString(R.string.sum_exception_section), "");
		exceptionItem = new AttendanceItem(
				AttendanceItem.EXEPTION_ATTENDANCE_NULL,
				getString(R.string.sum_exception_null), "");
		normalSection = new AttendanceItem(AttendanceItem.SECTION, getString(
				R.string.sum_normal_section, 0), "");
		normalItem = new AttendanceItem(AttendanceItem.NORMAL_ATTENDANCE_NULL,
				getString(R.string.sum_normal_null), "");
		adapter.addItem(exceptionSection);
		adapter.addItem(exceptionItem);
		adapter.addItem(normalSection);
		adapter.addItem(normalItem);
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

	private long lastClickTime = 0;
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (System.currentTimeMillis() - lastClickTime < 500) {
			return;
		}
		lastClickTime = System.currentTimeMillis();
		AttendanceItem item = adapter.getItem(position - 1);
		if (item.type == AttendanceItem.EXEPTION_ATTENDANCE
				|| item.type == AttendanceItem.NORMAL_ATTENDANCE) {
			Intent intent = new Intent(mContext, AttendanceListActivity.class);
			intent.putParcelableArrayListExtra(
					CodeConstants.KEY_ATTENDENCE_LIST,
					(ArrayList<? extends Parcelable>) item.attendanceVOs);
			startActivity(intent);
		}
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
						sumByProTask.execute(new String[] { startDate, endDate,
								project_id });
					}
				});
		pullListView.setRefreshing(true);
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
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				if (result.getRES_OBJ() != null
						&& !StringUtil.isEmpty(result.getRES_OBJ().toString())) {
					try {
						JSONObject object = new JSONObject(result.getRES_OBJ()
								.toString());
						adapter.clear();
						adapter.addItem(exceptionSection);
						if (object.has("exception")) {
							List<AttendanceVO> exceptionAttendances = GsonUtil
									.getInstance()
									.fromJson(
											object.getString("exception"),
											new TypeToken<List<AttendanceVO>>() {
											}.getType());
							if (exceptionAttendances != null
									&& exceptionAttendances.size() > 0) {
								AttendanceItem item = new AttendanceItem(
										AttendanceItem.EXEPTION_ATTENDANCE,
										getString(R.string.sum_exception_total,
												exceptionAttendances.size()),
										"");
								item.attendanceVOs = exceptionAttendances;
								adapter.addItem(item);
							} else {
								adapter.addItem(exceptionItem);
							}
						} else {
							adapter.addItem(exceptionItem);
						}
						if (object.has("normal")) {
							List<AttendanceVO> normalAttendances = GsonUtil
									.getInstance()
									.fromJson(
											object.getString("normal"),
											new TypeToken<List<AttendanceVO>>() {
											}.getType());
							if (normalAttendances != null
									&& normalAttendances.size() > 0) {
								adapter.addItem(new AttendanceItem(
										AttendanceItem.SECTION, getString(
												R.string.sum_normal_section,
												normalAttendances.size()), ""));
								Map<String, List<AttendanceVO>> normalMap = new HashMap<String, List<AttendanceVO>>();
								for (AttendanceVO vo : normalAttendances) {
									String date = dateFormat.format(timeFormat
											.parse(vo.getDate()));
									if (!normalMap.containsKey(date)) {
										normalMap.put(date,
												new ArrayList<AttendanceVO>());
									}
									normalMap.get(date).add(vo);
								}
								for (String key : normalMap.keySet()) {
									AttendanceItem item = new AttendanceItem(
											AttendanceItem.NORMAL_ATTENDANCE,
											getString(
													R.string.sum_normal_total,
													normalMap.get(key).size()),
											key);
									item.attendanceVOs = normalMap.get(key);
									adapter.addItem(item);
								}
							} else {
								adapter.addItem(new AttendanceItem(
										AttendanceItem.SECTION,
										getString(R.string.sum_normal_section,
												0), ""));
								adapter.addItem(normalItem);
							}
						} else {
							adapter.addItem(new AttendanceItem(
									AttendanceItem.SECTION, getString(
											R.string.sum_normal_section, 0), ""));
							adapter.addItem(normalItem);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			pullListView.onRefreshComplete();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				adapter.notifyDataSetChanged();
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
