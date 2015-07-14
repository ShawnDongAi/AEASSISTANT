package com.zzn.aeassistant.activity.user;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.DESCoderUtil;
import com.zzn.aeassistant.util.RegexUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.vo.HttpResult;

public class ResetPswActivity extends BaseActivity {
	public static final String RESET_PHONE = "phone";

	private EditText mPswInput, mPsw2Input;
	private Button mResetBtn;

	private ResetTask resetTask;

	@Override
	protected int layoutResID() {
		return R.layout.activity_reset_psw;
	}

	@Override
	protected int titleStringID() {
		return R.string.reset_psw;
	}

	@Override
	protected void initView() {
		phone = getIntent().getStringExtra(RESET_PHONE);
		if (TextUtils.isEmpty(phone) || !RegexUtil.isPhoneNum(phone)) {
			finish();
		}
		mPswInput = (EditText) findViewById(R.id.reset_password);
		mPsw2Input = (EditText) findViewById(R.id.reset_password_again);
		mResetBtn = (Button) findViewById(R.id.reset_btn);
		mResetBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.reset_btn:
			doReset();
			break;
		default:
			break;
		}
	}

	private String phone, password, password2 = "";

	private void doReset() {
		password = mPswInput.getText().toString().trim();
		password2 = mPsw2Input.getText().toString().trim();
		if (TextUtils.isEmpty(password)) {
			ToastUtil.show(R.string.login_empty_psw);
			mPswInput.requestFocus();
			return;
		}
		if (TextUtils.isEmpty(password2)) {
			ToastUtil.show(R.string.reset_psw2_null);
			mPsw2Input.requestFocus();
			return;
		}
		if (!password.equals(password2)) {
			ToastUtil.show(R.string.different_psw);
			mPsw2Input.requestFocus();
			return;
		}
		try {
			password = DESCoderUtil.encrypt(password, phone);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		resetTask = new ResetTask();
		resetTask.execute(new String[] { phone, password });
	}

	private class ResetTask extends AsyncTask<String, Integer, HttpResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			AEProgressDialog.showLoadingDialog(mContext);
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String phone = params[0];
			String password = params[1];
			String param = "phone=" + phone + "&password=" + password;
			HttpResult result = AEHttpUtil.doPost(URLConstants.URL_RESET_PSW,
					param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				AEApp.getInstance().clearTask(ResetPswActivity.this);
				Intent intent = new Intent(mContext, LoginActivity.class);
				startActivity(intent);
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

	@Override
	protected void onDestroy() {
		if (resetTask != null) {
			resetTask.cancel(true);
			resetTask = null;
		}
		super.onDestroy();
	}

	@Override
	protected boolean needLocation() {
		return false;
	}
}
