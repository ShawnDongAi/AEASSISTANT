package com.zzn.aenote.http.vo;

import java.io.Serializable;
import java.util.Map;

import com.zzn.aenote.http.utils.StringUtil;

public class TaskDetailVO implements Serializable {

	private static final long serialVersionUID = 5253392614240527610L;

	private String task_id;
	private String task_detail_id;
	private String process_user_id;
	private String process_project_id;
	private String content;
	private String attch_id;
	private String start_time;
	private String end_time;
	private String status;
	private String process_content;
	private String process_attch_id;
	private String process_user_name;
	private String process_user_head;
	private String process_project_name;
	private String time;
	private String create_user_id;
	private String create_project_id;
	private String root_id;
	private String create_user_name;
	private String create_user_head;
	private String create_project_name;
	private String root_project_name;

	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

	public String getTask_detail_id() {
		return task_detail_id;
	}

	public void setTask_detail_id(String task_detail_id) {
		this.task_detail_id = task_detail_id;
	}

	public String getProcess_user_id() {
		return process_user_id;
	}

	public void setProcess_user_id(String process_user_id) {
		this.process_user_id = process_user_id;
	}

	public String getProcess_project_id() {
		return process_project_id;
	}

	public void setProcess_project_id(String process_project_id) {
		this.process_project_id = process_project_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAttch_id() {
		return attch_id;
	}

	public void setAttch_id(String attch_id) {
		this.attch_id = attch_id;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProcess_content() {
		return process_content;
	}

	public void setProcess_content(String process_content) {
		this.process_content = process_content;
	}

	public String getProcess_attch_id() {
		return process_attch_id;
	}

	public void setProcess_attch_id(String process_attch_id) {
		this.process_attch_id = process_attch_id;
	}

	public String getProcess_user_name() {
		return process_user_name;
	}

	public void setProcess_user_name(String process_user_name) {
		this.process_user_name = process_user_name;
	}

	public String getProcess_user_head() {
		return process_user_head;
	}

	public void setProcess_user_head(String process_user_head) {
		this.process_user_head = process_user_head;
	}

	public String getProcess_project_name() {
		return process_project_name;
	}

	public void setProcess_project_name(String process_project_name) {
		this.process_project_name = process_project_name;
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

	public String getCreate_user_head() {
		return create_user_head;
	}

	public void setCreate_user_head(String create_user_head) {
		this.create_user_head = create_user_head;
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

	public static TaskDetailVO assembleTaskDetailVO(Map<String, Object> taskDetail) {
		TaskDetailVO vo = new TaskDetailVO();
		vo.setTask_id(StringUtil.nullToString(taskDetail.get("task_id")));
		vo.setTask_detail_id(StringUtil.nullToString(taskDetail.get("task_detail_id")));
		vo.setProcess_user_id(StringUtil.nullToString(taskDetail.get("process_user_id")));
		vo.setProcess_project_id(StringUtil.nullToString(taskDetail.get("process_project_id")));
		vo.setContent(StringUtil.nullToString(taskDetail.get("content")));
		vo.setAttch_id(StringUtil.nullToString(taskDetail.get("attch_id")));
		vo.setStart_time(StringUtil.nullToString(taskDetail.get("start_time")));
		vo.setEnd_time(StringUtil.nullToString(taskDetail.get("end_time")));
		vo.setStatus(StringUtil.nullToString(taskDetail.get("status")));
		vo.setProcess_content(StringUtil.nullToString(taskDetail.get("process_content")));
		vo.setProcess_attch_id(StringUtil.nullToString(taskDetail.get("process_attch_id")));
		vo.setProcess_user_name(StringUtil.nullToString(taskDetail.get("process_user_name")));
		vo.setProcess_user_head(StringUtil.nullToString(taskDetail.get("process_user_head")));
		vo.setProcess_project_name(StringUtil.nullToString(taskDetail.get("process_project_name")));
		vo.setTime(StringUtil.nullToString(taskDetail.get("time")));
		vo.setCreate_user_name(StringUtil.nullToString(taskDetail.get("create_user_name")));
		vo.setCreate_user_head(StringUtil.nullToString(taskDetail.get("create_user_head")));
		vo.setCreate_project_name(StringUtil.nullToString(taskDetail.get("create_project_name")));
		vo.setRoot_project_name(StringUtil.nullToString(taskDetail.get("root_project_name")));
		vo.setCreate_user_id(StringUtil.nullToString(taskDetail.get("create_user_id")));
		vo.setCreate_project_id(StringUtil.nullToString(taskDetail.get("create_project_id")));
		vo.setRoot_id(StringUtil.nullToString(taskDetail.get("root_id")));
		return vo;
	}
}