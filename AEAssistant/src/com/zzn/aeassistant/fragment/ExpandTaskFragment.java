package com.zzn.aeassistant.fragment;

import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.task.ExpandTaskAdapter;
import com.zzn.aeassistant.activity.task.TaskDetailEditActivity;
import com.zzn.aeassistant.activity.task.TaskDetailViewActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.Mode;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshExpandableListView;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.ProjectVO;
import com.zzn.aeassistant.vo.TaskDetailVO;
import com.zzn.aeassistant.vo.TaskVO;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

public class ExpandTaskFragment extends BaseFragment implements OnChildClickListener {
	private int taskStatus = CodeConstants.STATUS_TASK_ALL;
	private ProjectVO project;
	private PullToRefreshExpandableListView mListView;
	private ExpandTaskAdapter mAdapter;
	private int page = 0;
	private boolean hasMore = true;
	private ListTask listTask;

	@Override
	protected int layoutResID() {
		return R.layout.layout_expand_list;
	}

	@Override
	protected void initView(View container) {
		Bundle bundle = getArguments();
		taskStatus = bundle.getInt(CodeConstants.KEY_TASK_STATUS);
		project = (ProjectVO) bundle.getSerializable(CodeConstants.KEY_PROJECT_VO);
		mListView = (PullToRefreshExpandableListView) container.findViewById(R.id.base_list);
		mListView.setEmptyView(View.inflate(mContext, R.layout.list_empty_view, null));
		mAdapter = new ExpandTaskAdapter(mContext, taskStatus == CodeConstants.STATUS_TASK_ALL);
		mListView.getRefreshableView().setAdapter(mAdapter);
		mListView.getRefreshableView().setOnChildClickListener(this);
		initPullToRefresh();
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		TaskDetailVO taskDetail = mAdapter.getChild(groupPosition, childPosition);
		Intent intent = new Intent();
		intent.putExtra(CodeConstants.KEY_TASK_DETAIL, taskDetail);
		if (taskDetail.getProcess_user_id().equals(project.getCREATE_USER()) && taskDetail.getStatus().equals("0")) {
			intent.setClass(mContext, TaskDetailEditActivity.class);
		} else {
			intent.setClass(mContext, TaskDetailViewActivity.class);
		}
		getActivity().startActivityForResult(intent, CodeConstants.REQUEST_CODE_REFRESH);
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case CodeConstants.REQUEST_CODE_REFRESH:
				// 刷新列表
				mListView.setRefreshing();
				break;
			default:
				break;
			}
		}
	}

	private void initPullToRefresh() {
		mListView.setMode(Mode.BOTH);
		mListView.setOnRefreshListener(new OnRefreshListener2<ExpandableListView>() {
			@Override
			public void onPullDownToRefresh(final PullToRefreshBase<ExpandableListView> refreshView) {
				String label = DateUtils.formatDateTime(AEApp.getInstance(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(label);
				if (listTask != null) {
					listTask.cancel(true);
				}
				page = 0;
				listTask = new ListTask();
				listTask.execute();
			}

			@Override
			public void onPullUpToRefresh(final PullToRefreshBase<ExpandableListView> refreshView) {
				if (listTask != null) {
					listTask.cancel(true);
				}
				listTask = new ListTask();
				listTask.execute();
			}
		});
		AEProgressDialog.showLoadingDialog(mContext);
		listTask = new ListTask();
		listTask.execute("0");
	}

	private class ListTask extends AsyncTask<String, Integer, HttpResult> {

		@Override
		protected HttpResult doInBackground(String... params) {
			StringBuilder param = new StringBuilder();
			param.append("root_id=" + project.getROOT_ID());
			param.append("&page=" + page);
			if (taskStatus == CodeConstants.STATUS_TASK_CREATE) {
				param.append("&user_id=" + project.getCREATE_USER());
			}
			return AEHttpUtil.doPost(URLConstants.URL_QUERY_TASK, param.toString());
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			mListView.onRefreshComplete();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				if (result.getRES_OBJ() != null && !StringUtil.isEmpty(result.getRES_OBJ().toString())) {
					try {
						List<TaskVO> taskList = GsonUtil.getInstance().fromJson(result.getRES_OBJ().toString(),
								new TypeToken<List<TaskVO>>() {
								}.getType());
						if (page == 0) {
							mAdapter.setDatas(taskList);
							for (int i = 0; i < mAdapter.getGroupCount(); i++) {
								mListView.getRefreshableView().expandGroup(i);
							}
						} else {
							mAdapter.addDatas(taskList);
							for (int i = mAdapter.getGroupCount() - taskList.size(); i < mAdapter
									.getGroupCount(); i++) {
								mListView.getRefreshableView().expandGroup(i);
							}
						}
						if (taskList.size() < 20) {
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
			if (!hasMore) {
				mListView.setMode(Mode.PULL_FROM_START);
			} else {
				mListView.setMode(Mode.BOTH);
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
			mListView.onRefreshComplete();
		}
	}
}