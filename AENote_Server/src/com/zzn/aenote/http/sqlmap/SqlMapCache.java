package com.zzn.aenote.http.sqlmap;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Element;

import com.zzn.aenote.http.AppException;
import com.zzn.aenote.http.utils.PathUtils;
import com.zzn.aenote.http.utils.XMLParseUtils;

public class SqlMapCache {
	private static final Logger logger = Logger.getLogger(SqlMapCache.class);
	private static final Object synObject = new Object();
	private Map SQLS = null;
	private boolean isFlash = false;
	private String path;
	private static SqlMapCache instance = null;

	public static SqlMapCache getSqlMapCache() {
		if (instance == null) {
			synchronized (synObject) {
				if (instance == null) {
					logger.info("初始化查询SQL缓存区.");
					instance = new SqlMapCache();
				}
			}
		}
		return instance;
	}

	public SqlMapNode getSqlMap(final String id) {
		if (isFlash) {
			throw new AppException("SQL缓冲区正在刷新.");
		}
		if (!this.SQLS.containsKey(id)) {
			throw new AppException("找不到ID为<" + id + ">的SQL MAP.");
		}
		return (SqlMapNode) this.SQLS.get(id);
	}

	public void reflash() {
		if (isFlash) {
			throw new AppException("SQL缓冲区正在刷新.");
		}
		isFlash = true;
		logger.info("刷新查询SQL缓冲区.");
		this.buildSQLCache();
		isFlash = false;
	}

	private SqlMapCache() {
		path = PathUtils.getPath(SqlMapCache.class);
	}

	private synchronized void buildSQLCache() {
		this.SQLS = new HashMap();
		String configFile = path + "/resource/SQLEngine.xml";
		// String configFile=path + Global.SQL_ENGINE_PATH;
		logger.info("SQL缓存启动文件" + configFile);
		File file = new File(configFile);
		if (!file.exists()) {
			throw new AppException("找不到SQL缓存启动文件." + configFile);
		}
		List list;
		try {
			list = this.getSQLMapList(file);
		} catch (RuntimeException e) {
			throw new AppException("解析SQL缓存启动文件错误,请检查配置." + configFile);
		}
		this.parseSQLMap(list);

	}

	private List getSQLMapList(File file) {
		XMLParseUtils parse = new XMLParseUtils();
		Element root = parse.getRootElement(file);
		return parse.getListByXPath(root, "/sql-map-config/sql-map");
	}

	private void parseSQLMap(List list) {
		logger.info("需要加载的SQL缓存配置文件共" + list.size() + "个.");
		for (int i = 0; i < list.size(); i++) {
			Element node = (Element) list.get(i);
			String resource = node.getAttributeValue("resource");
			logger.info("===加载SQL缓存配置文件" + resource);
			this.addChild(resource);
		}
	}

	private void addChild(String cpath) {
		File cfile = new File(path + "/" + cpath);
		if (!cfile.exists()) {
			throw new AppException("找不到SQL缓存启动文件." + cfile.getPath());
		}
		List childs = this.getSelectList(cfile);
		for (int i = 0; i < childs.size(); i++) {
			Element node = (Element) childs.get(i);
			String id = node.getAttributeValue("id");
			String sql = node.getText();
			logger.debug("==>id:" + id + ";sql=" + sql);
			SqlMapParse sp = new SqlMapParse();
			SqlMapNode sqlMap = sp.parse(sql);
			sqlMap.setId(id);
			this.SQLS.put(id, sqlMap);
		}
	}

	private List getSelectList(File file) {
		XMLParseUtils parse = new XMLParseUtils();
		Element root = parse.getRootElement(file);
		return parse.getListByXPath(root, "/Scheme/select");
	}
}
