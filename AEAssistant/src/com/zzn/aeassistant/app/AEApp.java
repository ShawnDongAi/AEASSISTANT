package com.zzn.aeassistant.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sqlcipher.database.SQLiteDatabase;
import android.app.Activity;
import android.app.Application;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.zzn.aeassistant.constants.FileCostants;
import com.zzn.aeassistant.database.AESQLiteHelper;
import com.zzn.aeassistant.database.CommentDBHelper;
import com.zzn.aeassistant.database.PostDBHelper;
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
		initImageLoader();
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
		File apkDir = new File(FileCostants.DIR_APK);
		apkDir.mkdirs();
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
		PostDBHelper.createTable();
		CommentDBHelper.createTable();
		// AttchDBHelper.createTable();
	}

	@SuppressWarnings("deprecation")
	private void initImageLoader() {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				instance)
				.threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				// .memoryCache(new WeakMemoryCache())
				// .memoryCacheSize(2 * 1024 * 1024)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCacheFileCount(100)
				.discCache(
						new UnlimitedDiskCache(new File(
								FileCostants.DIR_SCANNING)))
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.imageDownloader(
						new BaseImageDownloader(instance, 10 * 1000, 30 * 1000))
				.build();
		ImageLoader.getInstance().init(config);
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

	public void clearTask(Activity currentActivity) {
		try {
			for (Activity activity : activityTask) {
				if (activity != null && activity != currentActivity) {
					activity.finish();
				}
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