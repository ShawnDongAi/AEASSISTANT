package com.zzn.aenote.http.server.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.server.CmHandler;
import com.zzn.aenote.http.service.ProjectService;
import com.zzn.aenote.http.service.UserService;
import com.zzn.aenote.http.utils.GsonUtil;
import com.zzn.aenote.http.utils.RegexUtil;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.vo.BaseRep;
import com.zzn.aenote.http.vo.ProjectVO;
import com.zzn.aenote.http.vo.UserVO;

public class Login implements CmHandler {
	protected static final Logger logger = Logger.getLogger(Login.class);
	private UserService userService;
	private ProjectService projectService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp,
			BaseRep rs) throws Exception {
		try {
			// 用户账号即手机号
			String phone = req.getParameter("phone");
			// 用户密码
			String password = req.getParameter("password");
			logger.info("接收到登陆请求,账号--->" + phone);
			if (StringUtil.isEmpty(phone) || StringUtil.isEmpty(password)) { // 用户名或密码为空
				rs.setRES_CODE(Global.USER_PSW_NULL);
				rs.setRES_MESSAGE("请输入手机号或密码");
				return;
			}
			if (!RegexUtil.isPhoneNum(phone)) {
				rs.setRES_CODE(Global.RESP_PARAM_ERROR);
				rs.setRES_MESSAGE("请输入有效的手机号码");
				return;
			}
			List<Map<String, Object>> userList = userService
					.queryUserByPhone(phone);
			if (userList == null || userList.size() <= 0) {
				rs.setRES_CODE(Global.USER_NOT_EXIST);
				rs.setRES_MESSAGE("手机号未注册");
				return;
			}
			Map<String, Object> userInfo = userList.get(0);
			if (userInfo.get("password").equals(password)) {
				Map<String, Object> result = new HashMap<String, Object>();
				UserVO user = UserVO.assembleUserVO(userInfo);
				result.put("user",
						GsonUtil.getInstance().toJson(user, UserVO.class));
				userService.updateLoginTime(user.getUSER_ID());
				List<ProjectVO> projectList = new ArrayList<ProjectVO>();
				List<Map<String, Object>> projects = projectService
						.queryProjectByCreateUser(user.getUSER_ID());
				if (projects != null && projects.size() > 0) {
					for (Map<String, Object> project : projects) {
						projectList.add(ProjectVO.assembleProject(project));
					}
				}
				result.put("projects",
						GsonUtil.getInstance().toJson(projectList));
				rs.setRES_CODE(Global.RESP_SUCCESS);
				rs.setRES_OBJ(GsonUtil.getInstance().toJson(result));
				rs.setRES_MESSAGE("登录成功");
				logger.info("登录成功.");
			} else {
				rs.setRES_CODE(Global.USER_ERROR);
				rs.setRES_MESSAGE("手机号或密码错误");
			}
		} catch (Exception e) {
			e.printStackTrace();
			rs.setRES_CODE(Global.RESP_ERROR);
			rs.setRES_MESSAGE("服务器异常,请重试");
		}
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}
}
