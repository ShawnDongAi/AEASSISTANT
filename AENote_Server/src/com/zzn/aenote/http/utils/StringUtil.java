package com.zzn.aenote.http.utils;

public final class StringUtil {
	public static String ELLIPSES = "&#133";

	public static final String EMPTY_STRING = "";

	public static String nullToEmpty(Object data) {
		if (isEmpty(data)) {
			return "''";
		}
		return data.toString().trim();
	}

	public static String nullToString(Object data) {
		if (isEmpty(data)) {
			return "";
		}
		return data.toString().trim();
	}

	public static boolean isEmpty(Object data) {
		return data == null || data.toString().trim().equals("") || data.toString().trim().equals("null");
	}
}