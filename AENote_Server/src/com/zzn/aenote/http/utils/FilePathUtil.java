package com.zzn.aenote.http.utils;

import java.io.File;

public class FilePathUtil {

	public static void createFilePath(String filePath) {
		File file = new File(filePath);
		String parentPath = file.getParent();
		File parenFile = new File(parentPath);
		if (!parenFile.exists() || !parenFile.isDirectory()) {
			parenFile.mkdirs();
		}
	}
	
	public static void createDirectory(String directoryPath) {
		File file = new File(directoryPath);
		if (!file.exists() || !file.isDirectory()) {
			file.mkdirs();
		}
	}
}
