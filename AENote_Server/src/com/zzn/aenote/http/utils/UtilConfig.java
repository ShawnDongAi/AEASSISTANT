package com.zzn.aenote.http.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class UtilConfig {
	static final Properties prop;
	static String workdir = System.getProperty("user.dir");
	
	static {
		String PROPERTIES_FILE_SYSTEM_VAR = "aenote.server.properties";
		String propertiesFile = System.getProperty(PROPERTIES_FILE_SYSTEM_VAR);
		if ( propertiesFile == null ) {
			propertiesFile = "/resource/application.properties";
		}
		System.out.println("work dir : " + workdir);
		prop = loadFile(propertiesFile);
	}
	
	public static Properties loadFile(String propertiesFile) {
		Properties proper = new Properties();
		try {
			File f = new File(workdir + propertiesFile);
			if ( f.exists() && f.isFile() ) {
				// 优先读取工作目录下的配置文件
				proper.load(new FileInputStream(f));
			} else {
				proper.load(UtilConfig.class.getResourceAsStream(propertiesFile));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return proper;
	}

	public static int getInt(String key) {
		return getInt(key, 100);
	}
	
	public static int getInt(String key, int defaultValue) {
		try {
			return  Integer.parseInt(prop.getProperty(key, String.valueOf(defaultValue)));
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public static String getString(String key, String defaultValue) {
		return prop.getProperty(key, defaultValue);
	}
	
	public static String getString(String key) {
		return prop.getProperty(key);
	}
	
	public static long getLong(String key) {
		return getLong(key, 1000L);
	}
	
	public static long getLong(String key, long defaultValue) {
		try {
			return  Long.parseLong(prop.getProperty(key, String.valueOf(defaultValue)));
		} catch (Exception e) {
			return defaultValue;
		}
	}
}
