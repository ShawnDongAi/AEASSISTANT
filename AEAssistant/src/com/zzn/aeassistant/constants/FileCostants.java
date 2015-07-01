package com.zzn.aeassistant.constants;

import java.io.File;

import android.os.Environment;

public class FileCostants {

	public static final String DIR_BASE = Environment
			.getExternalStorageDirectory() + File.separator + "AEAssistant";
	public static final String DIR_SCANNING = DIR_BASE + File.separator
			+ "SCANNING" + File.separator;
	public static final String DIR_HEAD = DIR_BASE + File.separator + "HEAD"
			+ File.separator;
	public static final String DIR_IMG = DIR_BASE + File.separator + "IMG"
			+ File.separator;
	public static final String DIR_AUDIO = DIR_BASE + File.separator + "AUDIO"
			+ File.separator;
	public static final String DIR_DOC = DIR_BASE + File.separator + "DOC"
			+ File.separator;
	public static final String DIR_EXCEL = DIR_BASE + File.separator + "EXCEL"
			+ File.separator;
	public static final String DIR_OTHER = DIR_BASE + File.separator + "OTHER"
			+ File.separator;
	public static final String DIR_APK = DIR_BASE + File.separator + "APK"
			+ File.separator;
}
