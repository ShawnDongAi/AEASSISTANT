package com.zzn.aeassistant.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class MD5Utils {

	public static String getMD5ofStr(String key) throws Exception {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		MessageDigest mdt;
		try {
			mdt = java.security.MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("MD5混淆过程出错。", e);
		}
		mdt.update(key.getBytes());
		byte tmp[] = mdt.digest();
		char str[] = new char[16 * 2];
		int k = 0;
		for (int i = 0; i < 16; i++) {
			byte byte0 = tmp[i];
			str[k++] = hexDigits[byte0 >>> 4 & 0xf];
			str[k++] = hexDigits[byte0 & 0xf];
		}
		return new String(str);
	}

}
