package com.zzn.aeassistant.activity.project;

import java.util.List;

import android.os.AsyncTask;
import com.google.gson.reflect.TypeToken;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.BaseActivity;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.UserVO;

public class ProjectUsersActivity extends BaseActivity {

	@Override
	protected int layoutResID() {
		return R.layout.activity_base_list;
	}

	@Override
	protected int titleStringID() {
		return R.string.project_user;
	}

	@Override
	protected void initView() {
	}

	@Override
	protected boolean needLocation() {
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private class ListUsersTask extends AsyncTask<String, Integer, HttpResult> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected HttpResult doInBackground(String... params) {
			String project_id = params[0];
			String param = "project_id=" + project_id;
			HttpResult result = AEHttpUtil.doPost(
					URLConstants.URL_PROJECT_USERS, param);
			return result;
		}

		@Override
		protected void onPostExecute(HttpResult result) {
			super.onPostExecute(result);
			if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
				if (result.getRES_OBJ() != null
						&& !StringUtil.isEmpty(result.getRES_OBJ().toString())) {
					List<UserVO> userList = GsonUtil.getInstance().fromJson(
							result.getRES_OBJ().toString(),
							new TypeToken<List<UserVO>>() {
							}.getType());
				}
			} else {
				ToastUtil.show(result.getRES_MESSAGE());
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
	}
}