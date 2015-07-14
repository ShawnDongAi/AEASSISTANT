package com.zzn.aeassistant.activity.setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.service.DownLoadService;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.GsonUtil;
import com.zzn.aeassistant.util.PhoneUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.vo.HttpResult;
import com.zzn.aeassistant.vo.VersionVO;

public class VersionUpdateTask extends AsyncTask<String, Integer, HttpResult> {
	private Context mContext;
	private boolean showDialog = false;

	public VersionUpdateTask(Context context, boolean showDialog) {
		this.mContext = context;
		this.showDialog = showDialog;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(showDialog) {
			AEProgressDialog.showLoadingDialog(mContext);
		}
	}

	@Override
	protected HttpResult doInBackground(String... params) {
		String param = "platform=0";
		HttpResult result = AEHttpUtil.doPost(URLConstants.URL_VERSION_UPDATE,
				param);
		return result;
	}

	@Override
	protected void onPostExecute(HttpResult result) {
		super.onPostExecute(result);
		if(showDialog) {
			AEProgressDialog.dismissLoadingDialog();
		}
		if (result.getRES_CODE().equals(HttpResult.CODE_SUCCESS)) {
			if (result.getRES_OBJ() != null
					&& !StringUtil.isEmpty(result.getRES_OBJ().toString())) {
				final VersionVO version = GsonUtil.getInstance().fromJson(
						result.getRES_OBJ().toString(), VersionVO.class);
				int newVersionCode = Integer
						.parseInt(version.getVersion_code());
				if (newVersionCode > PhoneUtil.getAppVersionCode()) {
					new AlertDialog.Builder(mContext)
							.setTitle(
									mContext.getString(
											R.string.version_has_new,
											version.getVersion_name()))
							.setMessage(version.getInstruction())
							.setPositiveButton(R.string.update,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											if (!PhoneUtil.isExternalStorageMounted()) {
												return;
											}
											DownLoadService.startDownLoad(
													mContext,
													String.format(
															URLConstants.URL_DOWNLOAD,
															version.getUrl()));
										}
									}).setNeutralButton(R.string.ignore, null)
							.setCancelable(false).create().show();
				} else {
					if (showDialog) {
						ToastUtil.show(R.string.version_newest);
					}
				}
			}
		} else {
			if (showDialog) {
				ToastUtil.show(result.getRES_MESSAGE());
			}
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		if(showDialog) {
			AEProgressDialog.dismissLoadingDialog();
		}
	}
}
