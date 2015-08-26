package com.zzn.aenote.http.vo;

import java.io.Serializable;
import java.util.Map;

public class VersionVO implements Serializable {

	private static final long serialVersionUID = -1737223586424620097L;

	private String platform;
	private String version_code;
	private String version_name;
	private String instruction;
	private String url;

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getVersion_code() {
		return version_code;
	}

	public void setVersion_code(String version_code) {
		this.version_code = version_code;
	}

	public String getVersion_name() {
		return version_name;
	}

	public void setVersion_name(String version_name) {
		this.version_name = version_name;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public static VersionVO assembleVersion(Map<String, Object> version) {
		VersionVO vo = new VersionVO();
		vo.setPlatform(version.get("platform").toString());
		vo.setInstruction(version.get("instruction").toString());
		vo.setUrl(version.get("url").toString());
		vo.setVersion_code(version.get("version_code").toString());
		vo.setVersion_name(version.get("version_name").toString());
		return vo;
	}
}
