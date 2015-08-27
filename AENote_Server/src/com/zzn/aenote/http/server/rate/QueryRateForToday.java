package com.zzn.aenote.http.server.rate;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.server.CmHandler;
import com.zzn.aenote.http.service.RateService;
import com.zzn.aenote.http.utils.GsonUtil;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.vo.BaseRep;
import com.zzn.aenote.http.vo.RateVO;

public class QueryRateForToday implements CmHandler {
	protected static final Logger logger = Logger
			.getLogger(QueryRateForToday.class);
	private RateService rateService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp,
			BaseRep rs) throws Exception {
		try {
			String user_id = req.getParameter("user_id").trim();
			String rate_user = req.getParameter("rate_user").trim();
			if (StringUtil.isEmpty(user_id) || StringUtil.isEmpty(rate_user)) {
				rs.setRES_CODE(Global.RESP_PARAM_NULL);
				rs.setRES_MESSAGE("缺少参数");
				return;
			}
			List<Map<String, Object>> result = rateService.queryRateForToday(
					user_id, rate_user);
			if (result != null && result.size() > 0) {
				rs.setRES_CODE(Global.RESP_SUCCESS);
				rs.setRES_OBJ(GsonUtil.getInstance().toJson(
						RateVO.assembleRate(result.get(0))));
				rs.setRES_MESSAGE("查询成功");
				return;
			}
			rs.setRES_CODE(Global.RESP_SUCCESS);
			rs.setRES_MESSAGE("查询成功");
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