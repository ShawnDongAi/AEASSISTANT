package com.zzn.aeassistant.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class AttendanceVO implements Parcelable {

	private String project_id;
	private String project_name;
	private String parent_id;
	private String root_id;
	private String date;
	private String imgURL;
	private String user_id;
	private String user_name;
	private String user_phone;
	private String address;
	private String longitude;
	private String latitude;
	private String normal;
	private String status;
	private int photo_width;
	private int photo_height;
	private String root_project_name;

	public String getProject_id() {
		return project_id;
	}

	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}

	public String getProject_name() {
		return project_name;
	}

	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public String getRoot_id() {
		return root_id;
	}

	public void setRoot_id(String root_id) {
		this.root_id = root_id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getImgURL() {
		return imgURL;
	}

	public void setImgURL(String imgURL) {
		this.imgURL = imgURL;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_phone() {
		return user_phone;
	}

	public void setUser_phone(String user_phone) {
		this.user_phone = user_phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getNormal() {
		return normal;
	}

	public void setNormal(String normal) {
		this.normal = normal;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getRoot_project_name() {
		return root_project_name;
	}

	public void setRoot_project_name(String root_project_name) {
		this.root_project_name = root_project_name;
	}

	public int getPhoto_width() {
		return photo_width;
	}

	public void setPhoto_width(int photo_width) {
		this.photo_width = photo_width;
	}

	public int getPhoto_height() {
		return photo_height;
	}

	public void setPhoto_height(int photo_height) {
		this.photo_height = photo_height;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(project_id);
		out.writeString(project_name);
		out.writeString(parent_id);
		out.writeString(root_id);
		out.writeString(date);
		out.writeString(imgURL);
		out.writeString(user_id);
		out.writeString(user_name);
		out.writeString(user_phone);
		out.writeString(address);
		out.writeString(longitude);
		out.writeString(latitude);
		out.writeString(normal);
		out.writeString(status);
		out.writeInt(photo_width);
		out.writeInt(photo_height);
		out.writeString(root_project_name);
	}

	public static final Parcelable.Creator<AttendanceVO> CREATOR = new Creator<AttendanceVO>() {
		@Override
		public AttendanceVO[] newArray(int size) {
			return new AttendanceVO[size];
		}

		@Override
		public AttendanceVO createFromParcel(Parcel in) {
			return new AttendanceVO(in);
		}
	};

	public AttendanceVO(Parcel in) {
		project_id = in.readString();
		project_name = in.readString();
		parent_id = in.readString();
		root_id = in.readString();
		date = in.readString();
		imgURL = in.readString();
		user_id = in.readString();
		user_name = in.readString();
		user_phone = in.readString();
		address = in.readString();
		longitude = in.readString();
		latitude = in.readString();
		normal = in.readString();
		status = in.readString();
		photo_width = in.readInt();
		photo_height = in.readInt();
		root_project_name = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
