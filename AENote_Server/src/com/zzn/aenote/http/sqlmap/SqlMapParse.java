package com.zzn.aenote.http.sqlmap;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class SqlMapParse {
	private static final Logger logger = Logger.getLogger(SqlMapParse.class);
	private SqlMapNode sqlMap;
	private List list;

	public SqlMapNode parse(String sql) {
		sqlMap = new SqlMapNode();
		list = new ArrayList();
		this.parseDynamic(sql);
		SqlMapItem[] items = new SqlMapItem[list.size()];
		for (int i = 0; i < list.size(); i++) {
			items[i] = (SqlMapItem) list.get(i);
		}
		sqlMap.setItems(items);
		return sqlMap;
	}

	private void parseDynamic(String sql) {
		StringBuffer sqlTemplate = new StringBuffer();
		char[] cs = sql.toCharArray();
		int index = 100;
		boolean falg = true;
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < cs.length; i++) {

			if (cs[i] == '[') {
				falg = false;
			} else {
				if (cs[i] == ']') {
					sqlTemplate.append("$T" + index);
					// sqlTemplate.append(" ");
					SqlMapItem item = new SqlMapItem();
					item.setEmpty(false);
					item.setItemID("$T" + index);
					String prap = temp.toString();
					String[] praps = prap.split("#");
					item.setPrapID(praps[1].trim());
					item.setCondit(temp.toString());
					list.add(item);
					temp.delete(0, temp.length());
					index = index + 1;
					falg = true;
				} else {
					if (falg) {
						sqlTemplate.append(cs[i]);
					} else {
						temp.append(cs[i]);
					}
				}
			}

		}
		logger.debug(sqlTemplate.toString());
		parseStatic(sqlTemplate.toString());

	}

	private void parseStatic(String sql) {
		StringBuffer sqlTemplate = new StringBuffer();
		char[] cs = sql.toCharArray();
		boolean falg = true;
		int index = 0;
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < cs.length; i++) {
			if (!falg) {
				if (cs[i] == '#') {
					falg = true;
					SqlMapItem item = new SqlMapItem();
					item.setEmpty(true);
					item.setItemID("$N" + index);
					sqlTemplate.append("$N" + index);
					item.setPrapID(temp.toString());
					item.setCondit(temp.toString());
					list.add(item);
					temp.delete(0, temp.length());
					index = index + 1;
				} else {
					temp.append(cs[i]);
				}
			} else {
				if (cs[i] == '#') {
					falg = false;
				} else {
					sqlTemplate.append(cs[i]);
				}

			}
		}
		logger.debug(sqlTemplate.toString());
		sqlMap.setSqlTemplate(sqlTemplate.toString());
	}
}
