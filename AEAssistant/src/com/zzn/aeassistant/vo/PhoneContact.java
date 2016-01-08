package com.zzn.aeassistant.vo;

public class PhoneContact {
	public static final int ITEM = 1;
	public static final int SECTION = 0;
	private int type;
	private String name;
	private String phone;
	private int sectionPosition;
	private int listPosition;
	private String sortLetter;

	public PhoneContact() {}
	
	public PhoneContact(int type) {
		this.type = type;
	}
	
	public PhoneContact(int type, String name, String phone) {
		this.type = type;
		this.name = name;
		this.phone = phone;
	}

	@Override
	public String toString() {
		return phone;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getSectionPosition() {
		return sectionPosition;
	}

	public void setSectionPosition(int sectionPosition) {
		this.sectionPosition = sectionPosition;
	}

	public int getListPosition() {
		return listPosition;
	}

	public void setListPosition(int listPosition) {
		this.listPosition = listPosition;
	}

	public String getSortLetter() {
		return sortLetter;
	}

	public void setSortLetter(String sortLetter) {
		this.sortLetter = sortLetter;
	}
}