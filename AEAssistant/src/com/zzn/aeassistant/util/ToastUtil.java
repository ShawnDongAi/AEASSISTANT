package com.zzn.aeassistant.util;

import com.zzn.aeassistant.app.AEApp;

import android.widget.Toast;

/**
 * Toast提示类
 * 
 * @author Shawn
 */
public class ToastUtil {
	private static Toast toast = null;

	public static void show(String msg) {
		cancelToast();
		toast = Toast.makeText(AEApp.getInstance(), msg, Toast.LENGTH_SHORT);
		toast.show();
	}

	public static void show(int msg) {
		cancelToast();
		toast = Toast.makeText(AEApp.getInstance(), msg, Toast.LENGTH_SHORT);
		toast.show();
	}

	public static void showLong(String msg) {
		cancelToast();
		toast = Toast.makeText(AEApp.getInstance(), msg, Toast.LENGTH_LONG);
		toast.show();
	}

	public static void showLong(int msg) {
		cancelToast();
		toast = Toast.makeText(AEApp.getInstance(), msg, Toast.LENGTH_LONG);
		toast.show();
	}

	public static void cancelToast() {
		if (toast != null) {
			toast.cancel();
			toast = null;
		}
	}
}
