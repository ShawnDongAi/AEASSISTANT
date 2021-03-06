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
import com.zzn.aenote.http.utils.RegexUtil;
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
			String parent_project_id = StringUtil.nullToString(req
					.getParameter("parent_project_id"));
			String leaf_user_phone = StringUtil.nullToString(req
					.getParameter("leaf_user_phone"));
			String leaf_user_name = StringUtil.nullToString(req
					.getParameter("leaf_user_name"));
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
			String[] leafPhones = leaf_user_phone.split(",");
			String[] leafNames = leaf_user_name.split(",");
			if (leafPhones.length > 1) {
				StringBuilder failedPhone = new StringBuilder();
				for (int i = 0; i < leafPhones.length; i++) {
					String leafPhone = RegexUtil.formatPhoneNum(leafPhones[i]);
					logger.info("join project===>" + leafPhone);
					if (StringUtil.isEmpty(leafPhone)) {
						continue;
					}
					// 先取用户
					List<Map<String, Object>> users = userService
							.queryUserByPhone(leafPhone);
					String leaf_user_id = "";
					String leafName = leafPhone;
					if (leafNames.length > i
							|| !StringUtil.isEmpty(leafNames[i])) {
						leafName = leafNames[i];
					}
					if (users == null || users.size() == 0) {
						UserVO userVO = userService.register(leafPhone,
								leafName, UtilUniqueKey.getKey());
						if (userVO != null) {
							leaf_user_id = userVO.getUSER_ID();
							leafName = userVO.getUSER_NAME();
						}
					} else {
						if (users.get(0).get("user_id") != null) {
							leaf_user_id = users.get(0).get("user_id")
									.toString();
							leafName = users.get(0).get("user_name").toString();
						}
					}
					if (StringUtil.isEmpty(leaf_user_id)) {
						logger.info("用户信息查询失败或插入新用户失败");
						failedPhone.append(leafPhone + ",");
						continue;
					}
					List<Map<String, Object>> parentProjects = projectService
							.queryProjectByID(parent_project_id);
					ProjectVO parentProjectVO = null;
					if (parentProjects != null && parentProjects.size() > 0) {
						parentProjectVO = ProjectVO
								.assembleProject(parentProjects.get(0));
					}
					if (parentProjectVO == null) {
						logger.info("缺少项目信息");
						failedPhone.append(leafPhone + ",");
						continue;
					}
					double longitude = Double.parseDouble(parentProjectVO
							.getLONGITUDE());
					double latitude = Double.parseDouble(parentProjectVO
							.getLATITUDE());
					ProjectVO leafProjectVO = null;
					List<Map<String, Object>> leafProjects = projectService
							.queryProjectByCreateUser(leaf_user_id);
					if (leafProjects != null && leafProjects.size() > 0) {
						for (Map<String, Object> projectMap : leafProjects) {
							ProjectVO project = ProjectVO
									.assembleProject(projectMap);
							if (project.getROOT_ID().equals(
									parentProjectVO.getROOT_ID())) {
								leafProjectVO = project;
								break;
							}
							double current_longitude = Double
									.parseDouble(project.getLONGITUDE());
							double current_latitude = Double
									.parseDouble(project.getLATITUDE());
							if (ToolsUtil.getDistance(current_longitude,
									current_latitude, longitude, latitude) < 500) {
								if (project.getROOT_ID().equals(
										project.getPROJECT_ID())) {
									leafProjectVO = project;
									break;
								}
								logger.info("用户在当前位置已经有项目");
								failedPhone.append(leafPhone + ",");
								continue;
							}
						}
					}
					if (leafProjectVO == null) {
						leafProjectVO = projectService.createProject(leafName
								+ "的项目", "", parentProjectVO.getPROJECT_ID(),
								parentProjectVO.getROOT_ID(), leaf_user_id,
								parentProjectVO.getADDRESS(),
								parentProjectVO.getLONGITUDE(),
								parentProjectVO.getLATITUDE(),
								parentProjectVO.getROOT_PROJECT_NAME());
						if (leafProjectVO == null) {
							failedPhone.append(leafPhone + ",");
						}
						continue;
					}
					if (isProjectsParent(leafProjectVO, parentProjectVO)) {
						failedPhone.append(leafPhone + ",");
						continue;
					}
					if (leafProjectVO.getROOT_ID().equals(
							leafProjectVO.getPROJECT_ID())) {
						if (!projectService.updateRootProject(
								leafProjectVO.getPROJECT_ID(),
								parentProjectVO.getROOT_ID())) {
							failedPhone.append(leafPhone + ",");
							continue;
						}
					}
					boolean result = projectService.updateParentProject(
							leafProjectVO.getPROJECT_ID(),
							parentProjectVO.getPROJECT_ID(),
							parentProjectVO.getROOT_ID());
					if (!result) {
						failedPhone.append(leafPhone + ",");
					}
				}
				if (failedPhone.length() > 0) {
					failedPhone.deleteCharAt(failedPhone.length() - 1);
					rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
					rs.setRES_MESSAGE(failedPhone + "导入失败");
				} else {
					rs.setRES_CODE(Global.RESP_SUCCESS);
					rs.setRES_MESSAGE("迁移成功");
				}
			} else {
				leaf_user_phone = RegexUtil.formatPhoneNum(leaf_user_phone);
				// 先取用户
				List<Map<String, Object>> users = userService
						.queryUserByPhone(leaf_user_phone);
				String leaf_user_id = "";
				logger.info("join project===>" + leaf_user_phone);
				String leafName = leaf_user_phone;
				if (!StringUtil.isEmpty(leaf_user_name)) {
					leafName = leaf_user_name;
				}
				if (users == null || users.size() == 0) {
					UserVO userVO = userService.register(leaf_user_phone,
							leafName, UtilUniqueKey.getKey());
					if (userVO != null) {
						leaf_user_id = userVO.getUSER_ID();
						leafName = userVO.getUSER_NAME();
					}
				} else {
					if (users.get(0).get("user_id") != null) {
						leaf_user_id = users.get(0).get("user_id").toString();
						leafName = users.get(0).get("user_name").toString();
					}
				}
				if (StringUtil.isEmpty(leaf_user_id)) {
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
				double latitude = Double.parseDouble(parentProjectVO
						.getLATITUDE());
				ProjectVO leafProjectVO = null;
				List<Map<String, Object>> leafProjects = projectService
						.queryProjectByCreateUser(leaf_user_id);
				if (leafProjects != null && leafProjects.size() > 0) {
					for (Map<String, Object> projectMap : leafProjects) {
						ProjectVO project = ProjectVO
								.assembleProject(projectMap);
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
							if (project.getROOT_ID().equals(
									project.getPROJECT_ID())) {
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
					leafProjectVO = projectService.createProject(leafName
							+ "的项目", "", parentProjectVO.getPROJECT_ID(),
							parentProjectVO.getROOT_ID(), leaf_user_id,
							parentProjectVO.getADDRESS(),
							parentProjectVO.getLONGITUDE(),
							parentProjectVO.getLATITUDE(),
							parentProjectVO.getROOT_PROJECT_NAME());
					if (leafProjectVO == null) {
						logger.info("创建子项目失败");
						rs.setRES_CODE(Global.PROJECT_NULL);
						rs.setRES_MESSAGE("创建子项目失败");
						return;
					}
					rs.setRES_CODE(Global.RESP_SUCCESS);
					rs.setRES_MESSAGE("添加下级成功");
					return;
				}
				if (isProjectsParent(leafProjectVO, parentProjectVO)) {
					rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
					rs.setRES_MESSAGE("该用户为您的上级用户,无法进行项目迁移");
					return;
				}
				if (leafProjectVO.getROOT_ID().equals(
						leafProjectVO.getPROJECT_ID())) {
					if (!projectService.updateRootProject(
							leafProjectVO.getPROJECT_ID(),
							parentProjectVO.getROOT_ID())) {
						rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
						rs.setRES_MESSAGE("迁移失败");
						return;
					}
				}
				boolean result = projectService.updateParentProject(
						leafProjectVO.getPROJECT_ID(),
						parentProjectVO.getPROJECT_ID(),
						parentProjectVO.getROOT_ID());
				if (result) {
					rs.setRES_CODE(Global.RESP_SUCCESS);
					rs.setRES_MESSAGE("迁移成功");
				} else {
					rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
					rs.setRES_MESSAGE("迁移失败");
				}
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
		logger.info("projectVO==>" + projectVO.getPROJECT_NAME()
				+ ",parentProjectVO==>" + parentProjectVO.getPROJECT_NAME());
		if (!parentProjectVO.getPROJECT_ID().equals(
				parentProjectVO.getROOT_ID())) {
			if (parentProjectVO.getPARENT_ID()
					.equals(projectVO.getPROJECT_ID())) {
				return true;
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