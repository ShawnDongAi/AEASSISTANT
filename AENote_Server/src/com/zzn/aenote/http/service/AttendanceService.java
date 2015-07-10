package com.zzn.aenote.http.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.BaseService;

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
			String date = format.format(new Date(System.currentTimeMillis()));
			data.put("time", date);
			data.put("photo", photo);
			data.put("project_id", project_id);
			data.put("parent_id", parent_id);
			data.put("root_id", root_id);
			data.put("address", address);
			data.put("longitude", longitude);
			data.put("latitude", latitude);
			data.put("normal", normal);
			data.put("status", "0");
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
			Date date = new Date(System.currentTimeMillis());
			data.put("time", format.format(date));
			data.put("date", dateFormat.format(date));
			data.put("status", "0");
			getJdbc().execute(getSql("update_scanning", data));
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<Map<String, Object>> sumListByProject(String startDate,
			String endDate, String projectID) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			// Date start = dateFormat.parse(startDate);
			// Date end = dateFormat.parse(endDate);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("start_date", startDate);
			data.put("end_date", endDate);
			data.put("project_id", projectID);
			List<Map<String, Object>> attendanceList = getJdbc().queryForList(
					getSql("sum_list_by_project", data));
			if (attendanceList != null && attendanceList.size() > 0) {
				result.addAll(attendanceList);
				result.addAll(sumListByParent(startDate, endDate, projectID));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<Map<String, Object>> sumListByParent(String startDate,
			String endDate, String parent_id) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("start_date", startDate);
			data.put("end_date", endDate);
			data.put("parent_id", parent_id);
			List<Map<String, Object>> attendanceList = getJdbc().queryForList(
					getSql("sum_list_by_parent", data));
			if (attendanceList != null && attendanceList.size() > 0) {
				result.addAll(attendanceList);
				result.addAll(sumListByParent(startDate, endDate,
						attendanceList.get(0).get("project_id").toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<Map<String, Object>> sumListByUser(String startDate,
			String endDate, String user_id, int page) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("start_date", startDate);
			data.put("end_date", endDate);
			data.put("user_id", user_id);
			data.put("start", (page * 20 + 1) + "");
			data.put("end", (page + 1) * 20 + "");
			result = getJdbc().queryForList(getSql("sum_list_by_user", data));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int sumCountByUser(String startDate, String endDate, String user_id) {
		int result = 0;
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("start_date", startDate);
			data.put("end_date", endDate);
			data.put("user_id", user_id);
			result = getJdbc().queryForInt(getSql("sum_count_by_user", data));
			if (result < 0) {
				result = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
