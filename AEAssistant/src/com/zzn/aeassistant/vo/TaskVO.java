package com.zzn.aeassistant.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TaskVO implements Serializable {
	private static final long serialVersionUID = -5281771921128743054L;
	private String task_id;
	private String create_user_id;
	private String create_project_id;
	private String root_id;
	private String time;
	private String create_user_name;
	private String create_project_name;
	private String root_project_name;
	private String create_user_head;
	private List<TaskDetailVO> task_detail_list = new ArrayList<>();

	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

	public String getCreate_user_id() {
		return create_user_id;
	}

	public void setCreate_user_id(String create_user_id) {
		this.create_user_id = create_user_id;
	}

	public String getCreate_project_id() {
		return create_project_id;
	}

	public void setCreate_project_id(String create_project_id) {
		this.create_project_id = create_project_id;
	}

	public String getRoot_id() {
		return root_id;
	}

	public void setRoot_id(String root_id) {
		this.root_id = root_id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getCreate_user_name() {
		return create_user_name;
	}

	public void setCreate_user_name(String create_user_name) {
		this.create_user_name = create_user_name;
	}

	public String getCreate_project_name() {
		return create_project_name;
	}

	public void setCreate_project_name(String create_project_name) {
		this.create_project_name = create_project_name;
	}

	public String getRoot_project_name() {
		return root_project_name;
	}

	public void setRoot_project_name(String root_project_name) {
		this.root_project_name = root_project_name;
	}

	public List<TaskDetailVO> getTask_detail_list() {
		return task_detail_list;
	}

	public void setTask_detail_list(List<TaskDetailVO> task_detail_list) {
		this.task_detail_list = task_detail_list;
	}

	public String getCreate_user_head() {
		return create_user_head;
	}

	public void setCreate_user_head(String create_user_head) {
		this.create_user_head = create_user_head;
	}
}