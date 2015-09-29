package com.zzn.aenote.http.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.BaseService;
import com.zzn.aenote.http.utils.UtilUniqueKey;
import com.zzn.aenote.http.vo.CommentVO;
import com.zzn.aenote.http.vo.PostVO;
import com.zzn.aenote.http.vo.ProjectVO;

/**
 * 朋友圈相关信息操作类
 * 
 * @author Shawn
 */
public class PostService extends BaseService {
	private static final Logger logger = Logger.getLogger(PostService.class);
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public PostVO insertPost(String content, String attch_id, ProjectVO project, String is_private,
			List<ProjectVO> sendProjectVOs) {
		try {
			Date date = new Date(System.currentTimeMillis());
			PostVO post = new PostVO();
			post.setPost_id(UtilUniqueKey.getKey("post_id" + System.currentTimeMillis()));
			post.setUser_id(project.getCREATE_USER());
			post.setUser_name(project.getCREATE_USER_NAME());
			post.setUser_head(project.getCREATE_USER_HEAD());
			post.setContent(content);
			post.setAttch_id(attch_id);
			post.setProject_id(project.getPROJECT_ID());
			post.setProject_name(project.getPROJECT_NAME());
			post.setRoot_id(project.getROOT_ID());
			post.setRoot_project_name(project.getROOT_PROJECT_NAME());
			post.setIs_private(is_private);
			post.setTime(format.format(date));
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("post_id", post.getPost_id());
			data.put("user_id", project.getCREATE_USER());
			data.put("content", content);
			data.put("attch_id", attch_id);
			data.put("project_id", project.getPROJECT_ID());
			data.put("root_id", project.getROOT_ID());
			data.put("time", format.format(date));
			data.put("private", is_private);
			getJdbc().execute(getSql("insert_post", data));

			StringBuilder sendProjects = new StringBuilder();
			StringBuilder sendProjectNames = new StringBuilder();
			StringBuilder sendUsers = new StringBuilder();
			StringBuilder sendUserNames = new StringBuilder();
			if (sendProjectVOs == null) {
				sendProjectVOs = new ArrayList<ProjectVO>();
			}
			for (ProjectVO projectVO : sendProjectVOs) {
				Map<String, Object> temp = new HashMap<String, Object>();
				temp.put("post_id", post.getPost_id());
				temp.put("send_user_id", projectVO.getCREATE_USER());
				temp.put("send_project_id", projectVO.getPROJECT_ID());
				getJdbc().execute(getSql("insert_post_send", temp));
				sendProjects.append(projectVO.getPROJECT_ID() + "@");
				sendProjectNames.append(projectVO.getPROJECT_NAME() + "@");
				sendUsers.append(projectVO.getCREATE_USER() + "@");
				sendUserNames.append(projectVO.getCREATE_USER_NAME() + "@");
			}
			if (sendProjects.length() > 0) {
				sendProjects.deleteCharAt(sendProjects.length() - 1);
			}
			if (sendProjectNames.length() > 0) {
				sendProjectNames.deleteCharAt(sendProjectNames.length() - 1);
			}
			if (sendUsers.length() > 0) {
				sendUsers.deleteCharAt(sendUsers.length() - 1);
			}
			if (sendUserNames.length() > 0) {
				sendUserNames.deleteCharAt(sendUserNames.length() - 1);
			}
			post.setSend_project_id(sendProjects.toString());
			post.setSend_project_name(sendProjectNames.toString());
			post.setSend_user_id(sendUsers.toString());
			post.setSend_user_name(sendUserNames.toString());
			return post;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<PostVO> queryPost(String project_id) {
		List<PostVO> result = new ArrayList<PostVO>();
		try {
			List<Map<String, Object>> projectList = new ArrayList<Map<String, Object>>();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("project_id", project_id);
			projectList = getJdbc().queryForList(getSql("query_leaf_project", data));
			StringBuilder projectIds = new StringBuilder(project_id);
			if (projectList != null && projectList.size() > 0) {
				for (Map<String, Object> project : projectList) {
					projectIds.append("," + project.get("project_id").toString());
				}
			}
			data.put("project_id", projectIds.toString());
			List<Map<String, Object>> postList = getJdbc().queryForList(getSql("query_post", data));
			if (postList != null && postList.size() > 0) {
				for (Map<String, Object> post : postList) {
					result.add(PostVO.assemblePostVO(post));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public List<PostVO> queryNextPost(String project_id, String time) {
		List<PostVO> result = new ArrayList<PostVO>();
		try {
			List<Map<String, Object>> projectList = new ArrayList<Map<String, Object>>();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("project_id", project_id);
			projectList = getJdbc().queryForList(getSql("query_leaf_project", data));
			StringBuilder projectIds = new StringBuilder(project_id);
			if (projectList != null && projectList.size() > 0) {
				for (Map<String, Object> project : projectList) {
					projectIds.append("," + project.get("project_id").toString());
				}
			}
			data.put("project_id", projectIds.toString());
			data.put("time", time);
			List<Map<String, Object>> postList = getJdbc().queryForList(getSql("query_post_by_end", data));
			if (postList != null && postList.size() > 0) {
				for (Map<String, Object> post : postList) {
					result.add(PostVO.assemblePostVO(post));
				}
			}
		} catch (Exception e) {
		}
		return result;
	}

	public CommentVO insertComment(String post_id, String content, String attch_id, ProjectVO project) {
		try {
			Date date = new Date(System.currentTimeMillis());
			CommentVO comment = new CommentVO();
			comment.setComment_id(UtilUniqueKey.getKey("comment" + System.currentTimeMillis()));
			comment.setPost_id(post_id);
			comment.setUser_id(project.getCREATE_USER());
			comment.setUser_name(project.getCREATE_USER_NAME());
			comment.setContent(content);
			comment.setAttch_id(attch_id);
			comment.setProject_id(project.getPROJECT_ID());
			comment.setProject_name(project.getPROJECT_NAME());
			comment.setRoot_id(project.getROOT_ID());
			comment.setTime(format.format(date));
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("comment_id", comment.getComment_id());
			data.put("post_id", post_id);
			data.put("user_id", project.getCREATE_USER());
			data.put("content", content);
			data.put("attch_id", attch_id);
			data.put("project_id", project.getPROJECT_ID());
			data.put("root_id", project.getROOT_ID());
			data.put("time", format.format(date));
			getJdbc().execute(getSql("insert_comment", data));
			return comment;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<CommentVO> queryComment(String postIds) {
		List<CommentVO> result = new ArrayList<CommentVO>();
		try {
			List<Map<String, Object>> commentList = new ArrayList<Map<String, Object>>();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("post_id", postIds);
			commentList = getJdbc().queryForList(getSql("query_comment", data));
			if (commentList != null && commentList.size() > 0) {
				for (Map<String, Object> comment : commentList) {
					result.add(CommentVO.assembleCommentVO(comment));
				}
			}
		} catch (Exception e) {
		}
		return result;
	}
}