package com.zzn.aeassistant.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.vo.HttpResult;

@SuppressWarnings("deprecation")
public class AEHttpUtil {
	private static final String CHARSET = HTTP.UTF_8;

	public static HttpResult doPost(String urlString, byte[] param) {
		HttpResult result = new HttpResult();
		result.setRES_CODE(HttpResult.CODE_FAILED);
		result.setRES_MESSAGE(AEApp.getInstance()
				.getString(R.string.http_error));
		if (!PhoneUtil.isNetworkConnected()) {
			result.setRES_MESSAGE(AEApp.getInstance().getString(
					R.string.http_null));
			return result;
		}
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
			result = GsonUtil.getInstance().fromJson(resultData.toString(),
					HttpResult.class);
			if (StringUtil.isEmpty(result.getRES_CODE())) {
				result.setRES_CODE(HttpResult.CODE_FAILED);
			}
			if (StringUtil.isEmpty(result.getRES_MESSAGE())) {
				result.setRES_MESSAGE(AEApp.getInstance().getString(
						R.string.http_error));
			}
			LogUtil.i("请求读取成功");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			LogUtil.i("请求错误URL地址");
		} catch (EOFException e) {
			e.printStackTrace();
			LogUtil.i("EOFException 合法异常");
		} catch (Exception e) {
			e.printStackTrace();
			result.setRES_MESSAGE(AEApp.getInstance().getString(
					R.string.http_out));
		}
		if (result.getRES_CODE() == null) {
			result.setRES_CODE(HttpResult.CODE_FAILED);
			result.setRES_MESSAGE(AEApp.getInstance()
					.getString(R.string.http_out));
		}
		return result;
	}

	public static HttpResult doPost(String urlString, String param) {
		byte[] paramByte = null;
		paramByte = param.getBytes();
		return doPost(urlString, paramByte);
	}

	public static HttpResult doGet(String urlString) {
		HttpResult result = new HttpResult();
		result.setRES_CODE(HttpResult.CODE_FAILED);
		result.setRES_MESSAGE(AEApp.getInstance()
				.getString(R.string.http_error));
		if (!PhoneUtil.isNetworkConnected()) {
			result.setRES_MESSAGE(AEApp.getInstance().getString(
					R.string.http_null));
			return result;
		}
		StringBuffer resultData = new StringBuffer();
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setConnectTimeout(10 * 1000);
			connection.setReadTimeout(20 * 1000);
			connection.setUseCaches(false);// 不用缓存
			connection.setInstanceFollowRedirects(true);// 重新传入
			connection.connect();
			InputStreamReader downInputStream = new InputStreamReader(
					connection.getInputStream());
			BufferedReader reader = new BufferedReader(downInputStream);
			String temp = "";
			while ((temp = reader.readLine()) != null) {
				resultData.append(temp);
			}
			reader.close();
			downInputStream.close();
			connection.disconnect();
			result = GsonUtil.getInstance().fromJson(resultData.toString(),
					HttpResult.class);
			LogUtil.i("请求读取成功");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			LogUtil.i("请求错误URL地址");
		} catch (EOFException e) {
			e.printStackTrace();
			LogUtil.i("EOFException 合法异常");
		} catch (Exception e) {
			e.printStackTrace();
			result.setRES_MESSAGE(AEApp.getInstance().getString(
					R.string.http_out));
		}
		if (result == null) {
			result = new HttpResult();
		}
		if (result.getRES_CODE() == null) {
			result.setRES_CODE(HttpResult.CODE_FAILED);
			result.setRES_MESSAGE(AEApp.getInstance()
					.getString(R.string.http_out));
		}
		return result;
	}

	public static HttpResult doPostWithFile(String urlString,
			List<String> filePaths, Map<String, String> params) {
		HttpResult result = new HttpResult();
		result.setRES_CODE(HttpResult.CODE_FAILED);
		result.setRES_MESSAGE(AEApp.getInstance()
				.getString(R.string.http_error));
		if (!PhoneUtil.isNetworkConnected()) {
			result.setRES_MESSAGE(AEApp.getInstance().getString(
					R.string.http_null));
			return result;
		}
		MultipartEntity mpEntity = new MultipartEntity();
		try {
			addParams(params, mpEntity);
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
		addFiles(filePaths, mpEntity);
		HttpPost post = new HttpPost(urlString);
		post.setEntity(mpEntity);
		try {
			HttpResponse resp = getHttpClient().execute(post);
			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = GsonUtil.getInstance().fromJson(
						EntityUtils.toString(resp.getEntity(), CHARSET),
						HttpResult.class);
				LogUtil.i("请求读取成功");
			} else {
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (result == null) {
			result = new HttpResult();
		}
		if (result.getRES_CODE() == null) {
			result.setRES_CODE(HttpResult.CODE_FAILED);
			result.setRES_MESSAGE(AEApp.getInstance().getString(
					R.string.http_out));
		}
		return result;
	}

	private static HttpClient getHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpProtocolParams
				.setUserAgent(
						params,
						"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
								+ "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
		/* 从连接池中取连接的超时时间 */
		ConnManagerParams.setTimeout(params, 30000);
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
		// 支持HTTP和HTTPS两种模式
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
				params, schReg);
		HttpClient client = new DefaultHttpClient(conMgr, params);
		return client;
	}

	/**
	 * 添加参数
	 * 
	 * @param params
	 * @param mpEntity
	 * @throws Exception
	 */
	private static void addParams(Map<String, String> params,
			MultipartEntity mpEntity) throws Exception {
		if (null != params && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				String value = entry.getValue();
				if (null == value) {
					value = "";
				}
				StringBody par = new StringBody(value);
				mpEntity.addPart(entry.getKey(), par);
			}
		}
	}

	/**
	 * 添加文件
	 * 
	 * @param filePaths
	 * @param mpEntity
	 */
	private static void addFiles(List<String> filePaths,
			MultipartEntity mpEntity) {
		if (null != filePaths && filePaths.size() > 0) {
			for (String path : filePaths) {
				File file = new File(path);
				if (file.exists()) {
					FileBody fileBody = new FileBody(file);
					mpEntity.addPart(file.getName(), fileBody);
				}
			}
		}
	}
}
