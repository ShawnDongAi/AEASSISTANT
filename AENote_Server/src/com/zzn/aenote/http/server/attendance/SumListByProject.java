package com.zzn.aenote.http.server.attendance;

import java.util.ArrayList;
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
import com.zzn.aenote.http.vo.AttendanceVO;
import com.zzn.aenote.http.vo.BaseRep;

public class SumListByProject implements CmHandler {
	protected static final Logger logger = Logger.getLogger(SumListByProject.class);
	private AttendanceService attendanceService;

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
			}
			List<Map<String, Object>> attendanceList = attendanceService.sumListByProject(startDate, endDate, projectID);
			List<AttendanceVO> result = new ArrayList<AttendanceVO>();
			for (Map<String, Object> attendance : attendanceList) {
				AttendanceVO vo = AttendanceVO.assembleAttendance(attendance);
				result.add(vo);
			}
			rs.setRES_CODE(Global.RESP_SUCCESS);
			rs.setRES_OBJ(GsonUtil.getInstance().toJson(result));
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
