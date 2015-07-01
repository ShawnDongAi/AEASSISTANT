package com.zzn.aenote.http;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.zzn.aenote.http.sqlmap.SqlMapTemplate;

/**
 * 简单封装基础服务接口
 * @author Shawn
 */
public abstract class BaseService {
	
	protected static Logger logger = Logger.getLogger(BaseService.class);

	protected String getSql(String sqlId, Map<String, Object> data) {
		String sql = SqlMapTemplate.convertToSQL(sqlId, data);
		logger.info(sql);
		return sql;
	}

	protected JdbcTemplate getJdbc() {
		JdbcTemplate jdbcTemplate = ServiceLocator.getBean2("jdbcTemplate");
		return jdbcTemplate;
	}

}
