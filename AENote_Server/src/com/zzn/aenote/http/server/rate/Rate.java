package com.zzn.aenote.http.server.rate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.server.CmHandler;
import com.zzn.aenote.http.service.RateService;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.vo.BaseRep;

public class Rate implements CmHandler {
	protected static final Logger logger = Logger.getLogger(Rate.class);
	private RateService rateService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp,
			BaseRep rs) throws Exception {
		try {
			String user_id = req.getParameter("user_id").trim();
			String rate_user = req.getParameter("rate_user").trim();
			String rate = req.getParameter("rate").trim();
			String content = req.getParameter("content").trim();
			String project_id = req.getParameter("project_id").trim();
			String root_id = req.getParameter("root_id").trim();
			String isNew = req.getParameter("is_new").trim();
			if (StringUtil.isEmpty(user_id) || StringUtil.isEmpty(rate_user)
					|| StringUtil.isEmpty(rate)
					|| StringUtil.isEmpty(project_id)
					|| StringUtil.isEmpty(root_id) || StringUtil.isEmpty(isNew)) {
				rs.setRES_CODE(Global.RESP_PARAM_NULL);
				rs.setRES_MESSAGE("缺少参数");
				return;
			}
			boolean result = false;
			if (isNew.equals("0")) {// 新增评价
				result = rateService.insertRate(user_id, rate_user, rate,
						content, project_id, root_id);
			} else {// 修改评价
				result = rateService.updateRate(user_id, rate_user, rate,
						content, project_id, root_id);
			}
			if (result) {
				rs.setRES_CODE(Global.RESP_SUCCESS);
				rs.setRES_MESSAGE("评价成功");
			} else {
				rs.setRES_CODE(Global.ORACLE_ERROR);
				rs.setRES_MESSAGE("评价失败，请重试");
			}
		} catch (Exception e) {
			e.printStackTrace();
			rs.setRES_CODE(Global.RESP_ERROR);
			rs.setRES_MESSAGE("服务器异常,请重试");
		}
	}

	public void setRateService(RateService rateService) {
		this.rateService = rateService;
	}
}