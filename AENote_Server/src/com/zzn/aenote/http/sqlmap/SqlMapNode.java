package com.zzn.aenote.http.sqlmap;

/**
 * linzz
 */

public class SqlMapNode {
	private String id;
	private SqlMapItem[] items;
	private String sqlTemplate;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public SqlMapItem[] getItems() {
		return items;
	}
	public void setItems(SqlMapItem[] items) {
		this.items = items;
	}
	public String getSqlTemplate() {
		return sqlTemplate;
	}
	public void setSqlTemplate(String sqlTemplate) {
		this.sqlTemplate = sqlTemplate;
	}
	 
}
