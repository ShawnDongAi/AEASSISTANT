package com.zzn.aenote.http.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.BaseService;
import com.zzn.aenote.http.utils.UtilUniqueKey;
import com.zzn.aenote.http.vo.TaskDetailVO;

/**
 * 任务相关信息操作类
 * 
 * @author Shawn
 */
public class TaskService extends BaseService {
	private static final Logger logger = Logger.getLogger(TaskService.class);
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 插入一条新任务
	 * @param create_user_id
	 * @param create_project_id
	 * @param root_id
	 * @param taskDetails
	 * @return
	 */
	public boolean insertTask(String create_user_id, String create_project_id, String root_id, List<TaskDetailVO> taskDetails) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			String task_id = UtilUniqueKey.getKey("task" + System.currentTimeMillis());
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
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}