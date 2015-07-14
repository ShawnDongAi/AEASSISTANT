package com.zzn.aeassistant.activity.user;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.PlatformkEY;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.RegexUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.vo.HttpResult;

public class VerifyActivity extends BaseActivity {
	private EditText mPhoneInput, mSmsInput;
	private Button mRegisterBtn, mVerifyBtn;
	
	private VerifyTask verifyTask;

	@Override
	protected int layoutResID() {
		return R.layout.activity_verify;
	}

	@Override
	protected int titleStringID() {
		return R.string.verify_sms;
	}

	@Override
	protected void initView() {
		mPhoneInput = (EditText) findViewById(R.id.register_phone);
		if (!getIntent().getBooleanExtra(CodeConstants.KEY_USER_PHONE_EDITABLE, true)) {
			mPhoneInput.setKeyListener(null);
			mPhoneInput.setFocusable(false);
			mPhoneInput.setFocusableInTouchMode(false);
		}
		mPhoneInput.setText(getIntent().getStringExtra(CodeConstants.KEY_USER_PHONE));
		mSmsInput = (EditText) findViewById(R.id.register_sms);
		mRegisterBtn = (Button) findViewById(R.id.register_btn);
		mVerifyBtn = (Button) findViewById(R.id.register_verify);
		mRegisterBtn.setOnClickListener(this);
		mVerifyBtn.setOnClickListener(this);
	}

	boolean ready;

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.register_btn:
			doVerify();
			break;
		case R.id.register_verify:
			phone = mPhoneInput.getText().toString().trim();
			if (TextUtils.isEmpty(phone)) {
				ToastUtil.show(R.string.login_empty_phone);
				mPhoneInput.requestFocus();
				return;
			}
			if (!RegexUtil.isPhoneNum(phone)) {
				ToastUtil.show(R.string.login_error_phone);
				mPhoneInput.requestFocus();
				return;
			}
			AEProgressDialog.showLoadingDialog(this);
			SMSSDK.initSDK(this, PlatformkEY.SMS_APP_KEY,
					PlatformkEY.SMS_APP_SECRET);
			EventHandler eventHandler = new EventHandler() {
				public void afterEvent(int event, int result, Object data) {
					AEProgressDialog.dismissLoadingDialog();
					if (result == SMSSDK.RESULT_COMPLETE) {
						// 回调完成
						if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
							// 获取验证码成功
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									ToastUtil.show(R.string.login_sms_send);
									mHandler.post(smsTimerTask);
								}
							});
							return;
						} else {
							ToastUtil.show(R.string.login_sms_send_error);
						}
					} else {
						ToastUtil.show(R.string.login_sms_send_error);
					}
					mVerifyBtn.setText(R.string.login_sms_verify);
					mVerifyBtn.setEnabled(true);
				}
			};
			// 注册回调监听接口
			SMSSDK.registerEventHandler(eventHandler);
			ready = true;
			mVerifyBtn.setEnabled(false);
			SMSSDK.getVerificationCode(PlatformkEY.ZONE, phone);
			break;
		default:
			break;
		}
	}

	private String phone, smsCode = "";

	private void doVerify() {
		phone = mPhoneInput.getText().toString().trim();
		smsCode = mSmsInput.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			ToastUtil.show(R.string.login_empty_phone);
			mPhoneInput.requestFocus();
			return;
		}
		if (!RegexUtil.isPhoneNum(phone)) {
			ToastUtil.show(R.string.login_error_phone);
			mPhoneInput.requestFocus();
			return;
		}
		if (TextUtils.isEmpty(smsCode)) {
			ToastUtil.show(R.string.login_empty_sms);
			mSmsInput.requestFocus();
			return;
		}
		verifyTask = new VerifyTask();
		verifyTask.execute(new String[] { phone, smsCode });
	}

	private class VerifyTask extends AsyncTask<String, Integer, HttpResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			AEProgressDialog.showLoadingDialog(mContext);
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String phone = params[0];
			String smsCode = params[1];
			String param = "phone=" + phone + "&code=" + smsCode;
			HttpResult result = AEHttpUtil.doPost(URLConstants.URL_VERIFY,
					param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				Intent intent = new Intent(mContext, ResetPswActivity.class);
				intent.putExtra(ResetPswActivity.RESET_PHONE, phone);
				startActivity(intent);
			} else {
				ToastUtil.show(result.getRES_MESSAGE());
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			AEProgressDialog.dismissLoadingDialog();
		}
	}

	private int restTime = 60;
	private Runnable smsTimerTask = new Runnable() {
		@Override
		public void run() {
			if (restTime <= 0) {
				mVerifyBtn.setText(R.string.login_sms_verify);
				mVerifyBtn.setEnabled(true);
				restTime = 60;
			} else {
				mVerifyBtn
						.setText(getString(R.string.login_sms_timer, restTime));
				restTime--;
				mHandler.postDelayed(smsTimerTask, 1000);
			}
		}
	};

	private Handler mHandler = new Handler();

	@Override
	protected void onDestroy() {
		mHandler.removeCallbacksAndMessages(null);
		if (ready) {
			// 销毁回调监听接口
			SMSSDK.unregisterAllEventHandler();
		}
		if (verifyTask != null) {
			verifyTask.cancel(true);
			verifyTask = null;
		}
		super.onDestroy();
	}

	@Override
	protected boolean needLocation() {
		return false;
	}
}
