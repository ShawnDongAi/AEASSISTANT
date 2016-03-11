package com.zzn.aeassistant.constants;

import java.io.File;

import com.zzn.aeassistant.app.AEApp;

import android.os.Environment;

public class FileCostants {

	public static final String DIR_BASE = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
			+ "Android" + File.separator + "data" + File.separator + AEApp.getInstance().getPackageName()
			+ File.separator;
	public static final String DIR_SCANNING = DIR_BASE + "SCANNING" + File.separator;
	public static final String DIR_HEAD = DIR_BASE + "HEAD" + File.separator;
	public static final String DIR_IMG = DIR_BASE + "IMG" + File.separator;
	public static final String DIR_AUDIO = DIR_BASE + "AUDIO" + File.separator;
	public static final String DIR_DOC = DIR_BASE + "DOC" + File.separator;
	public static final String DIR_EXCEL = DIR_BASE + "EXCEL" + File.separator;
	public static final String DIR_OTHER = DIR_BASE + "OTHER" + File.separator;
	public static final String DIR_APK = DIR_BASE + "APK" + File.separator;

	public static final String MB_HEAD = AEApp.getInstance().getFilesDir().getAbsolutePath() + File.separator + "HEAD"
			+ File.separator;
	public static final String MB_IMG = AEApp.getInstance().getFilesDir().getAbsolutePath() + File.separator + "IMG"
			+ File.separator;
	public static final String MB_APK = AEApp.getInstance().getFilesDir().getAbsolutePath() + File.separator + "APK"
			+ File.separator;
}
