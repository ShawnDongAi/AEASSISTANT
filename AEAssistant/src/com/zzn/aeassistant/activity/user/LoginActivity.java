package com.zzn.aeassistant.activity.user;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.activity.MainActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.app.PreConfig;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.MD5Utils;
import com.zzn.aeassistant.util.RegexUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.ProjectVO;
import com.zzn.aeassistant.vo.UserVO;

public class LoginActivity extends BaseActivity {

	private EditText mPhoneInput, mPswInput;
	private CheckBox mRememberPsw;
	private Button mLoginBtn;
	private TextView mRegister, mForgetPsw;
	private LoginTask loginTask;

	@Override
	protected int layoutResID() {
		return R.layout.activity_login;
	}

	@Override
	protected int titleStringID() {
		return R.string.login;
	}

	@Override
	protected void initView() {
		setSwipeBackEnable(false);
		mPhoneInput = (EditText) findViewById(R.id.login_phone);
		mPswInput = (EditText) findViewById(R.id.login_password);
		mRememberPsw = (CheckBox) findViewById(R.id.login_remember_psw);
		mLoginBtn = (Button) findViewById(R.id.login_btn);
		mLoginBtn.setOnClickListener(this);
		mRegister = (TextView) findViewById(R.id.login_register);
		mRegister.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mRegister.setOnClickListener(this);
		mForgetPsw = (TextView) findViewById(R.id.login_forgetpsw);
		mForgetPsw.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mForgetPsw.setOnClickListener(this);
		
		mPhoneInput.setText(PreConfig.getPhone());
		mPswInput.setText(PreConfig.getPsw());
		mRememberPsw.setChecked(PreConfig.isUserRemember());
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.login_btn:
			doLogin();
			break;
		case R.id.login_register:
			startActivity(new Intent(this, RegisterActivity.class));
			break;
		case R.id.login_forgetpsw:
			Intent intent = new Intent(this, VerifyActivity.class);
			intent.putExtra(CodeConstants.KEY_USER_PHONE, mPhoneInput.getText().toString().trim());
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private String phone, password = "";

	private void doLogin() {
		phone = mPhoneInput.getText().toString().trim();
		password = mPswInput.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			ToastUtil.show(R.string.login_empty_phone);
			mPhoneInput.requestFocus();
			return;
		}
		if (TextUtils.isEmpty(password)) {
			ToastUtil.show(R.string.login_empty_psw);
			mPswInput.requestFocus();
			return;
		}
		if (!RegexUtil.isPhoneNum(phone)) {
			ToastUtil.show(R.string.login_error_phone);
			mPhoneInput.requestFocus();
			return;
		}
		try {
			password = MD5Utils.getMD5ofStr(password);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		loginTask = new LoginTask();
		loginTask.execute(new String[]{phone, password});
	}

	private class LoginTask extends AsyncTask<String, Integer, HttpResult> {

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
			HttpResult result = AEHttpUtil
					.doPost(URLConstants.URL_LOGIN, param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			AEProgressDialog.dismissLoadingDialog();
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				JSONObject object;
				try {
					object = new JSONObject(result.getRES_OBJ()
							.toString());
					if (object.has("user")) {
						UserVO user = GsonUtil.getInstance().fromJson(
								object.getString("user"), UserVO.class);
						if (object.has("projects")) {
							List<ProjectVO> projects = GsonUtil.getInstance()
									.fromJson(object.getString("projects"),
											new TypeToken<List<ProjectVO>>() {
											}.getType());
							user.setPROJECTS(projects);
							PreConfig.setLoginStatus(true);
						}
						AEApp.setUser(user);
						if (mRememberPsw.isChecked()) {
							PreConfig.saveUserInfo(phone, mPswInput.getText()
									.toString());
						} else {
							PreConfig.clearPsw();
						}
						startActivity(new Intent(mContext, MainActivity.class));
						finish();
						return;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				ToastUtil.show(result.getRES_MESSAGE());
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
		if (loginTask != null) {
			loginTask.cancel(true);
			loginTask = null;
		}
		super.onDestroy();
	}

	@Override
	protected boolean needLocation() {
		return false;
	}
}
