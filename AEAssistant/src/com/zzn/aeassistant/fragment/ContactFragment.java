package com.zzn.aeassistant.fragment;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.IndexActivity;
import com.zzn.aeassistant.activity.IndexActivity.SaveClickListener;
import com.zzn.aeassistant.activity.QRScanningActivity;
import com.zzn.aeassistant.activity.project.ProjectStructureAdapter;
import com.zzn.aeassistant.activity.user.UserDetailActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.util.ToolsUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.Mode;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshSwipeMenuListView;
import com.zzn.aeassistant.view.swipemenu.SwipeMenu;
import com.zzn.aeassistant.view.swipemenu.SwipeMenuCreator;
import com.zzn.aeassistant.view.swipemenu.SwipeMenuItem;
import com.zzn.aeassistant.view.swipemenu.SwipeMenuListView;
import com.zzn.aeassistant.view.swipemenu.SwipeMenuListView.OnMenuItemClickListener;
import com.zzn.aeassistant.view.tree.Node;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.ProjectVO;

public class ContactFragment extends BaseFragment {
	private TextView projectTitle;
	private PullToRefreshSwipeMenuListView pullListView;
	private SwipeMenuListView listView;
	private ProjectStructureAdapter<ProjectVO> adapter;
	private InitProjectTask initProTask;
	private ListStructureTask listStruTask;
	private ProjectVO project = null;
	private long lastClickTime = 0;
	private PopupWindow projectMenu;
	private ListView projectList;
	private ProListAdapter proListAdapter;
	// 接收到来电后的对话框
	private AlertDialog comingCallDialog;
	// 上一个来电的号码
	private String lastComingPhone = "";

	private UpdateParentTask updateParentTask;
	private DeleteProjectTask deleteProjectTask;

	@Override
	protected int layoutResID() {
		return R.layout.fragment_contact;
	}

	@Override
	protected void initView(View container) {
		projectTitle = (TextView) container.findViewById(R.id.contact_project);
		pullListView = (PullToRefreshSwipeMenuListView) container
				.findViewById(R.id.base_list);
		listView = pullListView.getRefreshableView();
		projectTitle.setOnClickListener(this);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (System.currentTimeMillis() - lastClickTime < 500) {
					return;
				}
				lastClickTime = System.currentTimeMillis();
				Node node = (Node) listView.getAdapter().getItem(position);
				// ProjectVO projectVO = (ProjectVO) (node.getData());
				Intent intent = new Intent(mContext, UserDetailActivity.class);
				intent.putExtra(CodeConstants.KEY_PROJECT_VO, node);
				intent.putExtra(CodeConstants.KEY_PROJECT_ID,
						project.getPROJECT_ID());
				startActivity(intent);
			}
		});
		initMenuView();
		initPullToRefresh();
	}

	@Override
	public void onResume() {
		super.onResume();
		// if (project != null) {
		// pullListView.setRefreshing(true);
		// }
		// proListAdapter.setDatas(AEApp.getCurrentUser().getPROJECTS());
		// proListAdapter.notifyDataSetChanged();
		if (project == null) {
			AEProgressDialog.showLoadingDialog(mContext);
			initProTask = new InitProjectTask();
			initProTask.execute();
		}
		((IndexActivity) getActivity())
				.setOnSaveClickListener(new SaveClickListener() {
					@Override
					public void onSaveClick() {
						if (project == null) {
							ToastUtil.show(R.string.null_project);
							return;
						}
						startActivityForResult(new Intent(mContext,
								QRScanningActivity.class),
								CodeConstants.REQUEST_CODE_QRCODE);
					}
				});
		proListAdapter.setDatas(AEApp.getCurrentUser().getPROJECTS());
		proListAdapter.notifyDataSetChanged();
	}

	private void initMenuView() {
		View menuView = View.inflate(mContext, R.layout.menu_list, null);
		projectList = (ListView) menuView.findViewById(R.id.menu_list);
		proListAdapter = new ProListAdapter(mContext, AEApp.getCurrentUser()
				.getPROJECTS());
		projectList.setAdapter(proListAdapter);
		projectMenu = new PopupWindow(menuView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		projectMenu.setBackgroundDrawable(getResources().getDrawable(
				R.color.transparent_lightslategray));
		projectMenu.setOutsideTouchable(true);
		projectMenu.setFocusable(true);
		projectList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (projectMenu != null && projectMenu.isShowing()) {
					projectMenu.dismiss();
				}
				project = proListAdapter.getItem(position);
				projectTitle.setText(project.getROOT_PROJECT_NAME() + "-"
						+ project.getPROJECT_NAME());
				pullListView.setRefreshing(true);
			}
		});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.contact_project:
			if (!projectMenu.isShowing()) {
				projectMenu.showAsDropDown(projectTitle);
			}
			break;
		default:
			break;
		}
	}

	private void initPullToRefresh() {
		pullListView.setMode(Mode.PULL_FROM_START);
		pullListView
				.setOnRefreshListener(new OnRefreshListener<SwipeMenuListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<SwipeMenuListView> refreshView) {
						String label = DateUtils.formatDateTime(mContext,
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						listStruTask = new ListStructureTask();
						if (project != null) {
							listStruTask.execute(project.getPROJECT_ID());
						}
					}
				});
		initSwipeMenu();
	}

	private void initSwipeMenu() {
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				switch (menu.getViewType()) {
				case 0:
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
				Node node = adapter.getItem(position);
				ProjectVO item = (ProjectVO) (node.getData());
				deleteProjectTask = new DeleteProjectTask();
				deleteProjectTask.execute(item.getPROJECT_ID());
				return false;
			}
		});
		listView.setOpenInterpolator(new DecelerateInterpolator(1.0f));
		listView.setCloseInterpolator(new DecelerateInterpolator(1.0f));
	}

	@Override
	public boolean onBackPressed() {
		if (projectMenu != null && projectMenu.isShowing()) {
			projectMenu.dismiss();
			return true;
		}
		return super.onBackPressed();
	}

	@Override
	public void onDestroyView() {
		if (initProTask != null) {
			initProTask.cancel(true);
			initProTask = null;
		}
		if (listStruTask != null) {
			listStruTask.cancel(true);
			listStruTask = null;
		}
		if (deleteProjectTask != null) {
			deleteProjectTask.cancel(true);
			deleteProjectTask = null;
		}
		super.onDestroyView();
	}

	private class InitProjectTask extends
			AsyncTask<ProjectVO, Integer, ProjectVO> {

		@Override
		protected ProjectVO doInBackground(ProjectVO... params) {
			ProjectVO currentProject = null;
			if (AEApp.getCurrentLoc() == null) {
				if (AEApp.getCurrentUser().getPROJECTS() != null
						&& AEApp.getCurrentUser().getPROJECTS().size() > 0) {
					currentProject = AEApp.getCurrentUser().getPROJECTS()
							.get(0);
				}
			} else {
				double currentLatitude = AEApp.getCurrentLoc().getLatitude();
				double currentLongitude = AEApp.getCurrentLoc().getLongitude();
				if (AEApp.getCurrentUser().getPROJECTS() != null
						&& AEApp.getCurrentUser().getPROJECTS().size() > 0) {
					currentProject = AEApp.getCurrentUser().getPROJECTS()
							.get(0);
				}
				for (ProjectVO projectVO : AEApp.getCurrentUser().getPROJECTS()) {
					double proLatitude = Double.parseDouble(projectVO
							.getLATITUDE());
					double proLongitude = Double.parseDouble(projectVO
							.getLONGITUDE());
					if (ToolsUtil.getDistance(currentLongitude,
							currentLatitude, proLongitude, proLatitude) < 500) {
						currentProject = projectVO;
						break;
					}
				}
			}
			return currentProject;
		}

		@Override
		protected void onPostExecute(ProjectVO result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			project = result;
			if (result != null) {
				projectTitle.setText(result.getROOT_PROJECT_NAME() + "-"
						+ result.getPROJECT_NAME());
				listStruTask = new ListStructureTask();
				listStruTask.execute(result.getPROJECT_ID());
				AEProgressDialog.showLoadingDialog(mContext);
			} else {
				projectTitle.setText(R.string.null_project);
				pullListView.setMode(Mode.PULL_FROM_START);
				return;
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
		}
	}

	private class ListStructureTask extends
			AsyncTask<String, Integer, HttpResult> {

		@Override
		protected HttpResult doInBackground(String... params) {
			String project_id = params[0];
			if (StringUtil.isEmpty(project_id)) {
				return null;
			}
			String param = "project_id=" + project_id;
			HttpResult result = AEHttpUtil.doPost(
					URLConstants.URL_PROJECT_STRUCTURE, param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			pullListView.onRefreshComplete();
			if (result == null) {
				return;
			}
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				if (result.getRES_OBJ() != null
						&& !StringUtil.isEmpty(result.getRES_OBJ().toString())) {
					List<ProjectVO> projectList = GsonUtil.getInstance()
							.fromJson(result.getRES_OBJ().toString(),
									new TypeToken<List<ProjectVO>>() {
									}.getType());
					try {
						adapter = new ProjectStructureAdapter<ProjectVO>(
								listView, mContext, projectList, true,
								project.getPROJECT_ID());
						listView.setAdapter(adapter);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				ToastUtil.show(result.getRES_MESSAGE());
			}
		}

		@Override
		protected void onCancelled() {
			AEProgressDialog.dismissLoadingDialog();
			pullListView.onRefreshComplete();
			super.onCancelled();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case CodeConstants.REQUEST_CODE_QRCODE:
				if (comingCallDialog != null && comingCallDialog.isShowing()) {
					return;
				}
				String result = data
						.getStringExtra(CodeConstants.KEY_SCAN_RESULT);
				try {
					String phone = GsonUtil.getInstance()
							.fromJson(result, HashMap.class).get("user_phone")
							.toString();
					lastComingPhone = phone;
					comingCallDialog = new AlertDialog.Builder(mContext)
							.setTitle(R.string.warning)
							.setMessage(
									getString(R.string.project_join_current,
											lastComingPhone))
							.setPositiveButton(R.string.confirm,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// 迁移
											if (updateParentTask != null) {
												updateParentTask.cancel(true);
												updateParentTask = null;
											}
											updateParentTask = new UpdateParentTask();
											updateParentTask.execute(new String[] {
													project.getPROJECT_ID(),
													lastComingPhone });
										}
									}).setNegativeButton(R.string.cancel, null)
							.create();
					comingCallDialog.setCanceledOnTouchOutside(false);
					comingCallDialog.show();
				} catch (Exception e) {
					e.printStackTrace();
					ToastUtil.show(R.string.error_qrcode);
				}
				break;
			default:
				break;
			}
		}
	}

	private class UpdateParentTask extends
			AsyncTask<String, Integer, HttpResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			AEProgressDialog.showLoadingDialog(mContext);
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String project_id = params[0];
			String phone = params[1];
			String param = "parent_project_id=" + project_id
					+ "&leaf_user_phone=" + phone;
			HttpResult result = AEHttpUtil.doPost(
					URLConstants.URL_UPDATE_PARENT, param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				pullListView.setRefreshing(true);
				ToastUtil.show(result.getRES_MESSAGE());
			} else {
				ToastUtil.showImp(getActivity(), result.getRES_MESSAGE());
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
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
			String param = "user_id=" + AEApp.getCurrentUser().getUSER_ID()
					+ "&project_id=" + project_id;
			HttpResult result = AEHttpUtil.doPost(
					URLConstants.URL_DELETE_PRJECT, param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				try {
					pullListView.setRefreshing(true);
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