package com.zzn.aenote.http.vo;

import java.io.Serializable;
import java.util.Map;

public class CommentVO implements Serializable {

	private static final long serialVersionUID = -8901730542466409241L;

	private String comment_id;
	private String post_id;
	private String user_id;
	private String user_name;
	private String content;
	private String attch_id;
	private String project_id;
	private String project_name;
	private String root_id;
	private String time;

	public String getComment_id() {
		return comment_id;
	}

	public void setComment_id(String comment_id) {
		this.comment_id = comment_id;
	}

	public String getPost_id() {
		return post_id;
	}

	public void setPost_id(String post_id) {
		this.post_id = post_id;
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
	
	public static CommentVO assembleCommentVO(Map<String, Object> commentInfo) {
		CommentVO comment = new CommentVO();
		if (commentInfo.get("comment_id") != null) {
			comment.setComment_id(commentInfo.get("comment_id").toString().trim());
		}
		if (commentInfo.get("post_id") != null) {
			comment.setPost_id(commentInfo.get("post_id").toString().trim());
		}
		if (commentInfo.get("user_id") != null) {
			comment.setUser_id(commentInfo.get("user_id").toString().trim());
		}
		if (commentInfo.get("user_name") != null) {
			comment.setUser_name(commentInfo.get("user_name").toString().trim());
		}
		if (commentInfo.get("content") != null) {
			comment.setContent(commentInfo.get("content").toString().trim());
		}
		if (commentInfo.get("attch_id") != null) {
			comment.setAttch_id(commentInfo.get("attch_id").toString().trim());
		}
		if (commentInfo.get("project_id") != null) {
			comment.setProject_id(commentInfo.get("project_id").toString().trim());
		}
		if (commentInfo.get("project_name") != null) {
			comment.setProject_name(commentInfo.get("project_name").toString().trim());
		}
		if (commentInfo.get("root_id") != null) {
			comment.setRoot_id(commentInfo.get("root_id").toString().trim());
		}
		if (commentInfo.get("time") != null) {
			comment.setTime(commentInfo.get("time").toString().trim());
		}
		return comment;
	}
}