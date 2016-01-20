package com.zzn.aenote.http.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil {
	public static int doPost(String urlString, byte[] param) {
		int result = 15;
		StringBuffer resultData = new StringBuffer();
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			if (connection == null) {
				return result;
			}
			connection.setConnectTimeout(10 * 1000);
			connection.setReadTimeout(20 * 1000);
			connection.setDoOutput(true);// allows output
			connection.setDoInput(true);// allows input
			connection.setRequestMethod("POST");// post 请求大写
			connection.setUseCaches(false);// 不用缓存
			connection.setInstanceFollowRedirects(true);// 重新传入
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.connect();
			DataOutputStream outputStream = new DataOutputStream(
					connection.getOutputStream());
			outputStream.write(param);
			outputStream.flush();
			outputStream.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String temp = "";
			while ((temp = reader.readLine()) != null) {
				resultData.append(temp);
			}
			reader.close();
			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (EOFException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static int doPost(String urlString, String param) {
		byte[] paramByte = null;
		paramByte = param.getBytes();
		return doPost(urlString, paramByte);
	}
}