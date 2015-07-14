package com.zzn.aenote.http.server;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.ServiceLocator;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.vo.BaseRep;

public class Feedback implements CmHandler {
	protected static final Logger logger = Logger.getLogger(Feedback.class);
	private static SimpleDateFormat fromat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp,
			BaseRep rs) throws Exception {
		try {
			String user_id = req.getParameter("user_id");
			String content = req.getParameter("content");
			if (StringUtil.isEmpty(user_id)) {
				rs.setRES_CODE(Global.USER_ID_NULL);
				rs.setRES_MESSAGE("用户信息无效,请重新登录");
				return;
			}
			if (StringUtil.isEmpty(content)) {
				rs.setRES_CODE(Global.USER_ID_NULL);
				rs.setRES_MESSAGE("反馈内容不能为空");
				return;
			}
			String time = fromat.format(new Date(System.currentTimeMillis()));
			JdbcTemplate jdbcTemplate = ServiceLocator.getBean2("jdbcTemplate");
			jdbcTemplate
					.execute("insert into t_feedback(user_id,content,time) values('"
							+ user_id
							+ "','"
							+ content
							+ "',to_date('"
							+ time
							+ "','yyyy-MM-dd HH24:mi:ss'))");
			rs.setRES_CODE(Global.RESP_SUCCESS);
			rs.setRES_MESSAGE("反馈成功");
		} catch (Exception e) {
			e.printStackTrace();
			rs.setRES_CODE(Global.RESP_ERROR);
			rs.setRES_MESSAGE("服务器异常,请重试");
		}
	}
}
