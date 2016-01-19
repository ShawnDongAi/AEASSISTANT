package com.zzn.aeassistant.app;

import com.zzn.aeassistant.util.DESCoderUtil;
import com.zzn.aeassistant.util.PhoneUtil;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * SharePreference读取
 * 
 * @author Shawn
 *
 */
@SuppressLint("CommitPrefEdits")
public class PreConfig {
	public static final String USER_PHONE = "phone";
	public static final String USER_PSW = "password";
	public static final String USER_REMEMBER = "remember";
	public static final String AUTO_LOGIN = "auto_login";
	public static final String FIRST_LOAD = "first_load";

	public static SharedPreferences getDefaultPre() {
		return PreferenceManager.getDefaultSharedPreferences(AEApp.getInstance());
	}

	public static Editor getDefaultPreEditor() {
		return getDefaultPre().edit();
	}

	public static void saveUserInfo(String phone, String password) {
		Editor editor = getDefaultPreEditor();
		try {
			editor.putString(USER_PHONE, phone).commit();
			editor.putString(USER_PSW, password).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void clearUserInfo() {
		Editor editor = getDefaultPreEditor();
		editor.remove(USER_PHONE).commit();
		editor.remove(USER_PSW).commit();
	}

	public static void setLoginStatus(boolean isLogin) {
		getDefaultPreEditor().putBoolean(AUTO_LOGIN, isLogin).commit();
	}

	public static boolean isAutoLogin() {
		return getDefaultPre().getBoolean(AUTO_LOGIN, false);
	}

	public static boolean isFirstLoad() {
		return getDefaultPre().getBoolean(FIRST_LOAD, true);
	}

	public static void setFirstLoad() {
		getDefaultPreEditor().putBoolean(FIRST_LOAD, false).commit();
	}

	public static void clearPsw() {
		getDefaultPreEditor().remove(USER_PSW).commit();
	}

	public static String getPhone() {
		String phone = getDefaultPre().getString(USER_PHONE, "");
		try {
			return DESCoderUtil.decrypt(phone, PhoneUtil.getIMEI());
		} catch (Exception e) {
			e.printStackTrace();
			return phone;
		}
	}

	public static String getPsw() {
		String psw = getDefaultPre().getString(USER_PSW, "");
		try {
			return DESCoderUtil.decrypt(psw, PhoneUtil.getIMEI());
		} catch (Exception e) {
			e.printStackTrace();
			return psw;
		}
	}

	public static void setUserRemember(boolean remember) {
		getDefaultPreEditor().putBoolean(USER_REMEMBER, remember).commit();
	}

	public static boolean isUserRemember() {
		return getDefaultPre().getBoolean(USER_REMEMBER, true);
	}

	public static String getString(String key, String defValue) {
		return getDefaultPre().getString(key, defValue);
	}
}
