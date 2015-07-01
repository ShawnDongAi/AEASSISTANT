package com.zzn.aenote.http.server.attendance;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.oreilly.servlet.MultipartRequest;
import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.server.CmHandlerFile;
import com.zzn.aenote.http.service.AttchService;
import com.zzn.aenote.http.service.AttendanceService;
import com.zzn.aenote.http.service.ProjectService;
import com.zzn.aenote.http.service.UserService;
import com.zzn.aenote.http.utils.GsonUtil;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.utils.ToolsUtil;
import com.zzn.aenote.http.utils.UtilConfig;
import com.zzn.aenote.http.utils.UtilUniqueKey;
import com.zzn.aenote.http.vo.AttchVO;
import com.zzn.aenote.http.vo.BaseRep;
import com.zzn.aenote.http.vo.ProjectVO;
import com.zzn.aenote.http.vo.UserVO;

/**
 * 考勤接口
 * 
 * @author Shawn
 *
 */
public class Scanning extends CmHandlerFile {
	protected static final Logger logger = Logger.getLogger(Scanning.class);
	private UserService userService;
	private ProjectService projectService;
	private AttchService attchService;
	private AttendanceService attendanceService;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public String filePath() {
		Date now = new Date(System.currentTimeMillis());
		String today = format.format(now);
		String end = today + "//";
		return UtilConfig.getString("file.head.savePath" + end,
				"D://AENote-file//Server//attendance//" + end);
	}

	@Override
	public void handleFiles(List<String> filePaths, MultipartRequest req,
			BaseRep rs) throws Exception {
		try {
			String phone = req.getParameter("phone");
			String project_id = req.getParameter("project_id");
			String longitude = req.getParameter("longitude");
			String latitude = req.getParameter("latitude");
			String forWho = req.getParameter("for_who");
			String address = req.getParameter("address");
			String parent_user = req.getParameter("parent_user");
			if (StringUtil.isEmpty(parent_user)) {
				logger.info("上级管理员为空");
				rs.setRES_CODE(Global.USER_ID_NULL);
				rs.setRES_MESSAGE("请先登录");
				return;
			}
			if (StringUtil.isEmpty(phone)) {
				logger.info("缺少用户电话号码");
				rs.setRES_CODE(Global.USER_ID_NULL);
				rs.setRES_MESSAGE("请先登录");
				return;
			}
			if (StringUtil.isEmpty(longitude) || StringUtil.isEmpty(latitude)
					|| StringUtil.isEmpty(address)) {
				logger.info("缺少位置信息");
				rs.setRES_CODE(Global.ADDRESS_NULL);
				rs.setRES_MESSAGE("定位失败,请重试");
				return;
			}
			List<Map<String, Object>> users = userService
					.queryUserByPhone(phone);
			String user_id = "";
			String user_name = "";
			if (users == null || users.size() == 0) {
				UserVO userVO = userService.register(phone,
						UtilUniqueKey.getKey());
				if (userVO != null) {
					user_id = userVO.getUSER_ID();
					user_name = userVO.getUSER_NAME();
				}
			} else {
				if (users.get(0).get("user_id") != null) {
					user_id = users.get(0).get("user_id").toString();
					user_name = users.get(0).get("user_name").toString();
				}
			}
			if (StringUtil.isEmpty(user_id)) {
				logger.info("用户信息查询失败或插入新用户失败");
				rs.setRES_CODE(Global.USER_ID_NULL);
				rs.setRES_MESSAGE("用户信息查询失败,请重试");
				return;
			}
			double current_longitude = Double.parseDouble(longitude);
			double current_latitude = Double.parseDouble(latitude);
			Map<String, String> datas = new HashMap<String, String>();
			// 帮人打卡
			if (forWho != null && forWho.equals("1")) {
				project_id = "";
				String parent_id = "";
				String root_id = "";
				String current_root_id = "";
				String current_parent_id = "";
				List<Map<String, Object>> parentUsers = userService
						.queryUserByID(parent_user);
				if (parentUsers == null || parentUsers.size() == 0) {
					logger.info("管理员不存在");
					rs.setRES_CODE(Global.PROJECT_NULL);
					rs.setRES_MESSAGE("该管理员不存在");
					return;
				}
				UserVO parentUser = UserVO.assembleUserVO(parentUsers.get(0));
				List<Map<String, Object>> parentProjects = projectService
						.queryProjectByCreateUser(parentUser.getUSER_ID());
				if (parentProjects != null && parentProjects.size() > 0) {
					for (Map<String, Object> parentProject : parentProjects) {
						double project_longitude = Double
								.parseDouble(parentProject.get("longitude")
										.toString());
						double project_latitude = Double
								.parseDouble(parentProject.get("latitude")
										.toString());
						if (ToolsUtil.getDistance(current_longitude,
								current_latitude, project_longitude,
								project_latitude) < 500) {
							root_id = parentProject.get("root_id").toString();
							parent_id = parentProject.get("project_id")
									.toString();
							break;
						}
					}
				}
				if (StringUtil.isEmpty(root_id)) {
					ProjectVO parentProject = projectService.createProject(
							parentUser.getUSER_NAME(), "", "", "",
							parentUser.getUSER_ID(), address, longitude,
							latitude);
					if (parentProject == null) {
						logger.info("当前未加入任何项目");
						rs.setRES_CODE(Global.PROJECT_NULL);
						rs.setRES_MESSAGE("当前未加入任何项目");
						return;
					}
					root_id = parentProject.getROOT_ID();
					parent_id = parentProject.getPROJECT_ID();
					datas.put("new_project", GsonUtil.getInstance().toJson(parentProject));
				}
				List<Map<String, Object>> projects = projectService
						.queryProjectByCreateUser(user_id);
				if (projects != null && projects.size() > 0) {
					for (Map<String, Object> project : projects) {
						double project_longitude = Double.parseDouble(project
								.get("longitude").toString());
						double project_latitude = Double.parseDouble(project
								.get("latitude").toString());
						if (ToolsUtil.getDistance(current_longitude,
								current_latitude, project_longitude,
								project_latitude) < 500) {
							project_id = project.get("project_id").toString();
							current_parent_id = project.get("parent_id").toString();
							current_root_id = project.get("root_id").toString();
							break;
						}
					}
				}
				if (StringUtil.isEmpty(project_id)) {
					ProjectVO project = projectService.createProject(user_name,
							"", parent_id, root_id, user_id, address,
							longitude, latitude);
					project_id = project.getPARENT_ID();
					current_parent_id = project.getPARENT_ID();
					current_root_id = project.getROOT_ID();
				} else if (!current_root_id.equals(root_id)) {
					logger.info("用户在当前位置已经有项目");
					rs.setRES_CODE(Global.PROJECT_NULL);
					rs.setRES_MESSAGE("该用户在当前位置已加入其他项目，请提示对方删除已加入项目");
					return;
				}
				datas.put("project_id", project_id);
				datas.put("parent_project_id", current_parent_id);
				datas.put("root_project_id", current_root_id);
				datas.put("user_name", user_name);
				datas.put("user_phone", phone);
			} else {
				if (StringUtil.isEmpty(project_id)) {
					ProjectVO project = projectService.createProject(user_name,
							"", "", "", user_id, address, longitude, latitude);
					if (project == null) {
						logger.info("当前未加入任何项目");
						rs.setRES_CODE(Global.PROJECT_NULL);
						rs.setRES_MESSAGE("当前未加入任何项目");
						return;
					}
					project_id = project.getPROJECT_ID();
					datas.put("new_project", GsonUtil.getInstance().toJson(project));
				}
			}
			rs.setRES_OBJ(GsonUtil.getInstance().toJson(datas));

			if (filePaths.size() > 0 && filePaths.get(0) != null) {
				String imgFile = filePaths.get(0).toString();
				AttchVO attch = attchService.insertAttch(
						AttchVO.TYPE_ATTENCHANCE,
						user_id
								+ "_"
								+ format.format(new Date(System
										.currentTimeMillis())), imgFile);
				boolean result = attendanceService.scanning(user_id,
						project_id, attch.getATTCH_ID(), longitude, latitude,
						"0");
				if (result) {
					logger.info("打卡成功");
					rs.setRES_CODE(Global.RESP_SUCCESS);
					rs.setRES_MESSAGE("打卡成功");
				} else {
					logger.info("数据库写入失败");
					rs.setRES_CODE(Global.ORACLE_ERROR);
					rs.setRES_MESSAGE("打卡失败,请重试");
				}
			} else {
				logger.info("文件上传失败");
				rs.setRES_CODE(Global.FILE_UPLOAD_FAILED);
				rs.setRES_MESSAGE("照片上传失败,请重试");
			}
		} catch (Exception e) {
			e.printStackTrace();
			rs.setRES_CODE(Global.RESP_ERROR);
			rs.setRES_MESSAGE("服务器异常,请重试");
		}
	};

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setAttchService(AttchService attchService) {
		this.attchService = attchService;
	}

	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}

	public void setAttendanceService(AttendanceService attendanceService) {
		this.attendanceService = attendanceService;
	}
}
