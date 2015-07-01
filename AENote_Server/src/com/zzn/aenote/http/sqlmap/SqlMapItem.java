package com.zzn.aenote.http.sqlmap;

/**
 * linzz
 */

public class SqlMapItem {
	private String itemID;
	private String prapID;
	private boolean isEmpty;
	private String condit;
	public String getItemID() {
		return itemID;
	}
	public void setItemID(String itemID) {
		this.itemID = itemID;
	}
	public boolean isEmpty() {
		return isEmpty;
	}
	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}
	public String getCondit() {
		return condit;
	}
	public void setCondit(String condit) {
		this.condit = condit;
	}
	public String getPrapID() {
		return prapID;
	}
	public void setPrapID(String prapID) {
		this.prapID = prapID;
	}
}
