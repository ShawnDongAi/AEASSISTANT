package com.zzn.aeassistant.activity.post;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.activity.project.ProjectStructureAdapter;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.Mode;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zzn.aeassistant.view.pulltorefresh.PullToRefreshListView;
import com.zzn.aeassistant.view.tree.Node;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.ProjectVO;

public class SendProjectActivity extends BaseActivity {

	private PullToRefreshListView pullListView;
	private ListView listView;
	private ListStructureTask listStruTask;
	private ProjectVO project;
	private long lastClickTime = 0;

	@Override
	protected int layoutResID() {
		return R.layout.activity_base_struct_list;
	}

	@Override
	protected int titleStringID() {
		return R.string.project_leaf;
	}

	@Override
	protected void initView() {
		findViewById(R.id.lable).setVisibility(View.GONE);
		pullListView = (PullToRefreshListView) findViewById(R.id.base_list);
		listView = pullListView.getRefreshableView();
		project = (ProjectVO) getIntent().getSerializableExtra(CodeConstants.KEY_PROJECT_VO);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (System.currentTimeMillis() - lastClickTime < 500) {
					return;
				}
				lastClickTime = System.currentTimeMillis();
				Node node = (Node) listView.getAdapter().getItem(position);
				ProjectVO projectVO = (ProjectVO) (node.getData());
				if (projectVO.getPROJECT_ID().equals(project.getPROJECT_ID())) {
					return;
				}
				Intent intent = new Intent();
				intent.putExtra(CodeConstants.KEY_PROJECT_VO, projectVO);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		initPullToRefresh();
	}

	private void initPullToRefresh() {
		pullListView.setMode(Mode.PULL_FROM_START);
		pullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				listStruTask = new ListStructureTask();
				listStruTask.execute(project.getROOT_ID());
			}
		});
		listStruTask = new ListStructureTask();
		listStruTask.execute(project.getROOT_ID());
		AEProgressDialog.showLoadingDialog(mContext);
	}

	@Override
	protected boolean needLocation() {
		return false;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (listStruTask != null) {
			listStruTask.cancel(true);
			listStruTask = null;
		}
		super.onDestroy();
	}

	private class ListStructureTask extends AsyncTask<String, Integer, HttpResult> {

		@Override
		protected HttpResult doInBackground(String... params) {
			String project_id = params[0];
			String param = "project_id=" + project_id;
			HttpResult result = AEHttpUtil.doPost(URLConstants.URL_PROJECT_STRUCTURE, param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			pullListView.onRefreshComplete();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				if (result.getRES_OBJ() != null && !StringUtil.isEmpty(result.getRES_OBJ().toString())) {
					List<ProjectVO> projectList = GsonUtil.getInstance().fromJson(result.getRES_OBJ().toString(),
							new TypeToken<List<ProjectVO>>() {
							}.getType());
					try {
						ProjectStructureAdapter<ProjectVO> adapter = new ProjectStructureAdapter<ProjectVO>(listView,
								mContext, projectList, true, "");
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
}
