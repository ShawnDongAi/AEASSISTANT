package com.zzn.aenote.http.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.BaseService;
import com.zzn.aenote.http.utils.UtilUniqueKey;
import com.zzn.aenote.http.vo.AttchVO;
/**
 * 附件相关信息操作类
 * @author Shawn
 */
public class RateService extends BaseService {
	private static final Logger logger = Logger.getLogger(RateService.class);
	
	public AttchVO insertAttch(String type, String name, String url) {
		try {
			AttchVO attch = new AttchVO();
			attch.setATTCH_ID(UtilUniqueKey.getKey(name));
			attch.setTYPE(type);
			attch.setNAME(name);
			attch.setURL(url);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("attch_id", attch.getATTCH_ID());
			data.put("type", type);
			data.put("name", name);
			data.put("url", url);
			getJdbc().execute(getSql("insert_attch", data));
			return attch;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Map<String, Object>> queryAttch(String attch_id) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("attch_id", attch_id);
		List<Map<String, Object>> attchList = getJdbc().queryForList(
				getSql("query_attch", data));
		return attchList;
	}
}
