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

public class CreateProject implements CmHandler {
	protected static final Logger logger = Logger
			.getLogger(CreateProject.class);
	private ProjectService projectService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp,
			BaseRep rs) throws Exception {
		try {
			String user_id = req.getParameter("user_id");
			String project_name = req.getParameter("project_name");
			String address = req.getParameter("address");
			String longitude = req.getParameter("longitude");
			String latitude = req.getParameter("latitude");
			if (StringUtil.isEmpty(user_id)) {
				rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
				rs.setRES_MESSAGE("请先登录");
				return;
			}
			if (StringUtil.isEmpty(project_name)) {
				rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
				rs.setRES_MESSAGE("请输入项目名称");
				return;
			}
			if (StringUtil.isEmpty(address) || StringUtil.isEmpty(longitude)
					|| StringUtil.isEmpty(latitude)) {
				rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
				rs.setRES_MESSAGE("请选择项目位置");
				return;
			}
			List<Map<String, Object>> exitsProjects = projectService
					.queryProjectByCreateUser(user_id);
			if (exitsProjects != null && exitsProjects.size() > 0) {
				double current_longitude = Double.parseDouble(longitude);
				double current_latitude = Double.parseDouble(latitude);
				for (Map<String, Object> project : exitsProjects) {
					double project_longitude = Double
							.parseDouble(project.get("longitude")
									.toString());
					double project_latitude = Double
							.parseDouble(project.get("latitude")
									.toString());
					if (ToolsUtil.getDistance(current_longitude,
							current_latitude, project_longitude,
							project_latitude) < 500) {
						rs.setRES_CODE(Global.PROJECT_CREATE_FAILED);
						rs.setRES_MESSAGE("您在该位置已有项目,请删除该项目或更改项目地址后重试");
						return;
					}
				}
			}
			ProjectVO result = projectService.createProject(project_name, "", "",
					"", user_id, address, longitude, latitude);
			if (result != null) {
				rs.setRES_CODE(Global.RESP_SUCCESS);
				rs.setRES_MESSAGE("创建项目成功");
				rs.setRES_OBJ(GsonUtil.getInstance().toJson(result, ProjectVO.class));
				return;
			} else {
				rs.setRES_CODE(Global.PROJECT_CREATE_FAILED);
				rs.setRES_MESSAGE("创建项目失败,请重试");
				return;
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
