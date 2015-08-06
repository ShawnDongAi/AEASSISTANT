package com.zzn.aenote.http.server.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.server.CmHandler;
import com.zzn.aenote.http.service.UserService;
import com.zzn.aenote.http.utils.RegexUtil;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.vo.BaseRep;

public class UpdateIDCard implements CmHandler {
	protected static final Logger logger = Logger.getLogger(UpdateIDCard.class);
	private UserService userService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp,
			BaseRep rs) throws Exception {
		try {
			String user_id = req.getParameter("user_id");
			String idcard = req.getParameter("idcard");
			if (StringUtil.isEmpty(user_id) || StringUtil.isEmpty(idcard)) {
				rs.setRES_CODE(Global.USER_ID_NULL);
				rs.setRES_MESSAGE("用户帐号或身份证号不能为空");
				return;
			}
			if (!RegexUtil.isPhoneNum(idcard)) {
				rs.setRES_CODE(Global.RESP_PARAM_ERROR);
				rs.setRES_MESSAGE("请输入有效的身份证号");
				return;
			}
			boolean result = userService.updateUserIDCard(user_id, idcard);
			if (result) {
				rs.setRES_CODE(Global.RESP_SUCCESS);
				rs.setRES_MESSAGE("修改身份证号成功");
				logger.info("修改身份证号成功");
			} else {
				rs.setRES_CODE(Global.USER_HEAD_FAILED);
				rs.setRES_MESSAGE("修改身份证号失败");
				logger.info("修改身份证号失败");
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
