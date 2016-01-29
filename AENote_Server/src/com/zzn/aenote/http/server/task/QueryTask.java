package com.zzn.aenote.http.server.task;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.server.CmHandler;
import com.zzn.aenote.http.service.TaskService;
import com.zzn.aenote.http.utils.GsonUtil;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.vo.BaseRep;
import com.zzn.aenote.http.vo.TaskVO;

public class QueryTask implements CmHandler {
	protected static final Logger logger = Logger.getLogger(QueryTask.class);
	private TaskService taskService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp, BaseRep rs) throws Exception {
		try {
			String pageString = req.getParameter("page");
			String root_id = req.getParameter("root_id");
			String user_id = req.getParameter("user_id");
			if (StringUtil.isEmpty(root_id)) {
				rs.setRES_CODE(Global.RESP_PARAM_NULL);
				rs.setRES_MESSAGE("缺少参数");
				return;
			}
			int page = 0;
			try {
				page = Integer.parseInt(pageString);
			} catch (Exception e) {
				page = 0;
			}
			List<TaskVO> result;
			if (StringUtil.isEmpty(user_id)) {
				result = taskService.queryTaskAll(root_id, page);
			} else {
				result = taskService.queryTaskCreate(root_id, user_id, page);
			}
			rs.setRES_CODE(Global.RESP_SUCCESS);
			rs.setRES_OBJ(GsonUtil.getInstance().toJson(result));
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