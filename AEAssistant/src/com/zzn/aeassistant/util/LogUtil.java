package com.zzn.aeassistant.util;

import android.util.Log;

/**
 * Log工具类
 * @author Shawn
 */
public class LogUtil {
	static String TAG = "ZZN_IFIP";
	private static boolean isDebug = true;

	public static void d(String msg) {
		if (isDebug) {
			Log.d(TAG, msg);
		}
	}

	public static void d(String msg, Throwable tr) {
		if (isDebug) {
			Log.d(TAG, msg, tr);
		}
	}

	public static void e(String msg) {
		if (isDebug) {
			Log.e(TAG, msg);
		}
	}

	public static void e(String msg, Throwable tr) {
		if (isDebug) {
			Log.e(TAG, msg, tr);
		}
	}

	public static void i(String msg) {
		if (isDebug) {
			Log.i(TAG, msg);
		}
	}

	public static void i(String msg, Throwable tr) {
		if (isDebug) {
			Log.i(TAG, msg, tr);
		}
	}

	public static void v(String msg) {
		if (isDebug) {
			Log.v(TAG, msg);
		}
	}

	public static void v(String msg, Throwable tr) {
		if (isDebug) {
			Log.v(TAG, msg, tr);
		}
	}

	public static void w(String msg) {
		if (isDebug) {
			Log.w(TAG, msg);
		}
	}

	public static void w(String msg, Throwable tr) {
		if (isDebug) {
			Log.w(TAG, msg, tr);
		}
	}

	public static void w(Throwable tr) {
		if (isDebug) {
			Log.w(TAG, tr);
		}
	}
}