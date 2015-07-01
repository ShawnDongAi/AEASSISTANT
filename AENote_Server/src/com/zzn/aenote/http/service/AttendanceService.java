package com.zzn.aenote.http.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.BaseService;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.utils.UtilUniqueKey;

public class AttendanceService extends BaseService {
	private static final Logger logger = Logger
			.getLogger(AttendanceService.class);
	private static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public boolean scanning(String user_id, String project_id, String photo,
			String longitude, String latitude, String normal) {
		boolean result = false;
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("user_id", user_id);
			data.put("project_id", project_id);
			data.put("photo", photo);
			data.put("longitude", longitude);
			data.put("latitude", latitude);
			data.put("normal", normal);
			String date = format.format(new Date(System.currentTimeMillis()));
			data.put("time", date);
			getJdbc().execute(getSql("scanning", data));
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
