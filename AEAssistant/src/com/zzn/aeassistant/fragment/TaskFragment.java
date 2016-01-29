package com.zzn.aeassistant.fragment;

import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.task.TaskAdapter;
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
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshListView;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.ProjectVO;
import com.zzn.aeassistant.vo.TaskDetailVO;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TaskFragment extends BaseFragment implements OnItemClickListener {
	private ProjectVO project;
	private PullToRefreshListView mListView;
	private TaskAdapter mAdapter;
	private int page = 0;
	private boolean hasMore = true;
	private ListTask listTask;

	@Override
	protected int layoutResID() {
		return R.layout.layout_base_list;
	}

	@Override
	protected void initView(View container) {
		Bundle bundle = getArguments();
		project = (ProjectVO) bundle.getSerializable(CodeConstants.KEY_PROJECT_VO);
		mListView = (PullToRefreshListView) container.findViewById(R.id.base_list);
		mListView.setEmptyView(View.inflate(mContext, R.layout.list_empty_view, null));
		mAdapter = new TaskAdapter(mContext);
		mListView.getRefreshableView().setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		initPullToRefresh();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TaskDetailVO taskDetail = mAdapter.getItem(position - 1);
	}

	private void initPullToRefresh() {
		mListView.setMode(Mode.BOTH);
		mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(final PullToRefreshBase<ListView> refreshView) {
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
			public void onPullUpToRefresh(final PullToRefreshBase<ListView> refreshView) {
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
			param.append("&user_id=" + project.getCREATE_USER());
			return AEHttpUtil.doPost(URLConstants.URL_QUERY_MY_TASK, param.toString());
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			mListView.onRefreshComplete();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				if (result.getRES_OBJ() != null && !StringUtil.isEmpty(result.getRES_OBJ().toString())) {
					try {
						List<TaskDetailVO> taskList = GsonUtil.getInstance().fromJson(result.getRES_OBJ().toString(),
								new TypeToken<List<TaskDetailVO>>() {
								}.getType());
						if (page == 0) {
							mAdapter.setDatas(taskList);
						} else {
							mAdapter.addDatas(taskList);
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