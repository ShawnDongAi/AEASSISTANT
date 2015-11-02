package com.zzn.aeassistant.activity.user;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.activity.IndexActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.app.PreConfig;
import com.zzn.aeassistant.constants.PlatformkEY;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.MD5Utils;
import com.zzn.aeassistant.util.RegexUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.UserVO;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

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
		SMSSDK.initSDK(this, PlatformkEY.SMS_APP_KEY, PlatformkEY.SMS_APP_SECRET);
		EventHandler eventHandler = new EventHandler() {
			public void afterEvent(final int event, final int result, Object data) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						AEProgressDialog.dismissLoadingDialog();
						if (result == SMSSDK.RESULT_COMPLETE) {
							// 回调完成
							if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
								// 获取验证码成功
								restTime = 60;
								ToastUtil.show(R.string.login_sms_send);
								mHandler.post(smsTimerTask);
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
				});
			}
		};
		// 注册回调监听接口
		SMSSDK.registerEventHandler(eventHandler);
		((CheckBox) findViewById(R.id.agreement_checkbox)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mRegisterBtn.setEnabled(isChecked);
			}
		});
		findViewById(R.id.agreement_text).setOnClickListener(this);
	}

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
			mVerifyBtn.setEnabled(false);
			SMSSDK.getVerificationCode(PlatformkEY.ZONE, phone);
			break;
		case R.id.agreement_text:
			startActivity(new Intent(this, AgreementActivity.class));
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
			password = MD5Utils.getMD5ofStr(password);
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
			String param = "phone=" + phone + "&password=" + password + "&code=" + smsCode;
			HttpResult result = AEHttpUtil.doPost(URLConstants.URL_REGISTER, param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				try {
					UserVO user = GsonUtil.getInstance().fromJson(result.getRES_OBJ().toString(), UserVO.class);
					AEApp.setUser(user);
					PreConfig.saveUserInfo(phone, mPswInput.getText().toString());
					startActivity(new Intent(mContext, IndexActivity.class));
					finish();
				} catch (Exception e) {
					e.printStackTrace();
					ToastUtil.show(R.string.http_out);
				}
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
				mVerifyBtn.setText(getString(R.string.login_sms_timer, restTime));
				restTime--;
				mHandler.postDelayed(smsTimerTask, 1000);
			}
		}
	};

	@Override
	protected void onDestroy() {
		mHandler.removeCallbacksAndMessages(null);
		// 销毁回调监听接口
		SMSSDK.unregisterAllEventHandler();
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
