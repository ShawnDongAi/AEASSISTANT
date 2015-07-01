package com.zzn.aeassistant.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FilePathUtil {

	/**
	 * 判断文件路径是否存在,不存在就创建
	 * 
	 * @param filePath
	 */
	public static void CreateFilePath(String filePath) {
		File file = new File(filePath);
		String parentPath = file.getParent();
		File parenFile = new File(parentPath);
		if (!parenFile.exists() || !parenFile.isDirectory()) {
			parenFile.mkdirs();
		}
	}

	/**
	 * 复制文件
	 * 
	 * @param oldPath
	 * @param newPath
	 * @return
	 */
	@SuppressWarnings("resource")
	public static boolean copyFile(String oldPath, String newPath) {
		try {
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
}
