package com.zzn.aenote.http.server.task;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.reflect.TypeToken;
import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.server.CmHandler;
import com.zzn.aenote.http.service.TaskService;
import com.zzn.aenote.http.utils.GsonUtil;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.vo.BaseRep;
import com.zzn.aenote.http.vo.TaskDetailVO;

public class CreateTask implements CmHandler {
	protected static final Logger logger = Logger.getLogger(CreateTask.class);
	private TaskService taskService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp, BaseRep rs) throws Exception {
		try {
			String create_user_id = req.getParameter("create_user_id").trim();
			String create_project_id = req.getParameter("create_project_id").trim();
			String root_id = req.getParameter("root_id").trim();
			String taskDetailString = req.getParameter("taskDetails").trim();
			if (StringUtil.isEmpty(create_user_id) || StringUtil.isEmpty(create_project_id)
					|| StringUtil.isEmpty(root_id)) {
				rs.setRES_CODE(Global.RESP_PARAM_NULL);
				rs.setRES_MESSAGE("缺少参数");
				return;
			}
			List<TaskDetailVO> taskDetails = GsonUtil.getInstance().fromJson(taskDetailString,
					new TypeToken<List<TaskDetailVO>>() {
					}.getType());
			if (taskDetails == null || taskDetails.size() == 0) {
				rs.setRES_CODE(Global.RESP_PARAM_NULL);
				rs.setRES_MESSAGE("未创建任何任务");
				return;
			}
			boolean result = false;
			result = taskService.insertTask(create_user_id, create_project_id, root_id, taskDetails);
			if (result) {
				rs.setRES_CODE(Global.RESP_SUCCESS);
				rs.setRES_MESSAGE("新建任务成功");
			} else {
				rs.setRES_CODE(Global.ORACLE_ERROR);
				rs.setRES_MESSAGE("新建任务失败，请重试");
			}
		} catch (Exception e) {
			e.printStackTrace();
			rs.setRES_CODE(Global.RESP_ERROR);
			rs.setRES_MESSAGE("服务器异常,请重试");
		}
	}

	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}
}