package com.zzn.aeassistant.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import com.google.gson.reflect.TypeToken;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.user.LoginActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.app.PreConfig;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.MD5Utils;
import com.zzn.aeassistant.util.PhoneUtil;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.ProjectVO;
import com.zzn.aeassistant.vo.UserVO;

public class SplashActivity extends Activity {
	private AlertDialog mDialog;
	private LoginTask loginTask = null;
	private boolean isFirstLoad = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		if (PreConfig.isAutoLogin() && PreConfig.isUserRemember()) {
			if (PhoneUtil.isNetworkConnected()) {
				goToNext();
			}
		} else {
			isFirstLoad = true;
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (PhoneUtil.isNetworkConnected()) {
						goToNext();
					}
				}
			}, 2000);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, filter);
		if (!PhoneUtil.isNetworkConnected()) {
			if (mDialog == null) {
				mDialog = new AlertDialog.Builder(SplashActivity.this)
						.setTitle(R.string.warning)
						.setMessage(R.string.http_null)
						.setNeutralButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										goToNext();
									}
								})
						.setPositiveButton(R.string.settings,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										startActivity(new Intent(
												Settings.ACTION_SETTINGS));
									}
								}).setCancelable(false).create();
			}
			if (!mDialog.isShowing()) {
				mDialog.show();
			}
		} else if (!isFirstLoad) {
			goToNext();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}

	@Override
	protected void onDestroy() {
		if (loginTask != null) {
			loginTask.cancel(true);
			loginTask = null;
		}
		super.onDestroy();
	}

	private void goToNext() {
		if (PreConfig.isAutoLogin() && PreConfig.isUserRemember()) {
			if (loginTask != null) {
				loginTask.cancel(true);
				loginTask = null;
			}
			try {
				String phone = PreConfig.getPhone();
				String password = MD5Utils.getMD5ofStr(PreConfig.getPsw());
				loginTask = new LoginTask();
				loginTask.execute(new String[] { phone, password });
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		startActivity(new Intent(this, LoginActivity.class));
		finish();
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (PhoneUtil.isNetworkConnected()) {
				if (mDialog != null && mDialog.isShowing()) {
					mDialog.dismiss();
					goToNext();
				}
			} else {
				if (mDialog == null) {
					mDialog = new AlertDialog.Builder(SplashActivity.this)
							.setTitle(R.string.warning)
							.setMessage(R.string.http_null)
							.setNeutralButton(R.string.cancel,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											goToNext();
										}
									})
							.setPositiveButton(R.string.settings,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											startActivity(new Intent(
													Settings.ACTION_SETTINGS));
										}
									}).setCancelable(false).create();
				}
				if (mDialog != null && !mDialog.isShowing()) {
					mDialog.show();
				}
			}
		}
	};

	private class LoginTask extends AsyncTask<String, Integer, HttpResult> {

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
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				JSONObject object;
				try {
					object = new JSONObject(result.getRES_OBJ().toString());
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
						startActivity(new Intent(SplashActivity.this,
								IndexActivity.class));
						finish();
						return;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			startActivity(new Intent(SplashActivity.this, LoginActivity.class));
			finish();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			startActivity(new Intent(SplashActivity.this, LoginActivity.class));
			finish();
		}

	}
}
