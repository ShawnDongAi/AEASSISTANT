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

	public static SharedPreferences getDefaultPre() {
		return PreferenceManager.getDefaultSharedPreferences(AEApp
				.getInstance());
	}

	public static Editor getDefaultPreEditor() {
		return getDefaultPre().edit();
	}

	public static void saveUserInfo(String phone, String password) {
		Editor editor = getDefaultPreEditor();
		try {
			editor.putString(USER_PHONE,
					DESCoderUtil.encrypt(phone, PhoneUtil.getIMEI())).commit();
			editor.putString(USER_PSW,
					DESCoderUtil.encrypt(password, PhoneUtil.getIMEI()))
					.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void clearUserInfo() {
		Editor editor = getDefaultPreEditor();
		editor.remove(USER_PHONE).commit();
		editor.remove(USER_PSW).commit();
	}

	public static void clearPsw() {
		getDefaultPreEditor().remove(USER_PSW).commit();
	}

	public static String getPhone() {
		try {
			System.out.println(getDefaultPre().getString(USER_PHONE, ""));
			System.out.println(DESCoderUtil.decrypt(
					getDefaultPre().getString(USER_PHONE, ""),
					PhoneUtil.getIMEI()));
			return DESCoderUtil.decrypt(
					getDefaultPre().getString(USER_PHONE, ""),
					PhoneUtil.getIMEI());
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getPsw() {
		try {
			return DESCoderUtil.decrypt(
					getDefaultPre().getString(USER_PSW, ""),
					PhoneUtil.getIMEI());
		} catch (Exception e) {
			e.printStackTrace();
			return "";
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
