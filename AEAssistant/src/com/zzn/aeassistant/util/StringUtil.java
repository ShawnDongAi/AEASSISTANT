package com.zzn.aeassistant.util;

public class StringUtil {

	public static boolean isEmpty(String data) {
		return data == null || data.trim().equals("")
				|| data.trim().equals("null");
	}
}
