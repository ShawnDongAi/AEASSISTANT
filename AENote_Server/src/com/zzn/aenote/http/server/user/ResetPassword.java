package com.zzn.aenote.http.server.user;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.server.CmHandler;
import com.zzn.aenote.http.service.UserService;
import com.zzn.aenote.http.utils.DESCoderUtil;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.vo.BaseRep;

public class ResetPassword implements CmHandler {
	protected static final Logger logger = Logger
			.getLogger(ResetPassword.class);
	private UserService userService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp,
			BaseRep rs) throws Exception {
		try {
			// 用户手机号
			String phone = req.getParameter("phone");
			// 用户密码
			String password = req.getParameter("password");
			password = DESCoderUtil.decrypt(password, phone);
			logger.info("接收到注册请求,账号--->" + phone);
			if (StringUtil.isEmpty(password) || StringUtil.isEmpty(phone)) { // 用户名或密码为空
				rs.setRES_CODE(Global.USER_PSW_NULL);
				rs.setRES_MESSAGE("手机号或密码不完整");
				return;
			}
			List<Map<String, Object>> userList = userService
					.queryUserByPhone(phone);
			if (userList != null && userList.size() > 0) {
				boolean result = userService.resetPassword(
						userList.get(0).get("user_id").toString(), password);
				if (result) {
					rs.setRES_CODE(Global.RESP_SUCCESS);
					rs.setRES_MESSAGE("修改密码成功,请重新登录");
				} else {
					rs.setRES_CODE(Global.RESET_PSW_FAILED);
					rs.setRES_MESSAGE("修改密码失败,请重试");
				}
			} else {
				rs.setRES_CODE(Global.USER_NOT_EXIST);
				rs.setRES_MESSAGE("该手机号未被注册");
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
}
