package com.zzn.aenote.http.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.zzn.aenote.http.ServiceLocator;
import com.zzn.aenote.http.sqlmap.SqlMapTemplate;

public class PostVO implements Serializable {

	private static final long serialVersionUID = -4627554257014297361L;

	private String post_id;
	private String user_id;
	private String user_name;
	private String user_head;
	private String content;
	private String attch_id;
	private String project_id;
	private String project_name;
	private String root_id;
	private String root_project_name;
	private String time;
	private String send_user_id;
	private String send_user_name;
	private String send_project_id;
	private String send_project_name;
	private String is_private;

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

	public String getSend_user_id() {
		return send_user_id;
	}

	public void setSend_user_id(String send_user_id) {
		this.send_user_id = send_user_id;
	}

	public String getSend_project_id() {
		return send_project_id;
	}

	public void setSend_project_id(String send_project_id) {
		this.send_project_id = send_project_id;
	}

	public String getIs_private() {
		return is_private;
	}

	public void setIs_private(String is_private) {
		this.is_private = is_private;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getProject_name() {
		return project_name;
	}

	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}

	public String getRoot_project_name() {
		return root_project_name;
	}

	public void setRoot_project_name(String root_project_name) {
		this.root_project_name = root_project_name;
	}

	public String getSend_user_name() {
		return send_user_name;
	}

	public void setSend_user_name(String send_user_name) {
		this.send_user_name = send_user_name;
	}

	public String getSend_project_name() {
		return send_project_name;
	}

	public void setSend_project_name(String send_project_name) {
		this.send_project_name = send_project_name;
	}

	public String getUser_head() {
		return user_head;
	}

	public void setUser_head(String user_head) {
		this.user_head = user_head;
	}

	public static PostVO assemblePostVO(Map<String, Object> postInfo) {
		PostVO post = new PostVO();
		if (postInfo.get("post_id") != null) {
			post.setPost_id(postInfo.get("post_id").toString().trim());
		}
		if (postInfo.get("user_id") != null) {
			post.setUser_id(postInfo.get("user_id").toString().trim());
		}
		if (postInfo.get("user_name") != null) {
			post.setUser_name(postInfo.get("user_name").toString().trim());
		}
		if (postInfo.get("user_head") != null) {
			post.setUser_head(postInfo.get("user_head").toString().trim());
		}
		if (postInfo.get("content") != null) {
			post.setContent(postInfo.get("content").toString().trim());
		}
		if (postInfo.get("attch_id") != null) {
			post.setAttch_id(postInfo.get("attch_id").toString().trim());
		}
		if (postInfo.get("project_id") != null) {
			post.setProject_id(postInfo.get("project_id").toString().trim());
		}
		if (postInfo.get("project_name") != null) {
			post.setProject_name(postInfo.get("project_name").toString().trim());
		}
		if (postInfo.get("root_id") != null) {
			post.setRoot_id(postInfo.get("root_id").toString().trim());
		}
		if (postInfo.get("root_project_name") != null) {
			post.setRoot_project_name(postInfo.get("root_project_name").toString().trim());
		}
		if (postInfo.get("time") != null) {
			post.setTime(postInfo.get("time").toString().trim());
		}
		if (postInfo.get("private") != null) {
			post.setIs_private(postInfo.get("private").toString().trim());
		}
		JdbcTemplate jdbcTemplate = ServiceLocator.getBean2("jdbcTemplate");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("post_id", post.getPost_id());
		String sql = SqlMapTemplate.convertToSQL("query_send_info", data);
		List<Map<String, Object>> sendList = jdbcTemplate.queryForList(sql);
		StringBuilder sendUserIds = new StringBuilder();
		StringBuilder sendUserNames = new StringBuilder();
		StringBuilder sendProjectIds = new StringBuilder();
		StringBuilder sendProjectNames = new StringBuilder();
		if (sendList != null && sendList.size() > 0) {
			for (Map<String, Object> sendItem : sendList) {
				sendUserIds.append(sendItem.get("send_user_id") + ",");
				sendUserNames.append(sendItem.get("user_name") + ",");
				sendProjectIds.append(sendItem.get("send_project_id") + ",");
				sendProjectNames.append(sendItem.get("project_name") + ",");
			}
		}
		post.setSend_user_id(sendUserIds.toString());
		post.setSend_user_name(sendUserNames.toString());
		post.setSend_project_id(sendProjectIds.toString());
		post.setSend_project_name(sendProjectNames.toString());
		return post;
	}
}