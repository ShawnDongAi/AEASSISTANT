package com.zzn.aeassistant.activity.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.activity.QRScanningActivity;
import com.zzn.aeassistant.activity.user.UserDetailActivity;
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

public class ProjectStructureActivity extends BaseActivity {
	private PullToRefreshListView pullListView;
	private ListView listView;
	private ProjectStructureAdapter<ProjectVO> defaultAdapter;
	private ListStructureTask listStruTask;
	private String project_id;
	private long lastClickTime = 0;

	// 电话监听相关
	private TelephonyManager telephony;
	private MyPhoneStateListener myPhoneStateListener;
	// 接收到来电后的对话框
	private AlertDialog comingCallDialog;
	// 上一个来电的号码
	private String lastComingPhone = "";

	private UpdateParentTask updateParentTask;

	@Override
	protected int layoutResID() {
		return R.layout.activity_struct_list;
	}

	@Override
	protected int titleStringID() {
		return R.string.project_leaf;
	}

	@Override
	protected void initView() {
		telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();
		pullListView = (PullToRefreshListView) findViewById(R.id.base_list);
		listView = pullListView.getRefreshableView();
		project_id = getIntent().getStringExtra(CodeConstants.KEY_PROJECT_ID);
		try {
			defaultAdapter = new ProjectStructureAdapter<ProjectVO>(listView,
					mContext, new ArrayList<ProjectVO>(), true);
			listView.setAdapter(defaultAdapter);
			defaultAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				intent.putExtra(CodeConstants.KEY_PROJECT_ID, project_id);
				startActivity(intent);
			}
		});
		initPullToRefresh();
		save.setText(R.string.lable_qrcode_scanning);
		save.setVisibility(View.VISIBLE);
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
				listStruTask = new ListStructureTask();
				listStruTask.execute(project_id);
			}
		});
		listStruTask = new ListStructureTask();
		listStruTask.execute(project_id);
		AEProgressDialog.showLoadingDialog(mContext);
	}

	@Override
	protected boolean needLocation() {
		return false;
	}

	@Override
	protected void onPause() {
		if (telephony != null) {
			telephony.listen(myPhoneStateListener,
					PhoneStateListener.LISTEN_NONE);
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		telephony.listen(myPhoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	protected void onSaveClick() {
		super.onSaveClick();
		startActivityForResult(new Intent(mContext, QRScanningActivity.class),
				CodeConstants.REQUEST_CODE_QRCODE);
	}

	@Override
	protected void onDestroy() {
		if (updateParentTask != null) {
			updateParentTask.cancel(true);
			updateParentTask = null;
		}
		if (listStruTask != null) {
			listStruTask.cancel(true);
			listStruTask = null;
		}
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CodeConstants.REQUEST_CODE_REFRESH:
				listStruTask = new ListStructureTask();
				listStruTask.execute(project_id);
				AEProgressDialog.showLoadingDialog(mContext);
				break;
			case CodeConstants.REQUEST_CODE_QRCODE:
				if (comingCallDialog != null && comingCallDialog.isShowing()) {
					return;
				}
				String result = data.getStringExtra(CodeConstants.KEY_SCAN_RESULT);
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
											updateParentTask
													.execute(new String[] {
															project_id,
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

	private class ListStructureTask extends
			AsyncTask<String, Integer, HttpResult> {

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
			pullListView.onRefreshComplete();
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

	// 来电电话监听器
	private class MyPhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				if (comingCallDialog != null && comingCallDialog.isShowing()) {
					return;
				}
				lastComingPhone = incomingNumber;
				comingCallDialog = new AlertDialog.Builder(mContext)
						.setTitle(R.string.warning)
						.setMessage(
								getString(R.string.project_join_current,
										lastComingPhone))
						.setPositiveButton(R.string.confirm,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// 迁移
										if (updateParentTask != null) {
											updateParentTask.cancel(true);
											updateParentTask = null;
										}
										updateParentTask = new UpdateParentTask();
										updateParentTask.execute(new String[] {
												project_id, lastComingPhone });
									}
								}).setNegativeButton(R.string.cancel, null)
						.create();
				comingCallDialog.setCanceledOnTouchOutside(false);
				comingCallDialog.show();
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
				ToastUtil.showImp(ProjectStructureActivity.this,
						result.getRES_MESSAGE());
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
		}
	}
}