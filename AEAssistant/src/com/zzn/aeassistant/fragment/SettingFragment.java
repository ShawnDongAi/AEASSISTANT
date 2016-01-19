package com.zzn.aeassistant.fragment;

import java.io.File;

import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.google.zxing.WriterException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.zzn.aeassistant.R;
import com.zzn.aeassistant.activity.TextEditActivity;
import com.zzn.aeassistant.activity.setting.VersionUpdateTask;
import com.zzn.aeassistant.activity.user.AgreementActivity;
import com.zzn.aeassistant.activity.user.LoginActivity;
import com.zzn.aeassistant.activity.user.UserActivity;
import com.zzn.aeassistant.activity.user.VerifyActivity;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.app.PreConfig;
import com.zzn.aeassistant.constants.CodeConstants;
import com.zzn.aeassistant.constants.Config;
import com.zzn.aeassistant.constants.FileCostants;
import com.zzn.aeassistant.constants.URLConstants;
import com.zzn.aeassistant.util.AEHttpUtil;
import com.zzn.aeassistant.util.BitmapUtil;
import com.zzn.aeassistant.util.PhoneUtil;
import com.zzn.aeassistant.util.ToastUtil;
import com.zzn.aeassistant.view.AEProgressDialog;
import com.zzn.aeassistant.view.CircleImageView;
import com.zzn.aeassistant.vo.HttpResult;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

/**
 * 设置页
 * 
 * @author Shawn
 */
public class SettingFragment extends BaseFragment {
	public static final String ACTION_USER_INFO_CHANGED = "com.zzn.aeassistant.user_info_changed";
	public static final int REQUEST_FEEDBACK = 0;
	private static final String TWOCODE_FILE = "two_code.png";
	private View user, modifyPsd, versionUpdate, feedBack, share, twocode, logout;
	private TextView version, mUserName;
	private CircleImageView mUserHead;
	private VersionUpdateTask versionUpdateTask;
	private PopupWindow twocodeWindow;
	private ImageView twocodeImg;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

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
		user = container.findViewById(R.id.setting_user);
		mUserName = (TextView) container.findViewById(R.id.setting_user_name);
		mUserHead = (CircleImageView) container.findViewById(R.id.setting_user_head);
		user.setOnClickListener(this);
		modifyPsd.setOnClickListener(this);
		versionUpdate.setOnClickListener(this);
		feedBack.setOnClickListener(this);
		share.setOnClickListener(this);
		twocode.setOnClickListener(this);
		logout.setOnClickListener(this);
		container.findViewById(R.id.setting_agreenment).setOnClickListener(this);
		version.setText(PhoneUtil.getAppVersionName());
		initTwoCode();
		initImageLoader();
		initUserView();
		mContext.registerReceiver(userInfoReceiver, new IntentFilter(ACTION_USER_INFO_CHANGED));
	}
	
	private void initUserView() {
		mUserName.setText(AEApp.getCurrentUser().getUSER_NAME());
		imageLoader.displayImage(String.format(URLConstants.URL_IMG, AEApp.getCurrentUser().getBIG_HEAD()), mUserHead,
				options);
	}
	
	private BroadcastReceiver userInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			initUserView();
		}
	};

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.setting_user:
			startActivity(new Intent(mContext, UserActivity.class));
			break;
		case R.id.setting_modify_password:
			Intent modIntent = new Intent(getActivity(), VerifyActivity.class);
			modIntent.putExtra(CodeConstants.KEY_USER_PHONE, AEApp.getCurrentUser().getPHONE());
			modIntent.putExtra(CodeConstants.KEY_USER_PHONE_EDITABLE, false);
			startActivity(modIntent);
			break;
		case R.id.setting_version_update:
			if (Config.channel == Config.CHANNEL_BAIDU) {
				AEProgressDialog.showLoadingDialog(mContext);
				BDAutoUpdateSDK.uiUpdateAction(mContext, new UICheckUpdateCallback() {
					@Override
					public void onCheckComplete() {
						AEProgressDialog.dismissLoadingDialog();
					}
				});
			} else {
				if (versionUpdateTask != null) {
					versionUpdateTask.cancel(true);
				}
				versionUpdateTask = new VersionUpdateTask(mContext, true);
				versionUpdateTask.execute();
			}
			break;
		case R.id.setting_feedback:
			Intent feedIntent = new Intent(getActivity(), TextEditActivity.class);
			feedIntent.putExtra(CodeConstants.KEY_TITLE, getString(R.string.feed_back));
			feedIntent.putExtra(CodeConstants.KEY_HINT_TEXT, getString(R.string.hint_feedback));
			feedIntent.putExtra(CodeConstants.KEY_SINGLELINE, false);
			startActivityForResult(feedIntent, REQUEST_FEEDBACK);
			break;
		case R.id.setting_share:
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			File file = new File(FileCostants.DIR_BASE, TWOCODE_FILE);
			if (file != null && file.exists()) {
				shareIntent.setType("image/jpg");
				shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_img, URLConstants.URL_BASE));
				shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
			} else {
				shareIntent.setType("text/plain");
				shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text, URLConstants.URL_BASE));
			}
			shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
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
		case R.id.setting_agreenment:
			startActivity(new Intent(mContext, AgreementActivity.class));
			break;
		default:
			break;
		}
	}

	private void initTwoCode() {
		twocodeImg = new ImageView(mContext);
		try {
			Bitmap bitmap = BitmapUtil.cretaeTwoCode(mContext, URLConstants.URL_BASE, R.drawable.ic_launcher);
			twocodeImg.setImageBitmap(bitmap);
			BitmapUtil.writeToSdcard(bitmap, FileCostants.DIR_BASE, TWOCODE_FILE);
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
		twocodeWindow = new PopupWindow(twocodeImg, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		twocodeWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent_lightslategray));
		twocodeWindow.setAnimationStyle(R.style.bottommenu_anim_style);
	}
	
	@SuppressWarnings("deprecation")
	private void initImageLoader() {
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_head) // 设置图片在下载期间显示的图片
				.showImageForEmptyUri(R.drawable.ic_head)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.ic_head) // 设置图片加载/解码过程中错误时候显示的图片
				.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
				.imageScaleType(ImageScaleType.EXACTLY)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				.displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
				.build();// 构建完成
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_FEEDBACK:
				ToastUtil.show(R.string.thanks_for_feedback);
				String content = data.getStringExtra(CodeConstants.KEY_TEXT_RESULT);
				new FeedBackTask().execute(content);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public boolean onBackPressed() {
		if (twocodeWindow != null && twocodeWindow.isShowing()) {
			twocodeWindow.dismiss();
			return true;
		}
		return super.onBackPressed();
	}

	@Override
	public void onDestroyView() {
		if (versionUpdateTask != null) {
			versionUpdateTask.cancel(true);
			versionUpdateTask = null;
		}
		mContext.unregisterReceiver(userInfoReceiver);
		super.onDestroyView();
	}

	private class FeedBackTask extends AsyncTask<String, Integer, HttpResult> {
		@Override
		protected HttpResult doInBackground(String... params) {
			String content = params[0];
			String param = "user_id=" + AEApp.getCurrentUser().getUSER_ID() + "&content=" + content;
			HttpResult result = AEHttpUtil.doPost(URLConstants.URL_FEEDBACK, param);
			return result;
		}
	}

}