package com.zzn.aeassistant.app;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.zzn.aeassistant.util.DESCoderUtil;
import com.zzn.aeassistant.util.PhoneUtil;
import com.zzn.aeassistant.vo.UserVO;

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
	public static final String USER_VO = "user_vo";

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
	
	public static void saveUserVO(UserVO user) {
		if (user == null) {
			clearUserVO();
		}
		Editor editor = getDefaultPreEditor();
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(user);
			String userString = new String(Base64.encode(baos.toByteArray(),
					Base64.DEFAULT));
			editor.putString(USER_VO,
					DESCoderUtil.encrypt(userString, PhoneUtil.getIMEI()));
			editor.commit();
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
	
	public static void clearUserVO() {
		getDefaultPreEditor().remove(USER_VO).commit();
	}

	public static String getPhone() {
		try {
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
	
	public static UserVO getUserVO() {
		UserVO user = null;
		try {
			String personBase64 = DESCoderUtil.decrypt(getDefaultPre()
					.getString(USER_VO, ""), PhoneUtil.getIMEI());
			byte[] base64Bytes = Base64.decode(personBase64.getBytes(),
					Base64.DEFAULT);
			ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			user = (UserVO) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
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
