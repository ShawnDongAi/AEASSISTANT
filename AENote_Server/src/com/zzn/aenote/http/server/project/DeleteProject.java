package com.zzn.aenote.http.server.project;

import java.util.ArrayList;
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
import com.zzn.aenote.http.vo.BaseRep;
import com.zzn.aenote.http.vo.ProjectVO;

public class DeleteProject implements CmHandler {
	protected static final Logger logger = Logger.getLogger(DeleteProject.class);
	private ProjectService projectService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp,
			BaseRep rs) throws Exception {
		try {
			String project_id = req.getParameter("project_id");
			String user_id = req.getParameter("user_id");
			if (StringUtil.isEmpty(user_id)) {
				rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
				rs.setRES_MESSAGE("请先登录");
				return;
			}
			if (StringUtil.isEmpty(project_id)) {
				logger.info("缺少项目信息");
				rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
				rs.setRES_MESSAGE("缺少项目信息");
				return;
			}
			boolean result = projectService.deleteProject(project_id);
			if (!result) {
				rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
				rs.setRES_MESSAGE("删除失败");
			}
			List<ProjectVO> resultList = new ArrayList<ProjectVO>();
			List<Map<String, Object>> createProjects = projectService
					.queryProjectByCreateUser(user_id);
			if (createProjects != null && createProjects.size() > 0) {
				for (Map<String, Object> project : createProjects) {
					resultList.add(ProjectVO.assembleProject(project));
				}
			}
			rs.setRES_CODE(Global.RESP_SUCCESS);
			rs.setRES_MESSAGE("获取项目列表成功");
			rs.setRES_OBJ(GsonUtil.getInstance().toJson(resultList));
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
