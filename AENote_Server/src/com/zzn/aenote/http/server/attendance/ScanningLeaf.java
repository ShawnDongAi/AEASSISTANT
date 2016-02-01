package com.zzn.aenote.http.server.attendance;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.server.CmHandler;
import com.zzn.aenote.http.service.AttendanceService;
import com.zzn.aenote.http.service.ProjectService;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.vo.BaseRep;
import com.zzn.aenote.http.vo.ProjectVO;

public class ScanningLeaf implements CmHandler {
	private ProjectService projectService;
	private AttendanceService attendanceService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp, BaseRep rs) throws Exception {
		try {
			String projectIds = req.getParameter("project_ids");
			if (projectIds == null || StringUtil.isEmpty(projectIds)) {
				rs.setRES_CODE(Global.RESP_PARAM_NULL);
				rs.setRES_MESSAGE("请选择需要点工的下级项目");
				return;
			}
			String[] projectIdArray = projectIds.split(",");
			StringBuilder failedProject = new StringBuilder();
			for (String id : projectIdArray) {
				if (StringUtil.isEmpty(id)) {
					continue;
				}
				List<Map<String, Object>> projectList = projectService.queryProjectByID(id);
				if (projectList != null && projectList.size() > 0) {
					ProjectVO project = ProjectVO.assembleProject(projectList.get(0));
					boolean result = false;
					if (attendanceService.isScanningToday(id)) {
						if (attendanceService.isScanningTodayVaild(id)) {
							result = attendanceService.scanning(project.getCREATE_USER(), id, project.getPARENT_ID(),
									project.getROOT_ID(), project.getCREATE_USER_HEAD(), project.getADDRESS(),
									project.getLONGITUDE(), project.getLATITUDE(), "2", "");
						} else {
							result = attendanceService.updateScanning(project.getCREATE_USER(), id,
									project.getPARENT_ID(), project.getROOT_ID(), project.getCREATE_USER_HEAD(),
									project.getADDRESS(), project.getLONGITUDE(), project.getLATITUDE(), "2", "");
						}
					} else {
						if (!id.equals(project.getPARENT_ID())) {
							scanningParent(project.getPARENT_ID(), project.getADDRESS(), project.getLONGITUDE(),
									project.getLATITUDE());
						}
						result = attendanceService.scanning(project.getCREATE_USER(), id, project.getPARENT_ID(),
								project.getROOT_ID(), project.getCREATE_USER_HEAD(), project.getADDRESS(),
								project.getLONGITUDE(), project.getLATITUDE(), "2", "");
					}
					if (!result) {
						failedProject.append(project.getPROJECT_NAME() + ",");
					}
				}
			}
			if (failedProject.length() > 0) {
				failedProject.deleteCharAt(failedProject.length() - 1);
				rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
				rs.setRES_MESSAGE(failedProject + "签到失败");
			} else {
				rs.setRES_CODE(Global.RESP_SUCCESS);
				rs.setRES_MESSAGE("签到成功");
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