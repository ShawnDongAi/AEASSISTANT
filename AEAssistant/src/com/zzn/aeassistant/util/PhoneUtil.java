package com.zzn.aeassistant.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.app.AEApp;

/**
 * 设备信息类
 * 
 * @author Shawn
 */
public class PhoneUtil {

	/**
	 * 判断SD卡是否挂载
	 * 
	 * @return
	 */
	public static boolean isExternalStorageMounted() {
		boolean isExit = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		if (!isExit) {
			ToastUtil.show(R.string.sdcard_error);
		}
		return isExit;
	}

	/**
	 * 获取应用版本名
	 * 
	 * @return
	 */
	public static String getAppVersionName() {
		String versionName = "";
		Context context = AEApp.getInstance();
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = info.versionName;
			if (TextUtils.isEmpty(versionName)) {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 获取应用版本号
	 * 
	 * @return
	 */
	public static int getAppVersionCode() {
		int versionCode = 0;
		Context context = AEApp.getInstance();
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			versionCode = info.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	public static String getIMEI() {
		TelephonyManager phoneManager = (TelephonyManager) AEApp.getInstance()
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceID = phoneManager.getDeviceId();
		if (TextUtils.isEmpty(deviceID)) {
			deviceID = "80F62CD8FDF044CC9E875BAB4A4056A7";
		}
		return phoneManager.getDeviceId();
	}

	/**
	 * 判断是否有网络连接
	 * 
	 * @return
	 */
	public static boolean isNetworkConnected() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) AEApp
				.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null) {
			return mNetworkInfo.isAvailable();
		}
		return false;
	}
}
