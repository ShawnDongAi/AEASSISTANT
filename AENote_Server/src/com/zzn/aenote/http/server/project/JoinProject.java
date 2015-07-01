package com.zzn.aenote.http.server.project;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.server.CmHandler;
import com.zzn.aenote.http.service.ProjectService;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.vo.BaseRep;

public class JoinProject implements CmHandler {
	protected static final Logger logger = Logger.getLogger(JoinProject.class);
	private ProjectService projectService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp,
			BaseRep rs) throws Exception {
		try {
			String parent_project_id = req.getParameter("parent_project_id");
			String project_id = req.getParameter("project_id");
			String root_project_id = req.getParameter("root_project_id");
			if (StringUtil.isEmpty(parent_project_id) || StringUtil.isEmpty(project_id) || StringUtil.isEmpty(root_project_id)) {
				logger.info("缺少项目信息");
				rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
				rs.setRES_MESSAGE("缺少项目信息");
				return;
			}
			boolean result = projectService.updateParentProject(project_id, parent_project_id, root_project_id);
			if (result) {
				rs.setRES_CODE(Global.RESP_SUCCESS);
				rs.setRES_MESSAGE("添加成功");
			} else {
				rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
				rs.setRES_MESSAGE("添加失败");
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
