package com.zzn.aeassistant.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtil {
	private static Gson gson = null;

	public GsonUtil() {
		gson = new GsonBuilder().serializeNulls().create();
	}
	
	public static Gson getInstance() {
		if (gson == null) {
			new GsonUtil();
		}
		return gson;
	}
}
