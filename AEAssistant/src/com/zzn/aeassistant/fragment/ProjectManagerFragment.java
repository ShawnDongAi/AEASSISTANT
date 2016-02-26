package com.zzn.aeassistant.fragment;

import java.util.List;

import com.baidu.location.BDLocation;
import com.google.gson.reflect.TypeToken;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.project.CreateProjectActivity;
import com.zzn.aeassistant.activity.project.ProjectAdapter;
import com.zzn.aeassistant.activity.project.ProjectAdapter.ProjectItem;
import com.zzn.aeassistant.activity.task.TaskActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.util.ToolsUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.pinnedsection.PinnedSectionSwipeMenuListView;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.Mode;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshPinnedSwipeMenuListView;
import com.zzn.aeassistant.view.swipemenu.SwipeMenu;
import com.zzn.aeassistant.view.swipemenu.SwipeMenuCreator;
import com.zzn.aeassistant.view.swipemenu.SwipeMenuItem;
import com.zzn.aeassistant.view.swipemenu.SwipeMenuListView.OnMenuItemClickListener;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.ProjectVO;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;

public class ProjectManagerFragment extends BaseFragment implements
		OnItemClickListener {
	private PullToRefreshPinnedSwipeMenuListView pullListView;
	private PinnedSectionSwipeMenuListView listView;
	private ImageButton btnAdd;
	private ProjectAdapter adapter;
	private ProjectItem currentSection, managerSection;
	private ListProjectTask listTask;
	private InitProjectTask initProjectTask;
	private DeleteProjectTask deleteProjectTask;

	@Override
	protected int layoutResID() {
		return R.layout.activity_project_manager;
	}

	@Override
	protected void initView(View container) {
		btnAdd = (ImageButton) container.findViewById(R.id.project_btn_add);
		btnAdd.setOnClickListener(this);
		pullListView = (PullToRefreshPinnedSwipeMenuListView) container.findViewById(R.id.project_listview);
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
		initSwipeMenu();
		initPullToRefresh();
		initProjectTask = new InitProjectTask();
		if (AEApp.getCurrentLoc() == null) {
			initProjectTask.execute(new Double[] { 0.0, 0.0 });
		} else {
			initProjectTask.execute(new Double[] {
					AEApp.getCurrentLoc().getLatitude(),
					AEApp.getCurrentLoc().getLongitude() });
		}
	}

	private void initSwipeMenu() {
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				switch (menu.getViewType()) {
				case ProjectItem.CURRENT_PROJECT:
				case ProjectItem.MANAGER_PROJECT:
					SwipeMenuItem item = new SwipeMenuItem(mContext);
					item.setBackground(R.drawable.swipe_menu_item1);
					item.setWidth(ToolsUtil.dip2px(mContext, 90));
					item.setTitle(R.string.delete);
					item.setTitleSize(18);
					item.setTitleColor(Color.WHITE);
					menu.addMenuItem(item);
					break;
				default:
					break;
				}
			}
		};
		listView.setMenuCreator(creator);
		listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu,
					int index) {
				ProjectVO item = adapter.getItem(position).project;
				deleteProjectTask = new DeleteProjectTask();
				deleteProjectTask.execute(item.getPROJECT_ID());
				return false;
			}
		});
		listView.setOpenInterpolator(new DecelerateInterpolator(1.0f));
		listView.setCloseInterpolator(new DecelerateInterpolator(1.0f));
	}

	private void initPullToRefresh() {
		pullListView.setMode(Mode.PULL_FROM_START);
		pullListView
				.setOnRefreshListener(new OnRefreshListener<PinnedSectionSwipeMenuListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<PinnedSectionSwipeMenuListView> refreshView) {
						String label = DateUtils.formatDateTime(
								mContext,
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

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.project_btn_add:
			startActivityForResult(
					new Intent(mContext, CreateProjectActivity.class),
					CodeConstants.REQUEST_CODE_REFRESH);
			break;
		default:
			break;
		}
	}

	private class ListProjectTask extends
			AsyncTask<String, Integer, HttpResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String param = "user_id="
					+ AEApp.getCurrentUser()
							.getUSER_ID();
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
					AEApp.getCurrentUser()
							.setPROJECTS(projects);
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
		if (resultCode == Activity.RESULT_OK) {
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

	private long lastClickTime = 0;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (System.currentTimeMillis() - lastClickTime < 500) {
			return;
		}
		lastClickTime = System.currentTimeMillis();
		ProjectItem item = adapter.getItem(position - 1);
		if (item.type == ProjectItem.SECTION) {
			return;
		}
		ProjectVO vo = item.project;
		if (vo != null) {
			Intent taskIntent = new Intent(mContext, TaskActivity.class);
			taskIntent.putExtra(CodeConstants.KEY_PROJECT_VO, vo);
			startActivity(taskIntent);
		}
	}

	@Override
	public void onDestroyView() {
		if (listTask != null) {
			listTask.cancel(true);
			listTask = null;
		}
		if (initProjectTask != null) {
			initProjectTask.cancel(true);
			initProjectTask = null;
		}
		if (deleteProjectTask != null) {
			deleteProjectTask.cancel(true);
			deleteProjectTask = null;
		}
		super.onDestroy();
	}

	@Override
	public void onActivityReceiveLocation(BDLocation location) {
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
			if (adapter == null) {
				return;
			}
			adapter.clear();
			adapter.addItem(currentSection);
			if (result == null) {
				ProjectItem currentProject = new ProjectItem(
						ProjectItem.CURRENT_PROJECT_NULL,
						getString(R.string.out_of_project_location));
				adapter.addItem(currentProject);
			} else {
				ProjectItem currentProject = new ProjectItem(
						ProjectItem.CURRENT_PROJECT, result.getROOT_PROJECT_NAME());
				currentProject.project = result;
				adapter.addItem(currentProject);
			}
			adapter.addItem(managerSection);
			for (ProjectVO managerVO : AEApp.getCurrentUser().getPROJECTS()) {
				ProjectItem managerProject = new ProjectItem(
						ProjectItem.MANAGER_PROJECT,
						managerVO.getROOT_PROJECT_NAME());
				managerProject.project = managerVO;
				adapter.addItem(managerProject);
			}
			if (AEApp.getCurrentUser().getPROJECTS()
					.size() <= 0) {
				ProjectItem managerProject = new ProjectItem(
						ProjectItem.MANAGER_PROJECT_NULL,
						getString(R.string.null_project));
				adapter.addItem(managerProject);
			}
			adapter.notifyDataSetChanged();
		}
	}

	private class DeleteProjectTask extends
			AsyncTask<String, Integer, HttpResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			AEProgressDialog.showLoadingDialog(mContext);
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String project_id = params[0];
			String param = "user_id="
					+ AEApp.getCurrentUser()
							.getUSER_ID() + "&project_id=" + project_id;
			HttpResult result = AEHttpUtil.doPost(
					URLConstants.URL_DELETE_PRJECT, param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				initProjectTask = new InitProjectTask();
				try {
					List<ProjectVO> projects = GsonUtil.getInstance().fromJson(
							result.getRES_OBJ().toString(),
							new TypeToken<List<ProjectVO>>() {
							}.getType());
					AEApp.getCurrentUser()
							.setPROJECTS(projects);
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
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
		}
	}
}
