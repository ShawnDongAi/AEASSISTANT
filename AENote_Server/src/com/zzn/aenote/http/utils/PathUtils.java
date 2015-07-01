package com.zzn.aenote.http.utils;

import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.springframework.util.Assert;

import com.zzn.aenote.http.AppException;

public abstract class PathUtils {
	public final static String getPath(Class<?> clazz) {
		Assert.notNull(clazz);
		ClassLoader loader = clazz.getClassLoader();
		String clsName = clazz.getName() + ".class";
		Package pack = clazz.getPackage();
		String path = "";

		if (pack != null) {

			String packName = pack.getName();
			clsName = clsName.substring(packName.length() + 1);
			if (packName.indexOf(".") < 0) {
				path = packName + "/";
			} else {
				int start = 0, end = 0;
				end = packName.indexOf(".");
				while (end != -1) {
					path = path + packName.substring(start, end) + "/";
					start = end + 1;
					end = packName.indexOf(".", start);
				}
				path = path + packName.substring(start) + "/";
			}
		}
		URL url = loader.getResource(path + clsName);

		String realPath = url.getPath();

		int pos = realPath.indexOf("file:");
		if (pos > -1) {
			realPath = realPath.substring(pos + 5);
		}
		pos = realPath.indexOf(path + clsName);
		realPath = realPath.substring(0, pos - 1);

		if (realPath.endsWith("!")) {
			realPath = realPath.substring(0, realPath.lastIndexOf("/"));
		}

		try {
			realPath = java.net.URLDecoder.decode(realPath, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new  AppException("真实路径获取错误.",e);
		} 

		return realPath;
	}

}
