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
import com.zzn.aeassistant.activity.MainActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.app.PreConfig;
import com.zzn.aeassistant.constants.PlatformkEY;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.DESCoderUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.RegexUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.UserVO;

public class RegisterActivity extends BaseActivity {

	private EditText mPhoneInput, mPswInput, mSmsInput;
	private Button mRegisterBtn, mVerifyBtn;
	
	private RegisterTask registerTask;
	private Handler mHandler = new Handler();

	@Override
	protected int layoutResID() {
		return R.layout.activity_register;
	}

	@Override
	protected int titleStringID() {
		return R.string.register;
	}

	@Override
	protected void initView() {
		mPhoneInput = (EditText) findViewById(R.id.register_phone);
		mPswInput = (EditText) findViewById(R.id.register_password);
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
			doRegister();
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

	private String phone, password, smsCode = "";

	private void doRegister() {
		phone = mPhoneInput.getText().toString().trim();
		password = mPswInput.getText().toString().trim();
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
		if (TextUtils.isEmpty(password)) {
			ToastUtil.show(R.string.login_empty_psw);
			mPswInput.requestFocus();
			return;
		}
		if (TextUtils.isEmpty(smsCode)) {
			ToastUtil.show(R.string.login_empty_sms);
			mSmsInput.requestFocus();
			return;
		}
		try {
			password = DESCoderUtil.encrypt(password, phone);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		registerTask = new RegisterTask();
		registerTask.execute(new String[] { phone, password, smsCode });
	}

	private class RegisterTask extends AsyncTask<String, Integer, HttpResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			AEProgressDialog.showLoadingDialog(mContext);
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String phone = params[0];
			String password = params[1];
			String smsCode = params[2];
			String param = "phone=" + phone + "&password=" + password
					+ "&code=" + smsCode;
			HttpResult result = AEHttpUtil.doPost(URLConstants.URL_REGISTER,
					param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				UserVO user = GsonUtil.getInstance().fromJson(
						result.getRES_OBJ().toString(), UserVO.class);
				AEApp.setUser(user);
				PreConfig.saveUserInfo(phone, mPswInput.getText().toString());
				startActivity(new Intent(mContext, MainActivity.class));
				finish();
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

	@Override
	protected void onDestroy() {
		mHandler.removeCallbacksAndMessages(null);
		if (ready) {
			// 销毁回调监听接口
			SMSSDK.unregisterAllEventHandler();
		}
		if (registerTask != null) {
			registerTask.cancel(true);
			registerTask = null;
		}
		super.onDestroy();
	}

	@Override
	protected boolean needLocation() {
		return false;
	}
}