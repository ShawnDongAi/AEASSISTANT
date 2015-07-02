package com.zzn.aenote.http.server.project;

import java.util.HashMap;
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

public class QueryProjectDetail implements CmHandler {
	protected static final Logger logger = Logger
			.getLogger(QueryProjectDetail.class);
	private ProjectService projectService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp,
			BaseRep rs) throws Exception {
		try {
			String project_id = req.getParameter("project_id");
			if (StringUtil.isEmpty(project_id)) {
				rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
				rs.setRES_MESSAGE("当前未加入任何项目");
				return;
			}
			List<Map<String, Object>> projects = projectService
					.queryProjectByID(project_id);
			HashMap<String, Object> result = new HashMap<String, Object>();
			if (projects != null && projects.size() > 0) {
				ProjectVO project = ProjectVO.assembleProject(projects.get(0));
				result.put("project", project);
				result.put("leaf_count",
						projectService.queryLeafCount(project.getPROJECT_ID()));
			}
			rs.setRES_CODE(Global.RESP_SUCCESS);
			rs.setRES_MESSAGE("获取项目详情成功");
			rs.setRES_OBJ(GsonUtil.getInstance().toJson(result));
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
