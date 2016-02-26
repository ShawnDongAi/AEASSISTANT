package com.zzn.aeassistant.util;

public class StringUtil {

	public static boolean isEmpty(Object data) {
		return data == null || data.toString().trim().equals("")
				|| data.toString().trim().equals("null");
	}
	
	public static String null2String(Object data) {
		if (data == null) {
			return "";
		}
		return data.toString().trim();
	}
}
