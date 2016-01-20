package com.zzn.aenote.http.push;

import java.util.Timer;
import java.util.TimerTask;

import com.zzn.aenote.http.utils.HttpUtil;

public class PushServer {
	private static final String ACCESS_ID = "2100177951";
	private static final String ACCESS_KEY = "AUZ26N96D7GW";
	private static final String SECRET_KEY = "a4ac03c4fda51c2731f773a9388e8f4c";
	private static final String PARAM_ACCESS_ID = "access_id=" + ACCESS_ID;
	private static final String URL_PUSH_HEAD = "http://openapi.xg.qq.com/v2/class/method?params";

	// 工作圈帖子更新
	public static final String TAG_POST = "0001";
	// 工作圈帖子评论更新
	public static final String TAG_COMMENT = "0002";

	public static void push() {
//		HttpUtil.doPost(URL_PUSH_HEAD, buildParams());
		new Timer(true).schedule(pushTask, 60000);
	}
	
	private static TimerTask pushTask = new TimerTask() {
		@Override
		public void run() {
			push();
		}
	};

	private static String buildParams(String title, String content) {
		return "";
	}

	private static String buildSign(String method, String url, String title, String content) {
		return "";
	}
}
