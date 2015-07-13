package com.zzn.aeassistant.vo;

import java.io.Serializable;

import com.zzn.aeassistant.view.tree.TreeNodeId;
import com.zzn.aeassistant.view.tree.TreeNodeLabel;
import com.zzn.aeassistant.view.tree.TreeNodePid;

public class ProAttendanceVO implements Serializable {

	private static final long serialVersionUID = -5208450861256686116L;

	@TreeNodeId
	private String project_id;
	@TreeNodeLabel
	private String project_name;
	@TreeNodePid
	private String parent_id;
	private String root_id;
	private int count;
	private int exception_count;

	public String getProject_id() {
		return project_id;
	}

	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
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

	public String getProject_name() {
		return project_name;
	}

	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}

	public int getException_count() {
		return exception_count;
	}

	public void setException_count(int exception_count) {
		this.exception_count = exception_count;
	}
}
