package com.zzn.aenote.http.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.ServiceLocator;
import com.zzn.aenote.http.utils.GsonUtil;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.vo.BaseRep;
import com.zzn.aenote.http.vo.VersionVO;

public class VersionUpdate implements CmHandler {
	protected static final Logger logger = Logger
			.getLogger(VersionUpdate.class);

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp,
			BaseRep rs) throws Exception {
		try {
			String platform = req.getParameter("platform");
			if (StringUtil.isEmpty(platform)) {
				rs.setRES_CODE(Global.USER_ID_NULL);
				rs.setRES_MESSAGE("平台类型为空");
				return;
			}
			JdbcTemplate jdbcTemplate = ServiceLocator.getBean2("jdbcTemplate");
			List<Map<String, Object>> versionMaps = new ArrayList<Map<String, Object>>();
			try {
				versionMaps = jdbcTemplate
						.queryForList("select * from t_version where platform='"
								+ platform + "' order by version_code desc");
			} catch (Exception e) {
			}
			if (versionMaps != null && versionMaps.size() > 0) {
				VersionVO versionVO = VersionVO.assembleVersion(versionMaps
						.get(0));
				rs.setRES_OBJ(GsonUtil.getInstance().toJson(versionVO));
				rs.setRES_CODE(Global.RESP_SUCCESS);
				rs.setRES_MESSAGE("当前已是最新版本");
			} else {
				rs.setRES_CODE(Global.ORACLE_ERROR);
				rs.setRES_MESSAGE("当前已是最新版本");
				logger.info("当前已是最新版本");
			}
		} catch (Exception e) {
			e.printStackTrace();
			rs.setRES_CODE(Global.RESP_ERROR);
			rs.setRES_MESSAGE("服务器异常,请重试");
		}
	}
}
