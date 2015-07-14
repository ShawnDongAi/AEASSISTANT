package com.zzn.aeassistant.activity.setting;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.activity.TextEditActivity;
import com.zzn.aeassistant.activity.user.LoginActivity;
import com.zzn.aeassistant.activity.user.VerifyActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.vo.HttpResult;

/**
 * 设置页
 * 
 * @author Shawn
 */
public class SettingActivity extends BaseActivity {
	public static final int REQUEST_FEEDBACK = 0;
	private TextView modifyPsd, versionUpdate, feedBack;
	private Button logout;

	@Override
	protected int layoutResID() {
		return R.layout.activity_setting;
	}

	@Override
	protected int titleStringID() {
		return R.string.settings;
	}

	@Override
	protected void initView() {
		modifyPsd = (TextView) findViewById(R.id.setting_modify_password);
		versionUpdate = (TextView) findViewById(R.id.setting_version_update);
		feedBack = (TextView) findViewById(R.id.setting_feedback);
		logout = (Button) findViewById(R.id.logout);
		modifyPsd.setOnClickListener(this);
		versionUpdate.setOnClickListener(this);
		feedBack.setOnClickListener(this);
		logout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.setting_modify_password:
			Intent modIntent = new Intent(this, VerifyActivity.class);
			modIntent.putExtra(CodeConstants.KEY_USER_PHONE, AEApp
					.getCurrentUser().getPHONE());
			modIntent.putExtra(CodeConstants.KEY_USER_PHONE_EDITABLE, false);
			startActivity(modIntent);
			break;
		case R.id.setting_version_update:
			break;
		case R.id.setting_feedback:
			Intent feedIntent = new Intent(this, TextEditActivity.class);
			feedIntent.putExtra(CodeConstants.KEY_TITLE,
					getString(R.string.feed_back));
			feedIntent.putExtra(CodeConstants.KEY_HINT_TEXT, getString(R.string.hint_feedback));
			feedIntent.putExtra(CodeConstants.KEY_SINGLELINE, false);
			startActivityForResult(feedIntent, REQUEST_FEEDBACK);
			break;
		case R.id.logout:
			AEApp.getInstance().clearTask(this);
			startActivity(new Intent(this, LoginActivity.class));
			finish();
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_FEEDBACK:
				ToastUtil.show(R.string.thanks_for_feedback);
				String content = data
						.getStringExtra(CodeConstants.KEY_TEXT_RESULT);
				new FeedBackTask().execute(content);
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected boolean needLocation() {
		return false;
	}
	
	private class FeedBackTask extends AsyncTask<String, Integer, HttpResult> {
		@Override
		protected HttpResult doInBackground(String... params) {
			String content = params[0];
			String param = "user_id="+AEApp.getCurrentUser().getUSER_ID()+"&content="+content;
			HttpResult result = AEHttpUtil.doPost(URLConstants.URL_FEEDBACK, param);
			return result;
		}
	}
}
