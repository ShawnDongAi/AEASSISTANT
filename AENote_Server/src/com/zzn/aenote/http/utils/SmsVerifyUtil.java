package com.zzn.aenote.http.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.log4j.Logger;
import com.google.gson.reflect.TypeToken;
import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.vo.BaseRep;

public class SmsVerifyUtil {
	protected static final Logger logger = Logger
			.getLogger(SmsVerifyUtil.class);
	private static final String APP_KEY = "7de015021ca4";
	private static final String APP_SECRET = "686a17437739306a8abf2e32daf21452";
	private static final String ZONE = "86";
	private static final String URL_VERIFY = "https://api.sms.mob.com/sms/verify";
	// 链接超时时间
	public static int conn_timeout = 10000;
	// 读取超时
	public static int read_timeout = 10000;
	// 请求方式
	public static String method = "POST";

	public static BaseRep verifySmsCode(BaseRep rep, String phone, String code) {
		StringBuilder params = new StringBuilder();
		params.append("appkey=" + APP_KEY);
		params.append("&zone=" + ZONE);
		params.append("&phone=" + phone);
		params.append("&code=" + code);
		HttpURLConnection conn = null;
		try {
			conn = build();
			conn.addRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			conn.addRequestProperty("Accept", "application/json");
			conn.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.write(params.toString().getBytes(Charset.forName("UTF-8")));
			out.flush();
			out.close();
			conn.connect();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String resultString = parsRtn(conn.getInputStream());
				Map<String, Integer> result = GsonUtil.getInstance().fromJson(
						resultString, new TypeToken<Map<String, Integer>>() {
						}.getType());
				int statusCode = result.get("status");
				logger.info(phone + ":短信验证结果===》" + statusCode);
				if (statusCode == 200) {
					rep.setRES_CODE(Global.RESP_SUCCESS);
					rep.setRES_MESSAGE("验证成功");
				} else if (statusCode == 512 || statusCode == 513
						|| statusCode == 514 || statusCode == 515
						|| statusCode == 517 || statusCode == 519
						|| statusCode == 526) {
					rep.setRES_CODE(Global.SMSCODE_VERIFY_ERROR);
					rep.setRES_MESSAGE("短信验证功能不可用,请联系系统管理员");
				} else if (statusCode == 518) {
					rep.setRES_CODE(Global.SMSCODE_VERIFY_ERROR);
					rep.setRES_MESSAGE("电话号码不正确,请重新输入");
				} else if (statusCode == 520) {
					rep.setRES_CODE(Global.SMSCODE_VERIFY_ERROR);
					rep.setRES_MESSAGE("验证码不正确,请重试");
				}
			} else {
				throw new Exception(conn.getResponseCode() + " "
						+ conn.getResponseMessage());
			}
		} catch (KeyManagementException e) {
			e.printStackTrace();
			rep.setRES_CODE(Global.SMSCODE_VERIFY_ERROR);
			rep.setRES_MESSAGE("短信验证码验证失败,请重试");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			rep.setRES_CODE(Global.SMSCODE_VERIFY_ERROR);
			rep.setRES_MESSAGE("短信验证码验证失败,请重试");
		} catch (IOException e) {
			e.printStackTrace();
			rep.setRES_CODE(Global.SMSCODE_VERIFY_ERROR);
			rep.setRES_MESSAGE("短信验证码验证失败,请重试");
		} catch (Exception e) {
			e.printStackTrace();
			rep.setRES_CODE(Global.SMSCODE_VERIFY_ERROR);
			rep.setRES_MESSAGE("短信验证码验证失败,请重试");
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return rep;
	}

	private static HttpURLConnection build() throws NoSuchAlgorithmException,
			KeyManagementException, IOException {
		HttpURLConnection conn = null;
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs,
					String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs,
					String authType) {
			}
		} };
		// Install the all-trusting trust manager
		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, trustAllCerts, new SecureRandom());
		// ip host verify
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				return urlHostName.equals(session.getPeerHost());
			}
		};
		// set ip host verify
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		URL url = new URL(URL_VERIFY);
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);// POST
		conn.setConnectTimeout(conn_timeout);
		conn.setReadTimeout(read_timeout);
		return conn;
	}

	private static String parsRtn(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = null;
		boolean first = true;
		while ((line = reader.readLine()) != null) {
			if (first) {
				first = false;
			} else {
				buffer.append("\n");
			}
			buffer.append(line);
		}
		return buffer.toString();
	}
}
