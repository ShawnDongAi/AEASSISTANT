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
import com.zzn.aenote.http.vo.UserVO;

public class QueryProjectUsers implements CmHandler {
	protected static final Logger logger = Logger
			.getLogger(QueryProjectStructure.class);
	private ProjectService projectService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp,
			BaseRep rs) throws Exception {
		try {
			String project_id = req.getParameter("project_id");
			if (StringUtil.isEmpty(project_id)) {
				rs.setRES_CODE(Global.PROJECT_NULL_PARAMS);
				rs.setRES_MESSAGE("缺少项目信息");
				return;
			}
			List<UserVO> resultList = new ArrayList<UserVO>();
			List<Map<String, Object>> users = projectService
					.queryProjectUsers(project_id);
			if (users != null && users.size() > 0) {
				for (Map<String, Object> user : users) {
					resultList.add(UserVO.assembleUserVO(user));
				}
			}
			rs.setRES_CODE(Global.RESP_SUCCESS);
			rs.setRES_MESSAGE("获取项目成员成功");
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
