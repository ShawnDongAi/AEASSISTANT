package com.zzn.aeassistant.fragment;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.TextEditActivity;
import com.zzn.aeassistant.activity.setting.VersionUpdateTask;
import com.zzn.aeassistant.activity.user.LoginActivity;
import com.zzn.aeassistant.activity.user.VerifyActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.app.PreConfig;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.FileCostants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.BitmapUtil;
import com.zzn.aeassistant.util.PhoneUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.vo.HttpResult;

/**
 * 设置页
 * 
 * @author Shawn
 */
public class SettingFragment extends BaseFragment {
	public static final int REQUEST_FEEDBACK = 0;
	private static final String TWOCODE_FILE = "two_code.png";
	private View modifyPsd, versionUpdate, feedBack, share, twocode, logout;
	private TextView version;
	private VersionUpdateTask versionUpdateTask;
	private PopupWindow twocodeWindow;
	private ImageView twocodeImg;

	@Override
	protected int layoutResID() {
		return R.layout.fragment_setting;
	}

	@Override
	protected void initView(View container) {
		modifyPsd = container.findViewById(R.id.setting_modify_password);
		versionUpdate = container.findViewById(R.id.setting_version_update);
		feedBack = container.findViewById(R.id.setting_feedback);
		share = container.findViewById(R.id.setting_share);
		twocode = container.findViewById(R.id.setting_twocode);
		version = (TextView) container.findViewById(R.id.setting_version);
		logout = container.findViewById(R.id.logout);
		modifyPsd.setOnClickListener(this);
		versionUpdate.setOnClickListener(this);
		feedBack.setOnClickListener(this);
		share.setOnClickListener(this);
		twocode.setOnClickListener(this);
		logout.setOnClickListener(this);
		version.setText(PhoneUtil.getAppVersionName());
		initTwoCode();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.setting_modify_password:
			Intent modIntent = new Intent(getActivity(), VerifyActivity.class);
			modIntent.putExtra(CodeConstants.KEY_USER_PHONE, AEApp
					.getCurrentUser().getPHONE());
			modIntent.putExtra(CodeConstants.KEY_USER_PHONE_EDITABLE, false);
			startActivity(modIntent);
			break;
		case R.id.setting_version_update:
			if (versionUpdateTask != null) {
				versionUpdateTask.cancel(true);
			}
			versionUpdateTask = new VersionUpdateTask(mContext, true);
			versionUpdateTask.execute();
			break;
		case R.id.setting_feedback:
			Intent feedIntent = new Intent(getActivity(), TextEditActivity.class);
			feedIntent.putExtra(CodeConstants.KEY_TITLE,
					getString(R.string.feed_back));
			feedIntent.putExtra(CodeConstants.KEY_HINT_TEXT,
					getString(R.string.hint_feedback));
			feedIntent.putExtra(CodeConstants.KEY_SINGLELINE, false);
			startActivityForResult(feedIntent, REQUEST_FEEDBACK);
			break;
		case R.id.setting_share:
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			File file = new File(FileCostants.DIR_BASE, TWOCODE_FILE);
			if (file != null && file.exists()) {
				shareIntent.putExtra(
						Intent.EXTRA_SUBJECT,
						getString(R.string.share_img,
								URLConstants.URL_APK_DOWNLOAD));
				shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
				shareIntent.setType("image/jpeg");
			} else {
				shareIntent.putExtra(
						Intent.EXTRA_TEXT,
						getString(R.string.share_text,
								URLConstants.URL_APK_DOWNLOAD));
				shareIntent.setType("text/plain");
			}
			shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(Intent.createChooser(shareIntent,
					getString(R.string.share_title)));
			break;
		case R.id.setting_twocode:
			if (twocodeWindow != null && !twocodeWindow.isShowing()) {
				twocodeWindow.showAtLocation(twocode, Gravity.CENTER, 0, 0);
			}
			break;
		case R.id.logout:
			AEApp.getInstance().clearTask(getActivity());
			AEApp.setUser(null);
			PreConfig.setLoginStatus(false);
			startActivity(new Intent(mContext, LoginActivity.class));
			getActivity().finish();
			break;
		}
	}

	private void initTwoCode() {
		twocodeImg = new ImageView(mContext);
		try {
			Bitmap bitmap = BitmapUtil.cretaeTwoCode(mContext,
					URLConstants.URL_APK_DOWNLOAD, R.drawable.ic_launcher);
			twocodeImg.setImageBitmap(bitmap);
			BitmapUtil.writeToSdcard(bitmap, FileCostants.DIR_BASE,
					TWOCODE_FILE);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		twocodeImg.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (twocodeWindow != null && twocodeWindow.isShowing()) {
						twocodeWindow.dismiss();
					}
				}
				return false;
			}
		});
		twocodeWindow = new PopupWindow(twocodeImg, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		twocodeWindow.setBackgroundDrawable(getResources().getDrawable(
				R.color.transparent_lightslategray));
		twocodeWindow.setAnimationStyle(R.style.bottommenu_anim_style);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
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
	public void onDestroyView() {
		if (versionUpdateTask != null) {
			versionUpdateTask.cancel(true);
			versionUpdateTask = null;
		}
		super.onDestroyView();
	}

	private class FeedBackTask extends AsyncTask<String, Integer, HttpResult> {
		@Override
		protected HttpResult doInBackground(String... params) {
			String content = params[0];
			String param = "user_id=" + AEApp.getCurrentUser().getUSER_ID()
					+ "&content=" + content;
			HttpResult result = AEHttpUtil.doPost(URLConstants.URL_FEEDBACK,
					param);
			return result;
		}
	}

}