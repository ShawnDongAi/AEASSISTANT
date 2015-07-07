package com.zzn.aeassistant.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sqlcipher.database.SQLiteDatabase;
import android.app.Activity;
import android.app.Application;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.zzn.aeassistant.activity.user.LoginActivity;
import com.zzn.aeassistant.constants.FileCostants;
import com.zzn.aeassistant.database.AESQLiteHelper;
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
	private static BDLocation mCurrentLoc;
	private List<Activity> activityTask = new ArrayList<Activity>();

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
		if (mUser == null) {
			mUser = PreConfig.getUserVO();
		}
		return mUser;
	}

	public static BDLocation getCurrentLoc() {
		return mCurrentLoc;
	}

	public static void setCurrentLoc(BDLocation currentLoc) {
		mCurrentLoc = currentLoc;
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
		// File imgDir = new File(FileCostants.DIR_IMG);
		// imgDir.mkdirs();
		// File audioDir = new File(FileCostants.DIR_AUDIO);
		// audioDir.mkdirs();
		// File otherDir = new File(FileCostants.DIR_OTHER);
		// otherDir.mkdirs();
	}

	private void createDatabase() {
		// GroupDBHelper.createTable();
		UserDBHelper.createTable();
		// AttchDBHelper.createTable();
	}

	public void remove(Activity activity) {
		try {
			if (activityTask != null && activityTask.contains(activity)) {
				activityTask.remove(activity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void add(Activity activity) {
		try {
			if (activityTask != null) {
				activityTask.add(activity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void exit() {
		try {
			for (Activity activity : activityTask) {
				if (activity != null) {
					activity.finish();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
	}
}