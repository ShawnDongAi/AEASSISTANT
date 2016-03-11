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

public class DeleteTask implements CmHandler {
	protected static final Logger logger = Logger.getLogger(DeleteTask.class);
	private TaskService taskService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp, BaseRep rs) throws Exception {
		try {
			String task_id = req.getParameter("task_id");
			String task_detail_id = req.getParameter("task_detail_id");
			if (StringUtil.isEmpty(task_id) || StringUtil.isEmpty(task_detail_id)) {
				rs.setRES_CODE(Global.RESP_PARAM_NULL);
				rs.setRES_MESSAGE("缺少参数");
				return;
			}
			taskService.deleteTaskByDetail(task_id, task_detail_id);
			rs.setRES_CODE(Global.RESP_SUCCESS);
			rs.setRES_MESSAGE("删除任务成功");
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