package com.zzn.aenote.http.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DESCoderUtil {
	private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
	private static final String ALGORITHM_KEY = "AES";

	/**
	 * 解密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String data, String key) throws Exception {
		SecretKey secretKey = new SecretKeySpec(
				parseHexStr2Byte(MD5Utils.getMD5ofStr(key)), ALGORITHM_KEY);
		IvParameterSpec iv = new IvParameterSpec(
				parseHexStr2Byte(MD5Utils.getMD5ofStr(key)));
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
		return new String(cipher.doFinal(parseHexStr2Byte(data)));
	}

	/**
	 * 加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String data, String key) throws Exception {
		SecretKey secretKey = new SecretKeySpec(
				parseHexStr2Byte(MD5Utils.getMD5ofStr(key)), ALGORITHM_KEY);
		IvParameterSpec iv = new IvParameterSpec(
				parseHexStr2Byte(MD5Utils.getMD5ofStr(key)));
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
		return parseByte2HexStr(cipher.doFinal(data.getBytes()));
	}

	/**
	 * 将二进制转换成16进制
	 * 
	 * @param buf
	 * @return
	 */
	private static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 将16进制转换为二进制
	 * 
	 * @param hexStr
	 * @return
	 */
	private static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
					16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}
}
