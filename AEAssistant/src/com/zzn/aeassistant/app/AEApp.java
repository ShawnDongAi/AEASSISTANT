package com.zzn.aeassistant.app;

import java.io.File;

import net.sqlcipher.database.SQLiteDatabase;
import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.zzn.aeassistant.constants.FileCostants;
import com.zzn.aeassistant.database.AESQLiteHelper;
import com.zzn.aeassistant.database.AttchDBHelper;
import com.zzn.aeassistant.database.GroupDBHelper;
import com.zzn.aeassistant.database.UserDBHelper;
import com.zzn.aeassistant.util.PhoneUtil;
import com.zzn.aeassistant.vo.UserVO;

/**
 * 全局Application
 * 
 * @author Shawn
 */
public class AEApp extends Application {
	private static AEApp instance = null;
	private static AESQLiteHelper mAESQLiteHelper = null;
	private static UserVO mUser;

	public AEApp() {
		instance = this;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Thread.setDefaultUncaughtExceptionHandler(new AEExceptionHandler());
		SQLiteDatabase.loadLibs(this);
		mAESQLiteHelper = new AESQLiteHelper(instance);
		creatFileOrDir();
		createDatabase();
		SDKInitializer.initialize(this);
	}

	public static AEApp getInstance() {
		return instance;
	}

	public static AESQLiteHelper getDbHelper() {
		return mAESQLiteHelper;
	}
	
	public static void setUser(UserVO user) {
		mUser = user;
	}
	
	public static UserVO getCurrentUser() {
		return mUser;
	}

	private void creatFileOrDir() {
		if (!PhoneUtil.isExternalStorageMounted()) {
			return;
		}
		File baseDir = new File(FileCostants.DIR_BASE);
		baseDir.mkdirs();
		File scanningDir = new File(FileCostants.DIR_SCANNING);
		scanningDir.mkdirs();
		File headDir = new File(FileCostants.DIR_HEAD);
		headDir.mkdirs();
//		File imgDir = new File(FileCostants.DIR_IMG);
//		imgDir.mkdirs();
//		File audioDir = new File(FileCostants.DIR_AUDIO);
//		audioDir.mkdirs();
//		File otherDir = new File(FileCostants.DIR_OTHER);
//		otherDir.mkdirs();
	}

	private void createDatabase() {
//		GroupDBHelper.createTable();
		UserDBHelper.createTable();
//		AttchDBHelper.createTable();
	}
}