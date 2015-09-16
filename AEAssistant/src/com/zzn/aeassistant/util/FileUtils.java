package com.zzn.aeassistant.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

/**
 * 文件文件夹操作类
 * 
 * @author Jack
 * @version 1.0
 * 
 */
public class FileUtils {

	/**
	 * 创建文件夹
	 * 
	 * @param path
	 */
	public static void createFloder(String path) {
		File floder = new File(path);
		if (!floder.exists()) {
			floder.mkdirs();
		}
	}

	/**
	 * 删除文件
	 */
	public static boolean deleteFile(String filename) {
		File file = new File(filename);

		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					deleteFile(files[i].getPath()); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		}
		return true;
	}

	/**
	 * 剪切文件
	 */
	public static boolean cutFile(String SourchFile, String DescFile) {
		boolean result = false;
		try {
			File from = new File(SourchFile);
			if (!from.exists())
				return false;
			File to = new File(DescFile);
			from.renameTo(to);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 复制文件
	 */
	public static boolean copyFile(String SourchFile, String DescFile) {
		boolean result = false;
		return result;
	}

	/**
	 * 复制assets文件到sd卡
	 * 
	 * @param context
	 * @param assetDir
	 * @param dir
	 */
	public static void copyAssetsToSD(Context context, String assetDir,
			String dir) {
		String[] files;
		try {
			files = context.getResources().getAssets().list(assetDir);
		} catch (IOException e1) {
			return;
		}
		File mWorkingPath = new File(dir);
		// if this directory does not exists, make one.
		if (!mWorkingPath.exists()) {
			if (!mWorkingPath.mkdirs()) {

			}
		}

		for (int i = 0; i < files.length; i++) {
			try {
				String fileName = files[i];
				// we make sure file name not contains '.' to be a folder.
				if (!fileName.contains(".")) {
					if (0 == assetDir.length()) {
						copyAssetsToSD(context, fileName, dir + fileName + "/");
					} else {
						copyAssetsToSD(context, assetDir + "/" + fileName, dir
								+ fileName + "/");
					}
					continue;
				}
				File outFile = new File(mWorkingPath, fileName);
				if (outFile.exists())
					outFile.delete();
				InputStream in = null;
				if (0 != assetDir.length())
					in = context.getAssets().open(assetDir + "/" + fileName);
				else
					in = context.getAssets().open(fileName);
				OutputStream out = new FileOutputStream(outFile);

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				in.close();
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 
	 * @param context
	 * @param fileName
	 * @param fullName
	 * @return
	 */
	public static File assetFileToCacheDir(Context context, String fileName,
			String fullName) {
		try {
			InputStream is = context.getAssets().open(fileName);
			File file = new File(fullName);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] temp = new byte[1024];

			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}
			fos.close();
			is.close();
			return file;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param path
	 * @return
	 */
	public static boolean exists(String path) {
		File file = new File(path);
		return file.exists();
	}

	/**
	 * 取文件大小
	 * 
	 * @param f
	 * @return
	 */
	public static long getFileSize(File f) {

		long size = 0;

		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size / 1024 / 1024;
	}

	/**
	 * 取文件大小
	 */
	public static String getFileSize(String filename) {
		String size = "0M";
		File file = new File(filename);
		if (file.exists()) {
			size = "" + getFileSize(file);
		}
		return size;
	}

	/**
	 * 打开文件
	 */
	public static void openFile(Context context, File f) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);

		/* 调用getMIMEType()来取得MimeType */
		String type = getMIMEType(f);
		/* 设置intent的file与MimeType */
		intent.setDataAndType(Uri.fromFile(f), type);
		context.startActivity(intent);
	}

	/**
	 * 判断文件MimeType的method
	 */
	private static String getMIMEType(File f) {
		String type = "";
		String name = f.getName();
		/* 取得扩展名 */
		Locale locale = Locale.getDefault();
		String end = name.substring(name.lastIndexOf(".") + 1, name.length())
				.toLowerCase(locale);

		/* 依扩展名的类型决定MimeType */
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else if (end.equals("apk")) {
			/* android.permission.INSTALL_PACKAGES */
			type = "application/vnd.android.package-archive";
		} else {
			type = "*";
		}
		/* 如果无法直接打开，就跳出软件列表给用户选择 */
		if (end.equals("apk")) {
		} else {
			type += "/*";
		}
		return type;
	}

	/**
	 * 提取文件扩展名
	 */
	public static String getFileExt(String filename) {
		String ext = "";
		if (!StringUtil.isEmpty(filename)) {
			String[] name = filename.split("\\.");
			if (name.length > 0)
				ext = "." + name[name.length - 1];
		}
		return ext;
	}

	/**
	 * 遍历文件夹
	 */
	private static List<String> filePathList = new ArrayList<String>();

	public static List<String> getFileListPath(String filename) {
		List<String> list = new ArrayList<String>();
		File file = new File(filename);
		if (file.exists())
			getAllFiles(file);
		return list;
	}

	/**
	 * 循环遍历所有文件
	 * 
	 * @param root
	 */
	private static void getAllFiles(File root) {
		File files[] = root.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					getAllFiles(f);
				} else {
					filePathList.add(f.getPath());
				}
			}
		} else {
			filePathList.clear();
		}
	}

	/**
	 * bitmap保存成文件
	 *
	 * @param bitmap
	 * @param filename
	 * @return
	 */
	public static boolean bitmapToFile(Bitmap bitmap, String filename) {
		boolean result = false;
		FileOutputStream fos = null;
		try {
			// create a file to write bitmap data
			File f = new File(filename);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 100, bos); /* ignored for PNG */
			byte[] bitmapdata = bos.toByteArray();

			// write the bytes in file
			fos = new FileOutputStream(f);
			fos.write(bitmapdata);
			fos.close();
			result = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * bitmap保存成文件
	 * 
	 * @param bitmap
	 * @param filename
	 * @return
	 */
	public static boolean bitmapToFile(Bitmap bitmap, String filename,
			CompressFormat format) {
		boolean result = false;
		FileOutputStream fos = null;
		try {
			// create a file to write bitmap data
			File f = new File(filename);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(format, 100, bos); /* ignored for PNG */
			byte[] bitmapdata = bos.toByteArray();

			// write the bytes in file
			fos = new FileOutputStream(f);
			fos.write(bitmapdata);
			fos.close();
			result = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 取本地uri路径
	 * 
	 * @param context
	 * @param uri
	 * @return
	 */
	public static String getUriPath(Context context, Uri uri) {
		String fileName = null;
		Uri filePathUri = uri;
		if (uri != null) {
			if (uri.getScheme().toString().compareTo("content") == 0) {
				// content://开头的uri
				Cursor cursor = context.getContentResolver().query(uri, null,
						null, null, null);

				if (cursor != null && cursor.moveToFirst()) {
					int column_index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					fileName = cursor.getString(column_index); // 取出文件路径

					// Android 4.1 更改了SD的目录，sdcard映射到/storage/sdcard0
					if (!fileName.startsWith("/storage")
							&& !fileName.startsWith("/mnt")) {
						// 检查是否有”/mnt“前缀
						fileName = "/mnt" + fileName;
					}
					cursor.close();
				}
			}

			else if (uri.getScheme().compareTo("file") == 0) // file:///开头的uri
			{
				fileName = filePathUri.toString();// 替换file://
				fileName = filePathUri.toString().replace("file://", "");
				int index = fileName.indexOf(Environment
						.getExternalStorageDirectory().getPath());
				fileName = index == -1 ? fileName : fileName.substring(index);
				if (!fileName.startsWith("/mnt")) {
					// 加上"/mnt"头
					fileName += "/mnt";
				}
			}
		}
		return fileName;
	}

	/**
	 * 根据目录路径逐级创建目录
	 * 
	 * @param directoryPath
	 *            目录的路径
	 * @return true代表成功 false代表失败
	 */
	public static boolean createDirectory(String directoryPath) {
		StringTokenizer st = new StringTokenizer(directoryPath, "/");
		String currentPath = st.nextToken() + "/";
		String nextPath = currentPath;
		while (st.hasMoreTokens()) {
			currentPath = st.nextToken() + "/";
			nextPath += currentPath;
			File inbox = new File(nextPath);
			if (!inbox.isDirectory())
				inbox.delete();
			if (!inbox.exists())
				inbox.mkdir();
		}
		File directory = new File(directoryPath);
		if (directory.isDirectory()) {
			return true;
		}
		return false;
	}
}
