package com.zzn.aeassistant.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.IndexActivity;
import com.zzn.aeassistant.activity.IndexActivity.SaveClickListener;
import com.zzn.aeassistant.activity.post.PostActivity;
import com.zzn.aeassistant.activity.post.PostAdapter;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.util.ToolsUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.Mode;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshListView;
import com.zzn.aeassistant.vo.ProjectVO;

/**
 * 工作圈
 * 
 * @author Shawn
 */
public class WorkSpaceFragment extends BaseFragment {
	private TextView projectTitle;
	private PullToRefreshListView mListView;
	private PostAdapter adapter;
	private int page = 0;
	private boolean hasMore = true;
	private InitProjectTask initProTask;
	private ProjectVO project = null;
	private PopupWindow projectMenu;
	private ListView projectList;
	private ProListAdapter proListAdapter;

	@Override
	protected int layoutResID() {
		return R.layout.fragment_work_space;
	}

	@Override
	protected void initView(View container) {
		projectTitle = (TextView) container
				.findViewById(R.id.workspace_project);
		projectTitle.setOnClickListener(this);
		mListView = (PullToRefreshListView) container
				.findViewById(R.id.base_list);
		adapter = new PostAdapter(mContext);
		mListView.setAdapter(adapter);
		((IndexActivity) getActivity())
				.setOnSaveClickListener(new SaveClickListener() {
					@Override
					public void onSaveClick() {
						startActivity(new Intent(mContext, PostActivity.class));
					}
				});
		initMenuView();
		initPullToRefresh();
		initProTask = new InitProjectTask();
		initProTask.execute();
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
				projectTitle.setText(project.getPROJECT_NAME());
//				mListView.setRefreshing(true);
			}
		});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.workspace_project:
			if (!projectMenu.isShowing()) {
				projectMenu.showAsDropDown(projectTitle);
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onDestroyView() {
		if (initProTask != null) {
			initProTask.cancel(true);
			initProTask = null;
		}
		super.onDestroyView();
	}

	private void initPullToRefresh() {
		mListView.setMode(Mode.BOTH);
		mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(
					final PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(AEApp.getInstance(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				page = 0;
				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy(true, false)
						.setLastUpdatedLabel(label);
				// sumByProTask = new SumByProTask();
				// sumByProTask.execute(new String[] { startDate, endDate });
			}

			@Override
			public void onPullUpToRefresh(
					final PullToRefreshBase<ListView> refreshView) {
				if (hasMore) {
					// sumByProTask = new SumByProTask();
					// sumByProTask.execute(new String[] { startDate, endDate
					// });
				} else {
					refreshView.onRefreshComplete();
				}
			}
		});
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
				projectTitle.setText(result.getPROJECT_NAME());
//				listStruTask = new ListStructureTask();
//				listStruTask.execute(result.getPROJECT_ID());
//				AEProgressDialog.showLoadingDialog(mContext);
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
		}
	}
}
