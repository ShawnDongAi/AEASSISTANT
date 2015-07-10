package com.zzn.aeassistant.activity.project;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

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
import com.zzn.aeassistant.view.tree.Node;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.ProjectVO;

public class ProjectStructureActivity extends BaseActivity {
	private ListView listView;
	private ProjectStructureAdapter<ProjectVO> defaultAdapter;
	private ListStructureTask listStruTask;
	private String project_id;
	private long lastClickTime = 0;

	@Override
	protected int layoutResID() {
		return R.layout.activity_base_list;
	}

	@Override
	protected int titleStringID() {
		return R.string.project_leaf;
	}

	@Override
	protected void initView() {
		listView = (ListView) findViewById(R.id.base_list);
		project_id = getIntent().getStringExtra(CodeConstants.KEY_PROJECT_ID);
		try {
			defaultAdapter = new ProjectStructureAdapter<ProjectVO>(listView,
					mContext, new ArrayList<ProjectVO>(), true);
			listView.setAdapter(defaultAdapter);
			defaultAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
		listStruTask = new ListStructureTask();
		listStruTask.execute(project_id);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (System.currentTimeMillis() - lastClickTime < 500) {
					return;
				}
				lastClickTime = System.currentTimeMillis();
				ProjectVO project = (ProjectVO) (((Node) listView.getAdapter()
						.getItem(position)).getData());
				try {
					Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
							+ project.getCREATE_USER_PHONE()));
					startActivity(intent);
				} catch (Exception e) {
					ToastUtil.show(R.string.dial_error);
				}
			}
		});
	}

	@Override
	protected boolean needLocation() {
		return false;
	}

	@Override
	protected void onDestroy() {
		if (listStruTask != null) {
			listStruTask.cancel(true);
			listStruTask = null;
		}
		super.onDestroy();
	}

	private class ListStructureTask extends
			AsyncTask<String, Integer, HttpResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			AEProgressDialog.showLoadingDialog(mContext);
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String project_id = params[0];
			String param = "project_id=" + project_id;
			HttpResult result = AEHttpUtil.doPost(
					URLConstants.URL_PROJECT_STRUCTURE, param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				if (defaultAdapter != null) {
					listView.setAdapter(defaultAdapter);
					defaultAdapter.notifyDataSetChanged();
				}
				if (result.getRES_OBJ() != null
						&& !StringUtil.isEmpty(result.getRES_OBJ().toString())) {
					List<ProjectVO> projectList = GsonUtil.getInstance()
							.fromJson(result.getRES_OBJ().toString(),
									new TypeToken<List<ProjectVO>>() {
									}.getType());
					try {
						ProjectStructureAdapter<ProjectVO> adapter = new ProjectStructureAdapter<ProjectVO>(
								listView, mContext, projectList, true);
						listView.setAdapter(adapter);
						defaultAdapter.notifyDataSetChanged();
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
			super.onCancelled();
		}
	}
}