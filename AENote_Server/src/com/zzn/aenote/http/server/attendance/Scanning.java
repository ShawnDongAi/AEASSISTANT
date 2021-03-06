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
		return UtilConfig.getString("file.head.savePath" + end, "D://AENote-file//Server//attendance//" + end);
	}

	@Override
	public void handleFiles(List<String> filePaths, MultipartRequest req, BaseRep rs) throws Exception {
		try {
			String phone = req.getParameter("phone");
			String project_id = req.getParameter("project_id");
			String longitude = req.getParameter("longitude");
			String latitude = req.getParameter("latitude");
			String forWho = req.getParameter("for_who");
			String address = req.getParameter("address");
			String parent_user = req.getParameter("parent_user");
			String is_out_scannng = "0";
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
			if (StringUtil.isEmpty(longitude) || StringUtil.isEmpty(latitude) || StringUtil.isEmpty(address)) {
				logger.info("缺少位置信息");
				rs.setRES_CODE(Global.ADDRESS_NULL);
				rs.setRES_MESSAGE("定位失败,请重试");
				return;
			}
			if (StringUtil.isEmpty(project_id)) {
				logger.info("当前位置无任何项目");
				rs.setRES_CODE(Global.ADDRESS_NULL);
				rs.setRES_MESSAGE("当前位置无任何项目");
				return;
			}
			List<Map<String, Object>> users = userService.queryUserByPhone(phone);
			String user_id = "";
			String user_name = "";
			// 未注册用户打卡自动为其注册
			if (users == null || users.size() == 0) {
				UserVO userVO = userService.register(phone, UtilUniqueKey.getKey());
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
			String parent_id = "";
			String root_id = "";
			String root_name = "";
			// 帮人打卡
			if (forWho != null && forWho.equals("1")) {
				String current_root_id = "";
				String project_name = "";
				// 获取管理员所在项目
				ProjectVO parentProject = null;
				List<Map<String, Object>> parentProjects = projectService.queryProjectByID(project_id);
				if (parentProjects != null && parentProjects.size() > 0) {
					parentProject = ProjectVO.assembleProject(parentProjects.get(0));
				}
				if (parentProject == null) {
					logger.info("当前未加入任何项目");
					rs.setRES_CODE(Global.PROJECT_NULL);
					rs.setRES_MESSAGE("当前未加入任何项目");
					return;
				}
				logger.info("当前管理项目===>" + parentProject.getPROJECT_ID());
				parent_id = parentProject.getPARENT_ID();
				root_id = parentProject.getROOT_ID();
				root_name = parentProject.getROOT_PROJECT_NAME();
				double parent_longitude = Double.parseDouble(parentProject.getLONGITUDE());
				double parent_latitude = Double.parseDouble(parentProject.getLATITUDE());
				// 项目位置内为正常考勤,否则为外出考勤
				if (ToolsUtil.getDistance(current_longitude, current_latitude, parent_longitude,
						parent_latitude) < 500) {
					is_out_scannng = "0";
				} else {
					is_out_scannng = "1";
				}

				// 获取该考勤下级所有项目,判断是否与当前项目位置范围有重叠,重叠则不允许打卡
				List<Map<String, Object>> projects = projectService.queryProjectByCreateUser(user_id);
				if (projects != null && projects.size() > 0) {
					for (Map<String, Object> project : projects) {
						double project_longitude = Double.parseDouble(project.get("longitude").toString());
						double project_latitude = Double.parseDouble(project.get("latitude").toString());
						if (ToolsUtil.getDistance(current_longitude, current_latitude, project_longitude,
								project_latitude) < 500) {
							project_id = project.get("project_id").toString();
							current_root_id = project.get("root_id").toString();
							project_name = project.get("project_name").toString();
							logger.info("用户在当前位置已经有项目==>" + project.get("project_name").toString());
							break;
						}
					}
				}
				if (StringUtil.isEmpty(current_root_id)) {
					ProjectVO projectVO = projectService.createProject(user_name + "的项目", "", parent_id, root_id,
							user_id, address, longitude, latitude, root_name);
					project_id = projectVO.getPROJECT_ID();
					project_name = projectVO.getPROJECT_NAME();
				} else if (!current_root_id.equals(root_id)) {
					rs.setRES_CODE(Global.PROJECT_NULL);
					rs.setRES_MESSAGE(user_name + "在当前位置已加入其他项目，请提示他进行项目迁移");
					return;
				}
				datas.put("user_name", project_name);
				datas.put("user_phone", phone);
			} else {
				ProjectVO project = null;
				List<Map<String, Object>> projects = projectService.queryProjectByID(project_id);
				if (projects != null && projects.size() > 0) {
					project = ProjectVO.assembleProject(projects.get(0));
				}
				if (project == null) {
					logger.info("当前未加入任何项目");
					rs.setRES_CODE(Global.PROJECT_NULL);
					rs.setRES_MESSAGE("当前未加入任何项目");
					return;
				}
				project_id = project.getPROJECT_ID();
				parent_id = project.getPARENT_ID();
				root_id = project.getROOT_ID();
				double project_longitude = Double.parseDouble(project.getLONGITUDE());
				double project_latitude = Double.parseDouble(project.getLATITUDE());
				// 项目位置内为正常考勤,否则为外出考勤
				if (ToolsUtil.getDistance(current_longitude, current_latitude, project_longitude,
						project_latitude) < 500) {
					is_out_scannng = "0";
				} else {
					is_out_scannng = "1";
				}
			}
			rs.setRES_OBJ(GsonUtil.getInstance().toJson(datas));

			if (filePaths.size() > 0 && filePaths.get(0) != null) {
				String imgFile = filePaths.get(0).toString();
				AttchVO attch = attchService.insertAttch(AttchVO.TYPE_ATTENCHANCE,
						user_id + "_" + format.format(new Date(System.currentTimeMillis())), imgFile);
				boolean result = false;
				if (attendanceService.isScanningToday(project_id)) {
					logger.info("今天有打卡数据");
					if (attendanceService.isScanningTodayVaild(project_id)) {
						result = attendanceService.scanning(user_id, project_id, parent_id, root_id,
								attch.getATTCH_ID(), address, longitude, latitude, is_out_scannng, imgFile);
					} else {
						result = attendanceService.updateScanning(user_id, project_id, parent_id, root_id,
								attch.getATTCH_ID(), address, longitude, latitude, is_out_scannng, imgFile);
					}
				} else {
					if (!project_id.equals(parent_id)) {
						scanningParent(parent_id, address, longitude, latitude);
					}
					result = attendanceService.scanning(user_id, project_id, parent_id, root_id, attch.getATTCH_ID(),
							address, longitude, latitude, is_out_scannng, imgFile);
				}
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

	// 如果上级还未进行考勤，则自动录入一条无效考勤记录，方便后续按项目组织架构查询统计(防止中间出现组织架构变动而查询不到之前的考勤数据)
	public void scanningParent(String project_id, String address, String longitude, String latitude) {
		List<Map<String, Object>> projects = projectService.queryProjectByID(project_id);
		boolean lastOne = false;
		if (projects != null && projects.size() > 0) {
			ProjectVO project = ProjectVO.assembleProject(projects.get(0));
			if (project.getPARENT_ID().equals(project.getPROJECT_ID())) {
				lastOne = true;
			}
			boolean isScanning = attendanceService.isScanningToday(project_id);
			if (!isScanning) {
				attendanceService.scanning(project.getCREATE_USER(), project_id, project.getPARENT_ID(),
						project.getROOT_ID(), "00000000000000000000000000000000", address, longitude, latitude, "0",
						"");
				if (!lastOne) {
					scanningParent(project.getPARENT_ID(), address, longitude, latitude);
				}
			}
		}
	}
}
