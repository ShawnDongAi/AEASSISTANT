package com.zzn.aenote.http.server.task;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.server.CmHandler;
import com.zzn.aenote.http.service.TaskService;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.vo.BaseRep;

public class ProcessTask implements CmHandler {
	protected static final Logger logger = Logger.getLogger(ProcessTask.class);
	private TaskService taskService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp, BaseRep rs) throws Exception {
		try {
			String task_detail_id = req.getParameter("task_detail_id").trim();
			String status = req.getParameter("status").trim();
			String process_content = req.getParameter("process_content").trim();
			String process_attch_id = req.getParameter("process_attch_id").trim();
			if (StringUtil.isEmpty(task_detail_id) || StringUtil.isEmpty(status)
					|| StringUtil.isEmpty(process_content)) {
				rs.setRES_CODE(Global.RESP_PARAM_NULL);
				rs.setRES_MESSAGE("缺少参数");
				return;
			}
			boolean result = false;
			result = taskService.processTask(task_detail_id, status, process_content, process_attch_id);
			if (result) {
				rs.setRES_CODE(Global.RESP_SUCCESS);
				rs.setRES_MESSAGE("处理任务成功");
			} else {
				rs.setRES_CODE(Global.ORACLE_ERROR);
				rs.setRES_MESSAGE("处理任务失败，请重试");
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