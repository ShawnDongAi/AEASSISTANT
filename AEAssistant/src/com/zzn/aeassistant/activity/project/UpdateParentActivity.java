package com.zzn.aeassistant.activity.project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.ProjectVO;

public class UpdateParentActivity extends BaseActivity {
	private ProjectVO project;
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
		return R.layout.activity_update_parent;
	}

	@Override
	protected int titleStringID() {
		return R.string.project_update_parent;
	}

	@Override
	protected void initView() {
		telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();
		project = (ProjectVO) getIntent().getSerializableExtra(
				CodeConstants.KEY_PROJECT_VO);
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
	protected void onDestroy() {
		if (updateParentTask != null) {
			updateParentTask.cancel(true);
			updateParentTask = null;
		}
		super.onDestroy();
	}

	@Override
	protected boolean needLocation() {
		return false;
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
												project.getPROJECT_ID(),
												lastComingPhone });
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
			ToastUtil.show(result.getRES_MESSAGE());
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
		}
	}
}
