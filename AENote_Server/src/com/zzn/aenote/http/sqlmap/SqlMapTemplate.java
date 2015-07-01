package com.zzn.aenote.http.sqlmap;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

public abstract class SqlMapTemplate {
	 
	private static SqlMapCache CACHE = SqlMapCache.getSqlMapCache();

	public static String convertToSQL(String templateID, Map data) {
		SqlMapNode node = CACHE.getSqlMap(templateID);
		SqlMapItem[] items = node.getItems();
		String sql = node.getSqlTemplate();
		for (int i = 0; i < items.length; i++) {
			if (!items[i].isEmpty()) {
				// 获取传入ID的值
				if (data.containsKey(items[i].getPrapID())) {
					String value = (String) data.get(items[i].getPrapID());
					if (!isEmpty(value)) {
						String codi = items[i].getCondit();
						codi = StringUtils.replace(codi, "#"
								+ items[i].getPrapID() + "#", value);
						sql = StringUtils.replace(sql, items[i].getItemID(),
								codi);
					} else {
						sql = StringUtils.replace(sql, items[i].getItemID(),
								" ");
					}

				} else {
					sql = StringUtils.replace(sql, items[i].getItemID(), " ");
				}
			} else {
				String value = (String) data.get(items[i].getPrapID());
				String codi = items[i].getCondit();
				codi = StringUtils.replace(codi, items[i].getPrapID(), value);
				sql = StringUtils.replace(sql, items[i].getItemID(), codi);
			}
		}
		return sql;
	}

	private static final boolean isEmpty(final String value) {
		return value == null || value.length() == 0;
	}
}
