package com.zzn.aenote.http.server.user;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.server.CmHandler;
import com.zzn.aenote.http.service.UserService;
import com.zzn.aenote.http.utils.GsonUtil;
import com.zzn.aenote.http.utils.SmsVerifyUtil;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.vo.BaseRep;
import com.zzn.aenote.http.vo.UserVO;

public class Register implements CmHandler {
	protected static final Logger logger = Logger.getLogger(Register.class);
	private UserService userService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp,
			BaseRep rs) throws Exception {
		try {
			// 用户手机号
			String phone = req.getParameter("phone");
			// 用户密码
			String password = req.getParameter("password");
			// 短信验证码
			String smsCode = req.getParameter("code");
			logger.info("接收到注册请求,账号--->" + phone);
			if (StringUtil.isEmpty(smsCode) || StringUtil.isEmpty(password)
					|| StringUtil.isEmpty(phone)) { // 用户名或密码为空
				rs.setRES_CODE(Global.USER_PSW_NULL);
				rs.setRES_MESSAGE("手机号、密码或短信验证码不完整");
				return;
			}
			rs = SmsVerifyUtil.verifySmsCode(rs, phone, smsCode);
			if (!rs.getRES_CODE().equals(Global.RESP_SUCCESS)) {
				return;
			}
			List<Map<String, Object>> userList = userService
					.queryUserByPhone(phone);
			if (userList != null && userList.size() > 0) {// 注册失败，账号已存在
				rs.setRES_CODE(Global.USER_ERROR);
				rs.setRES_MESSAGE("手机号已被注册");
			} else {
				UserVO user = userService.register(phone, password);
				if (user == null) {
					rs.setRES_CODE(Global.REGISTER_ERROR);
					rs.setRES_MESSAGE("注册失败,请重试");
				}
				logger.info("插入新用户成功");
				rs.setRES_CODE(Global.RESP_SUCCESS);
				rs.setRES_OBJ(GsonUtil.getInstance().toJson(user, UserVO.class));
				rs.setRES_MESSAGE("注册成功");
				logger.info("注册成功");
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
