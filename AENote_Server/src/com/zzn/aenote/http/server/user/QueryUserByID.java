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
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.vo.BaseRep;
import com.zzn.aenote.http.vo.UserVO;

public class QueryUserByID implements CmHandler {
	protected static final Logger logger = Logger
			.getLogger(QueryUserByID.class);
	private UserService userService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp,
			BaseRep rs) throws Exception {
		try {
			String userID = req.getParameter("user_id");
			if (StringUtil.isEmpty(userID)) {
				rs.setRES_CODE(Global.RESP_PARAM_NULL);
				rs.setRES_MESSAGE("缺少参数");
				return;
			}
			List<Map<String, Object>> userList = userService
					.queryUserByID(userID);
			if (userList != null && userList.size() > 0) {
				UserVO user = UserVO.assembleUserVO(userList.get(0));
				rs.setRES_CODE(Global.RESP_SUCCESS);
				rs.setRES_OBJ(GsonUtil.getInstance().toJson(user, UserVO.class));
				rs.setRES_MESSAGE("查询成功");
			} else {
				rs.setRES_CODE(Global.RESP_ERROR);
				rs.setRES_MESSAGE("无此用户");
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
