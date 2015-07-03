package com.zzn.aenote.http.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.BaseService;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.utils.UtilUniqueKey;

public class AttendanceService extends BaseService {
	private static final Logger logger = Logger
			.getLogger(AttendanceService.class);
	private SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public boolean scanning(String user_id, String project_id,
			String parent_id, String root_id, String photo, String address,
			String longitude, String latitude, String normal) {
		boolean result = false;
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("user_id", user_id);
			data.put("project_id", project_id);
			data.put("parent_id", parent_id);
			data.put("root_id", root_id);
			data.put("photo", photo);
			data.put("address", address);
			data.put("longitude", longitude);
			data.put("latitude", latitude);
			data.put("normal", normal);
			data.put("status", "0");
			String date = format.format(new Date(System.currentTimeMillis()));
			data.put("time", date);
			getJdbc().execute(getSql("scanning", data));
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean isScanningToday(String project_id) {
		boolean result = false;
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("project_id", project_id);
			data.put("date",
					dateFormat.format(new Date(System.currentTimeMillis())));
			List<Map<String, Object>> attendanceList = getJdbc().queryForList(
					getSql("query_project_attendance_for_today", data));
			if (attendanceList != null && attendanceList.size() > 0) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean isScanningTodayVaild(String project_id) {
		boolean result = false;
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("project_id", project_id);
			data.put("date",
					dateFormat.format(new Date(System.currentTimeMillis())));
			List<Map<String, Object>> attendanceList = getJdbc().queryForList(
					getSql("query_project_vaild_attendance_for_today", data));
			if (attendanceList != null && attendanceList.size() > 0) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean updateScanning(String user_id, String project_id,
			String parent_id, String root_id, String photo, String address,
			String longitude, String latitude, String normal) {
		boolean result = false;
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("user_id", user_id);
			data.put("project_id", project_id);
			data.put("parent_id", parent_id);
			data.put("root_id", root_id);
			data.put("photo", photo);
			data.put("address", address);
			data.put("longitude", longitude);
			data.put("latitude", latitude);
			data.put("normal", normal);
			data.put("status", "0");
			Date date = new Date(System.currentTimeMillis());
			data.put("time", format.format(date));
			data.put("date", dateFormat.format(date));
			getJdbc().execute(getSql("update_scanning", data));
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
