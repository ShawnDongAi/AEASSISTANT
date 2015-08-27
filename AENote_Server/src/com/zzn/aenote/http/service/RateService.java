package com.zzn.aenote.http.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.BaseService;

/**
 * 评价相关操作类
 * 
 * @author Shawn
 */
public class RateService extends BaseService {
	private static final Logger logger = Logger.getLogger(RateService.class);
	private static SimpleDateFormat allFormater = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	private static SimpleDateFormat formater = new SimpleDateFormat(
			"yyyy-MM-dd");

	/**
	 * 新增评价
	 * @param user_id
	 * @param rate_user
	 * @param rate
	 * @param content
	 * @param project_id
	 * @param root_id
	 */
	public boolean insertRate(String user_id, String rate_user, String rate,
			String content, String project_id, String root_id) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("user_id", user_id);
			data.put("rate_user", rate_user);
			data.put("rate", rate);
			if (content == null) {
				content = "";
			}
			data.put("content", content);
			Date date = new Date(System.currentTimeMillis());
			data.put("time", allFormater.format(date));
			data.put("project_id", project_id);
			data.put("root_id", root_id);
			getJdbc().execute(getSql("insert_rate", data));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 更新今日评价
	 * @param user_id
	 * @param rate_user
	 * @param rate
	 * @param content
	 * @param project_id
	 * @param root_id
	 */
	public boolean updateRate(String user_id, String rate_user, String rate,
			String content, String project_id, String root_id) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("user_id", user_id);
			data.put("rate_user", rate_user);
			data.put("rate", rate);
			if (content == null) {
				content = "";
			}
			data.put("content", content);
			Date date = new Date(System.currentTimeMillis());
			data.put("time", allFormater.format(date));
			data.put("date", formater.format(date));
			data.put("project_id", project_id);
			data.put("root_id", root_id);
			getJdbc().execute(getSql("update_rate", data));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 查询今日对其的评价信息
	 * @param user_id
	 * @param rate_user
	 * @return
	 */
	public List<Map<String, Object>> queryRateForToday(String user_id, String rate_user) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("user_id", user_id);
			data.put("rate_user", rate_user);
			Date date = new Date(System.currentTimeMillis());
			data.put("time", formater.format(date));
			result = getJdbc().queryForList(getSql("rate_today", data));
		} catch (Exception e) {
			logger.info(e);
		}
		return result;
	}
	
	/**
	 * 查询历史评价
	 * @param user_id
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> queryRateForHistory(String user_id, int page) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("user_id", user_id);
			data.put("start", (page * 20 + 1) + "");
			data.put("end", (page + 1) * 20 + "");
			result = getJdbc().queryForList(getSql("rate_history", data));
		} catch (Exception e) {
			logger.info(e);
		}
		return result;
	}
}
