package com.zzn.aenote.http.server.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.server.CmHandler;
import com.zzn.aenote.http.utils.SmsVerifyUtil;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.vo.BaseRep;

public class VerifySmsCode implements CmHandler {
	protected static final Logger logger = Logger.getLogger(VerifySmsCode.class);

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp,
			BaseRep rs) throws Exception {
		try {
			// 用户手机号
			String phone = req.getParameter("phone");
			// 短信验证码
			String smsCode = req.getParameter("code");
			if (StringUtil.isEmpty(smsCode) || StringUtil.isEmpty(phone)) { // 用户名或密码为空
				rs.setRES_CODE(Global.USER_PSW_NULL);
				rs.setRES_MESSAGE("手机号或短信验证码不完整");
				return;
			}
			rs = SmsVerifyUtil.verifySmsCode(rs, phone, smsCode);
		} catch (Exception e) {
			e.printStackTrace();
			rs.setRES_CODE(Global.RESP_ERROR);
			rs.setRES_MESSAGE("服务器异常,请重试");
		}
	}
}
