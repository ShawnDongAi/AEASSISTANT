package com.zzn.aeassistant.view.cropimage;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;

import com.zzn.aeassistant.util.AEThreadManager;
import com.zzn.aeassistant.util.AttchUtil;
import com.zzn.aeassistant.util.StringUtil;
import com.zzn.aeassistant.view.AEProgressDialog;

/*
 * Modified from original in AOSP.
 */
public class CropUtil {

	public static void closeSilently(Closeable c) {
		if (c == null)
			return;
		try {
			c.close();
		} catch (Throwable t) {
			// Do nothing
		}
	}

	public static int getExifRotation(File imageFile) {
		if (imageFile == null)
			return 0;
		try {
			ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
			// We only recognize a subset of orientation tag values
			switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_UNDEFINED)) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				return 90;
			case ExifInterface.ORIENTATION_ROTATE_180:
				return 180;
			case ExifInterface.ORIENTATION_ROTATE_270:
				return 270;
			default:
				return ExifInterface.ORIENTATION_UNDEFINED;
			}
		} catch (IOException e) {
			return 0;
		}
	}

	public static boolean copyExifRotation(File sourceFile, File destFile) {
		if (sourceFile == null || destFile == null)
			return false;
		try {
			ExifInterface exifSource = new ExifInterface(
					sourceFile.getAbsolutePath());
			ExifInterface exifDest = new ExifInterface(
					destFile.getAbsolutePath());
			exifDest.setAttribute(ExifInterface.TAG_ORIENTATION,
					exifSource.getAttribute(ExifInterface.TAG_ORIENTATION));
			exifDest.saveAttributes();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static File getFromMediaUri(Context context, Uri uri) {
		String path = AttchUtil.getPath(context, uri);
		if (path == null || StringUtil.isEmpty(path)) {
			return null;
		}
		return new File(path);
	}

	public static void startBackgroundJob(MonitoredActivity activity, Runnable job, Handler handler) {
		// Make the progress dialog uncancelable, so that we can gurantee
		// the thread will be done before the activity getting destroyed
		AEProgressDialog.showLoadingDialog(activity);
		AEThreadManager.getInstance().addThread(new BackgroundJob(activity, job, handler));
	}

	private static class BackgroundJob extends
			MonitoredActivity.LifeCycleAdapter implements Runnable {

		private final MonitoredActivity mActivity;
		private final Runnable mJob;
		private final Handler mHandler;
		private final Runnable mCleanupRunner = new Runnable() {
			public void run() {
				mActivity.removeLifeCycleListener(BackgroundJob.this);
				AEProgressDialog.dismissLoadingDialog();
			}
		};

		public BackgroundJob(MonitoredActivity activity, Runnable job, Handler handler) {
			mActivity = activity;
			mJob = job;
			mActivity.addLifeCycleListener(this);
			mHandler = handler;
		}

		public void run() {
			try {
				mJob.run();
			} finally {
				mHandler.post(mCleanupRunner);
			}
		}

		@Override
		public void onActivityDestroyed(MonitoredActivity activity) {
			// We get here only when the onDestroyed being called before
			// the mCleanupRunner. So, run it now and remove it from the queue
			mCleanupRunner.run();
			mHandler.removeCallbacks(mCleanupRunner);
		}

		@Override
		public void onActivityStopped(MonitoredActivity activity) {
			AEProgressDialog.dismissLoadingDialog();
		}

		@Override
		public void onActivityStarted(MonitoredActivity activity) {
			AEProgressDialog.dismissLoadingDialog();
		}
	}

}
