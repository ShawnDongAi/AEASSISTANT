package com.zzn.aeassistant.util;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.constants.CodeConstants;

public class AttchUtil {

	/**
	 * 拍照
	 * 
	 * @param activity
	 * @param savePath
	 */
	public static void capture(Activity activity, String savePath) {
		if (!PhoneUtil.isExternalStorageMounted()) {
			return;
		}
		try {
			File file = new File(savePath);
			FilePathUtil.CreateFilePath(savePath);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			intent.putExtra("photoPath", file);
			activity.startActivityForResult(intent,
					CodeConstants.REQUEST_CODE_TAKEPHOTO);
		} catch (Exception e) {
			ToastUtil.show(R.string.pick_error);
		}
	}

	/**
	 * 拍照
	 * 
	 * @param fragment
	 * @param savePath
	 */
	public static void capture(Fragment fragment, String savePath) {
		if (!PhoneUtil.isExternalStorageMounted()) {
			return;
		}
		try {
			File file = new File(savePath);
			FilePathUtil.CreateFilePath(savePath);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			intent.putExtra("photoPath", file);
			fragment.startActivityForResult(intent,
					CodeConstants.REQUEST_CODE_TAKEPHOTO);
		} catch (Exception e) {
			ToastUtil.show(R.string.pick_error);
		}
	}

	/**
	 * 从相册选取图片
	 * 
	 * @param activity
	 */
	public static void getPictureFromGallery(Activity activity) {
		if (!PhoneUtil.isExternalStorageMounted()) {
			return;
		}
		try {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			activity.startActivityForResult(
					Intent.createChooser(intent,
							activity.getString(R.string.album)),
					CodeConstants.REQUEST_CODE_GETPHOTO);
		} catch (Exception e) {
			ToastUtil.show(R.string.pick_error);
		}
	}

	/**
	 * 从相册选取图片
	 * 
	 * @param fragment
	 */
	public static void getPictureFromGallery(Fragment fragment) {
		if (!PhoneUtil.isExternalStorageMounted()) {
			return;
		}
		try {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			fragment.startActivityForResult(
					Intent.createChooser(intent,
							fragment.getString(R.string.album)),
					CodeConstants.REQUEST_CODE_GETPHOTO);
		} catch (Exception e) {
			ToastUtil.show(R.string.pick_error);
		}
	}

	public static void record(Activity activity) {
		Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
		activity.startActivityForResult(intent,
				CodeConstants.REQUEST_CODE_VOICE);
	}

	public static void getFile(Activity activity) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			activity.startActivityForResult(
					Intent.createChooser(intent,
							activity.getString(R.string.choose_file)),
					CodeConstants.REQUEST_CODE_GETFILE);
		} catch (android.content.ActivityNotFoundException ex) {
			ToastUtil.show(R.string.null_file_manager);
		}
	}

	public static void getFile(Fragment fragment) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			fragment.startActivityForResult(
					Intent.createChooser(intent,
							fragment.getString(R.string.choose_file)),
					CodeConstants.REQUEST_CODE_GETFILE);
		} catch (android.content.ActivityNotFoundException ex) {
			ToastUtil.show(R.string.null_file_manager);
		}
	}

	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri
				.getAuthority());
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}
}
