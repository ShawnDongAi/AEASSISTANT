package com.zzn.aenote.http.server.project;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.server.CmHandler;
import com.zzn.aenote.http.service.ProjectService;
import com.zzn.aenote.http.service.UserService;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.utils.ToolsUtil;
import com.zzn.aenote.http.utils.UtilUniqueKey;
import com.zzn.aenote.http.vo.BaseRep;
import com.zzn.aenote.http.vo.ProjectVO;
import com.zzn.aenote.http.vo.UserVO;

public class JoinProject implements CmHandler {
	protected static final Logger logger = Logger.getLogger(JoinProject.class);
	private ProjectService projectService;
	private UserService userService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp,
			BaseRep rs) throws Exception {
		try {
			String parent_project_id = req.getParameter("parent_project_id");
			String leaf_user_phone = req.getParameter("leaf_user_phone");
			if (StringUtil.isEmpty(parent_project_id)) {
				logger.info("缺少项目信息");
				rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
				rs.setRES_MESSAGE("缺少项目信息");
				return;
			}
			if (StringUtil.isEmpty(leaf_user_phone)) {
				logger.info("缺少用户信息");
				rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
				rs.setRES_MESSAGE("缺少用户信息");
				return;
			}
			// 先取用户
			List<Map<String, Object>> users = userService
					.queryUserByPhone(leaf_user_phone);
			String leaf_user_id = "";
			String leaf_user_name = "";
			if (users == null || users.size() == 0) {
				UserVO userVO = userService.register(leaf_user_phone,
						UtilUniqueKey.getKey());
				if (userVO != null) {
					leaf_user_id = userVO.getUSER_ID();
					leaf_user_name = userVO.getUSER_NAME();
				}
			} else {
				if (users.get(0).get("user_id") != null) {
					leaf_user_id = users.get(0).get("user_id").toString();
					leaf_user_name = users.get(0).get("user_name").toString();
				}
			}
			if (StringUtil.isEmpty(leaf_user_name)) {
				logger.info("用户信息查询失败或插入新用户失败");
				rs.setRES_CODE(Global.USER_ID_NULL);
				rs.setRES_MESSAGE("用户信息查询失败,请重试");
				return;
			}

			List<Map<String, Object>> parentProjects = projectService
					.queryProjectByID(parent_project_id);
			ProjectVO parentProjectVO = null;
			if (parentProjects != null && parentProjects.size() > 0) {
				parentProjectVO = ProjectVO.assembleProject(parentProjects
						.get(0));
			}
			if (parentProjectVO == null) {
				logger.info("缺少项目信息");
				rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
				rs.setRES_MESSAGE("缺少项目信息");
				return;
			}
			double longitude = Double.parseDouble(parentProjectVO
					.getLONGITUDE());
			double latitude = Double.parseDouble(parentProjectVO.getLATITUDE());
			ProjectVO leafProjectVO = null;
			List<Map<String, Object>> leafProjects = projectService
					.queryProjectByCreateUser(leaf_user_id);
			if (leafProjects != null && leafProjects.size() > 0) {
				for (Map<String, Object> projectMap : leafProjects) {
					ProjectVO project = ProjectVO.assembleProject(projectMap);
					if (project.getROOT_ID().equals(
							parentProjectVO.getROOT_ID())) {
						leafProjectVO = project;
						break;
					}
					double current_longitude = Double.parseDouble(project
							.getLONGITUDE());
					double current_latitude = Double.parseDouble(project
							.getLATITUDE());
					if (ToolsUtil.getDistance(current_longitude,
							current_latitude, longitude, latitude) < 500) {
						if (project.getROOT_ID()
								.equals(project.getPROJECT_ID())) {
							leafProjectVO = project;
							break;
						}
						logger.info("用户在当前位置已经有项目");
						rs.setRES_CODE(Global.PROJECT_NULL);
						rs.setRES_MESSAGE("该用户在当前位置已加入其他项目，请提示对方删除当前所属项目");
						return;
					}
				}
			}
			if (leafProjectVO == null) {
				leafProjectVO = projectService.createProject(leaf_user_name+"的项目",
						"", parentProjectVO.getPROJECT_ID(),
						parentProjectVO.getROOT_ID(), leaf_user_id,
						parentProjectVO.getADDRESS(),
						parentProjectVO.getLONGITUDE(),
						parentProjectVO.getLATITUDE());
				if (leafProjectVO == null) {
					logger.info("创建子项目失败");
					rs.setRES_CODE(Global.PROJECT_NULL);
					rs.setRES_MESSAGE("创建子项目失败");
					return;
				} else {
					rs.setRES_CODE(Global.RESP_SUCCESS);
					rs.setRES_MESSAGE("迁移成功");
				}
			}
			if (isProjectsParent(leafProjectVO, parentProjectVO)) {
				rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
				rs.setRES_MESSAGE("该用户为您的上级用户,无法进行项目迁移");
				return;
			}
			if (leafProjectVO.getROOT_ID()
					.equals(leafProjectVO.getPROJECT_ID())) {
				if (!projectService.updateRootProject(
						leafProjectVO.getPROJECT_ID(),
						parentProjectVO.getROOT_ID())) {
					rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
					rs.setRES_MESSAGE("迁移失败");
					return;
				}
			}
			boolean result = projectService.updateParentProject(
					leafProjectVO.getPROJECT_ID(), parent_project_id,
					parentProjectVO.getROOT_ID());
			if (result) {
				rs.setRES_CODE(Global.RESP_SUCCESS);
				rs.setRES_MESSAGE("迁移成功");
			} else {
				rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
				rs.setRES_MESSAGE("迁移失败");
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

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	private boolean isProjectsParent(ProjectVO projectVO,
			ProjectVO parentProjectVO) {
		boolean result = false;
		if (!parentProjectVO.getPROJECT_ID().equals(
				parentProjectVO.getROOT_ID())) {
			if (parentProjectVO.getPARENT_ID()
					.equals(projectVO.getPROJECT_ID())) {
				result = false;
				return result;
			}
			List<Map<String, Object>> parentProjects = projectService
					.queryProjectByID(parentProjectVO.getPARENT_ID());
			if (parentProjects != null && parentProjects.size() > 0) {
				parentProjectVO = ProjectVO.assembleProject(parentProjects
						.get(0));
				result = isProjectsParent(projectVO, parentProjectVO);
			}
		}
		return result;
	}
}
