package com.zzn.aenote.http.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.BaseService;
import com.zzn.aenote.http.utils.UtilUniqueKey;
import com.zzn.aenote.http.vo.TaskDetailVO;
import com.zzn.aenote.http.vo.TaskVO;

/**
 * 任务相关信息操作类
 * 
 * @author Shawn
 */
public class TaskService extends BaseService {
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 插入一条新任务
	 * 
	 * @param create_user_id
	 * @param create_project_id
	 * @param root_id
	 * @param taskDetails
	 * @return
	 */
	public boolean insertTask(String create_user_id, String create_project_id, String root_id,
			List<TaskDetailVO> taskDetails) {
		Map<String, Object> data = new HashMap<String, Object>();
		String task_id = UtilUniqueKey.getKey("task" + System.currentTimeMillis());
		try {
			data.put("task_id", task_id);
			data.put("create_user_id", create_user_id);
			data.put("create_project_id", create_project_id);
			data.put("root_id", root_id);
			Date time = new Date(System.currentTimeMillis());
			data.put("time", format.format(time));
			getJdbc().execute(getSql("insert_task", data));
			for (TaskDetailVO taskDetail : taskDetails) {
				data.clear();
				data.put("task_detail_id", UtilUniqueKey.getKey("taskDetail" + System.currentTimeMillis()));
				data.put("task_id", task_id);
				data.put("process_user_id", taskDetail.getProcess_user_id());
				data.put("content", taskDetail.getContent());
				data.put("attch_id", taskDetail.getAttch_id());
				data.put("start_time", taskDetail.getStart_time());
				data.put("status", "0");
				getJdbc().execute(getSql("insert_task_detail", data));
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			data.clear();
			data.put("task_id", task_id);
			getJdbc().execute(getSql("delete_task", data));
			getJdbc().execute(getSql("delete_task_detail", data));
			return false;
		}
	}
	
	/**
	 * 处理任务
	 * @param task_detail_id
	 * @param status
	 * @param process_content
	 * @param process_attch_id
	 * @return
	 */
	public boolean processTask(String task_detail_id, String status, String process_content, String process_attch_id) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("task_detail_id", task_detail_id);
			data.put("status", status);
			data.put("process_content", process_content);
			data.put("process_attch_id", process_attch_id);
			Date time = new Date(System.currentTimeMillis());
			data.put("end_time", format.format(time));
			getJdbc().execute(getSql("process_task_detail", data));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 查询全部任务
	 * 
	 * @param root_id
	 * @param page
	 * @return
	 */
	public List<TaskVO> queryTaskAll(String root_id, int page) {
		List<TaskVO> result;
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("root_id", root_id);
		data.put("start", (page * 20 + 1) + "");
		data.put("end", (page + 1) * 20 + "");
		result = getJdbc().queryForList(getSql("query_task_all", data));
		if (result == null) {
			result = new ArrayList<>();
		}
		for (TaskVO task : result) {
			data.clear();
			data.put("task_id", task.getTask_id());
			task.setTask_detail_list(getJdbc().queryForList(getSql("query_task_detail", data)));
		}
		return result;
	}

	/**
	 * 查询我分配的任务
	 * 
	 * @param root_id
	 * @param user_id
	 * @param page
	 * @return
	 */
	public List<TaskVO> queryTaskCreate(String root_id, String user_id, int page) {
		List<TaskVO> result;
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("root_id", root_id);
		data.put("user_id", user_id);
		data.put("start", (page * 20 + 1) + "");
		data.put("end", (page + 1) * 20 + "");
		result = getJdbc().queryForList(getSql("query_task_create", data));
		if (result == null) {
			result = new ArrayList<>();
		}
		for (TaskVO task : result) {
			data.clear();
			data.put("task_id", task.getTask_id());
			task.setTask_detail_list(getJdbc().queryForList(getSql("query_task_detail", data)));
		}
		return result;
	}

	public List<TaskDetailVO> queryTaskProcess(String root_id, String user_id, int page) {
		List<TaskDetailVO> result;
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("root_id", root_id);
		data.put("user_id", user_id);
		data.put("start", (page * 20 + 1) + "");
		data.put("end", (page + 1) * 20 + "");
		result = getJdbc().queryForList(getSql("query_task_process", data));
		if (result == null) {
			result = new ArrayList<>();
		}
		return result;
	}
}