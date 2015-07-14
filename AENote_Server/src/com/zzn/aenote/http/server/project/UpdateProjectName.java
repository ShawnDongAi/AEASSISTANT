package com.zzn.aenote.http.server.project;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.server.CmHandler;
import com.zzn.aenote.http.service.ProjectService;
import com.zzn.aenote.http.utils.GsonUtil;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.utils.ToolsUtil;
import com.zzn.aenote.http.vo.BaseRep;
import com.zzn.aenote.http.vo.ProjectVO;

public class UpdateProjectName implements CmHandler {
	protected static final Logger logger = Logger
			.getLogger(UpdateProjectName.class);
	private ProjectService projectService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp,
			BaseRep rs) throws Exception {
		try {
			String project_id = req.getParameter("project_id");
			String project_name = req.getParameter("project_name");
			if (StringUtil.isEmpty(project_id) || StringUtil.isEmpty(project_name)) {
				rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
				rs.setRES_MESSAGE("项目信息有误");
				return;
			}
			boolean result = projectService.updateProjectName(project_id, project_name);
			if (result) {
				rs.setRES_CODE(Global.RESP_SUCCESS);
				rs.setRES_MESSAGE("修改项目名成功");
				logger.info("修改项目名成功");
			} else {
				rs.setRES_CODE(Global.USER_HEAD_FAILED);
				rs.setRES_MESSAGE("修改项目名失败");
				logger.info("修改项目名失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			rs.setRES_CODE(Global.RESP_ERROR);
			rs.setRES_MESSAGE("服务器异常,请重试");
		}
	}

	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}
}
