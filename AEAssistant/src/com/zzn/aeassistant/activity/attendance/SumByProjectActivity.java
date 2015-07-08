package com.zzn.aeassistant.activity.attendance;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.baidu.location.BDLocation;
import com.google.gson.reflect.TypeToken;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.activity.project.ProjectAdapter;
import com.zzn.aeassistant.activity.project.ProjectAdapter.ProjectItem;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.util.ToolsUtil;
import com.zzn.aeassistant.view.pinnedsection.PinnedSectionListView;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.Mode;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshPinnedListView;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.ProjectVO;

public class SumByProjectActivity extends BaseActivity implements
		OnItemClickListener {
	private PullToRefreshPinnedListView pullListView;
	private PinnedSectionListView listView;
	private ProjectAdapter adapter;
	private ProjectItem currentSection, managerSection;
	private ListProjectTask listTask;
	private InitProjectTask initProjectTask;
	private String startDate, endDate;

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
		adapter = new ProjectAdapter(mContext);
		currentSection = new ProjectItem(ProjectItem.SECTION,
				getString(R.string.project_current));
		managerSection = new ProjectItem(ProjectItem.SECTION,
				getString(R.string.project_manager));
		adapter.addItem(currentSection);
		adapter.addItem(managerSection);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		initPullToRefresh();
		startDate = getIntent().getStringExtra(CodeConstants.KEY_START_DATE);
		endDate = getIntent().getStringExtra(CodeConstants.KEY_END_DATE);
		initProjectTask = new InitProjectTask();
		if (AEApp.getCurrentLoc() == null) {
			initProjectTask.execute(new Double[] { 0.0, 0.0 });
		} else {
			initProjectTask.execute(new Double[] {
					AEApp.getCurrentLoc().getLatitude(),
					AEApp.getCurrentLoc().getLongitude() });
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
						listTask = new ListProjectTask();
						listTask.execute();
					}
				});
	}

	private class ListProjectTask extends
			AsyncTask<String, Integer, HttpResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String param = "user_id=" + AEApp.getCurrentUser().getUSER_ID();
			HttpResult result = AEHttpUtil.doPost(
					URLConstants.URL_PROJECT_MANAGER_LIST, param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			pullListView.onRefreshComplete();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				try {
					List<ProjectVO> projects = GsonUtil.getInstance().fromJson(
							result.getRES_OBJ().toString(),
							new TypeToken<List<ProjectVO>>() {
							}.getType());
					AEApp.getCurrentUser().setPROJECTS(projects);
					if (initProjectTask != null) {
						initProjectTask.cancel(true);
						initProjectTask = null;
					}
					initProjectTask = new InitProjectTask();
					if (AEApp.getCurrentLoc() == null) {
						initProjectTask.execute(new Double[] { 0.0, 0.0 });
					} else {
						initProjectTask.execute(new Double[] {
								AEApp.getCurrentLoc().getLatitude(),
								AEApp.getCurrentLoc().getLongitude() });
					}
				} catch (Exception e) {
					e.printStackTrace();
					ToastUtil.show(R.string.http_error);
				}
			} else {
				ToastUtil.show(result.getRES_MESSAGE());
			}
		}

		@Override
		protected void onCancelled() {
			pullListView.onRefreshComplete();
			super.onCancelled();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CodeConstants.REQUEST_CODE_REFRESH:
				if (pullListView != null) {
					pullListView.setRefreshing(true);
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ProjectItem item = adapter.getItem(position - 1);
		if (item.type == ProjectItem.SECTION) {
			return;
		}
		ProjectVO vo = item.project;
		if (vo != null) {
			Intent intent = new Intent(this, SumByProListActivity.class);
			intent.putExtra(CodeConstants.KEY_START_DATE, startDate);
			intent.putExtra(CodeConstants.KEY_END_DATE, endDate);
			intent.putExtra(CodeConstants.KEY_PROJECT_ID, vo.getPROJECT_ID());
			startActivity(intent);
		}
	}

	@Override
	protected void onDestroy() {
		if (listTask != null) {
			listTask.cancel(true);
			listTask = null;
		}
		if (initProjectTask != null) {
			initProjectTask.cancel(true);
			initProjectTask = null;
		}
		super.onDestroy();
	}

	@Override
	protected boolean needLocation() {
		return true;
	}

	@Override
	protected void onActivityReceiveLocation(BDLocation location) {
		super.onActivityReceiveLocation(location);
		AEApp.setCurrentLoc(location);
		if (initProjectTask != null) {
			initProjectTask.cancel(true);
			initProjectTask = null;
		}
		initProjectTask = new InitProjectTask();
		if (location == null) {
			ToastUtil.show(R.string.location_failed);
			initProjectTask.execute(new Double[] { 0.0, 0.0 });
			return;
		}
		initProjectTask.execute(new Double[] { location.getLatitude(),
				location.getLongitude() });
	}

	private class InitProjectTask extends AsyncTask<Double, Integer, ProjectVO> {

		@Override
		protected ProjectVO doInBackground(Double... params) {
			double currentLatitude = params[0];
			double currentLongitude = params[1];
			ProjectVO currentProject = null;
			for (ProjectVO projectVO : AEApp.getCurrentUser().getPROJECTS()) {
				double proLatitude = Double
						.parseDouble(projectVO.getLATITUDE());
				double proLongitude = Double.parseDouble(projectVO
						.getLONGITUDE());
				if (ToolsUtil.getDistance(currentLongitude, currentLatitude,
						proLongitude, proLatitude) < 500) {
					currentProject = projectVO;
					break;
				}
			}
			return currentProject;
		}

		@Override
		protected void onPostExecute(ProjectVO result) {
			super.onPostExecute(result);
			adapter.clear();
			adapter.addItem(currentSection);
			if (result == null) {
				ProjectItem currentProject = new ProjectItem(
						ProjectItem.CURRENT_PROJECT_NULL,
						getString(R.string.out_of_project_location));
				adapter.addItem(currentProject);
			} else {
				ProjectItem currentProject = new ProjectItem(
						ProjectItem.CURRENT_PROJECT, result.getPROJECT_NAME());
				currentProject.project = result;
				adapter.addItem(currentProject);
			}
			adapter.addItem(managerSection);
			for (ProjectVO managerVO : AEApp.getCurrentUser().getPROJECTS()) {
				ProjectItem managerProject = new ProjectItem(
						ProjectItem.MANAGER_PROJECT,
						managerVO.getPROJECT_NAME());
				managerProject.project = managerVO;
				adapter.addItem(managerProject);
			}
			if (AEApp.getCurrentUser().getPROJECTS().size() <= 0) {
				ProjectItem managerProject = new ProjectItem(
						ProjectItem.MANAGER_PROJECT_NULL,
						getString(R.string.null_project));
				adapter.addItem(managerProject);
			}
			adapter.notifyDataSetChanged();
		}
	}
}
