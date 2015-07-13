package com.zzn.aenote.http.server.attendance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.server.CmHandler;
import com.zzn.aenote.http.service.AttendanceService;
import com.zzn.aenote.http.utils.GsonUtil;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.vo.BaseRep;
import com.zzn.aenote.http.vo.ProAttendanceVO;

public class SumCountByProject implements CmHandler {
	protected static final Logger logger = Logger
			.getLogger(SumCountByProject.class);
	private AttendanceService attendanceService;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp,
			BaseRep rs) throws Exception {
		try {
			String startDate = req.getParameter("start_date");
			String endDate = req.getParameter("end_date");
			String projectID = req.getParameter("project_id");
			if (StringUtil.isEmpty(startDate) || StringUtil.isEmpty(endDate)
					|| StringUtil.isEmpty(projectID)) {
				logger.info("缺少参数");
				rs.setRES_CODE(Global.RESP_ERROR);
				rs.setRES_MESSAGE("请选择查询日期或项目");
				return;
			}
			Date end = dateFormat.parse(endDate);
			Calendar endCalendar = Calendar.getInstance();
			endCalendar.setTime(end);
			endCalendar.add(Calendar.DATE, 1);
			endDate = dateFormat.format(endCalendar.getTime());
			logger.info(startDate + "-" + endDate);
			List<Map<String, Object>> attendanceList = attendanceService
					.sumCountByProject(startDate, endDate, projectID);
			List<ProAttendanceVO> attendaces = new ArrayList<ProAttendanceVO>();
			if (attendanceList != null) {
				for (Map<String, Object> attendance : attendanceList) {
					ProAttendanceVO vo = ProAttendanceVO.assembleAttendance(attendance);
					if (projectID.equals(vo.getProject_id())) {
						vo.setParent_id(projectID);
						vo.setRoot_id(projectID);
					}
					attendaces.add(vo);
				}
			}
			rs.setRES_CODE(Global.RESP_SUCCESS);
			rs.setRES_OBJ(GsonUtil.getInstance().toJson(attendaces));
		} catch (Exception e) {
			e.printStackTrace();
			rs.setRES_CODE(Global.RESP_ERROR);
			rs.setRES_MESSAGE("服务器异常,请重试");
		}
	}

	public void setAttendanceService(AttendanceService attendanceService) {
		this.attendanceService = attendanceService;
	}
}
