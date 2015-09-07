package com.zzn.aenote.http.server.post;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.reflect.TypeToken;
import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.server.CmHandler;
import com.zzn.aenote.http.service.PostService;
import com.zzn.aenote.http.utils.GsonUtil;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.vo.BaseRep;
import com.zzn.aenote.http.vo.PostVO;
import com.zzn.aenote.http.vo.ProjectVO;

public class Post implements CmHandler {
	protected static final Logger logger = Logger.getLogger(Post.class);
	private PostService postService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp,
			BaseRep rs) throws Exception {
		try {
			String content = req.getParameter("content");
			String projectString = req.getParameter("project");
			String is_private = req.getParameter("is_private");
			String sendProjectString = req.getParameter("send_project");
			if (StringUtil.isEmpty(is_private)) {
				is_private = "0";
			}
			if (StringUtil.isEmpty(sendProjectString)) {
				sendProjectString = "";
			}
			if (StringUtil.isEmpty(projectString)
					|| StringUtil.isEmpty(content)) {
				rs.setRES_CODE(Global.RESP_PARAM_NULL);
				rs.setRES_MESSAGE("信息不完整");
				return;
			}
			ProjectVO project = GsonUtil.getInstance().fromJson(projectString,
					ProjectVO.class);
			List<ProjectVO> sendProjectVOs = GsonUtil.getInstance().fromJson(
					sendProjectString, new TypeToken<List<ProjectVO>>() {
					}.getType());
			PostVO post = postService.insertPost(content, "", project,
					is_private, sendProjectVOs);
			if (post == null) {
				rs.setRES_CODE(Global.USER_ERROR);
				rs.setRES_MESSAGE("发帖失败");
			} else {
				rs.setRES_CODE(Global.RESP_SUCCESS);
				rs.setRES_OBJ(GsonUtil.getInstance().toJson(post, PostVO.class));
				rs.setRES_MESSAGE("发帖成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			rs.setRES_CODE(Global.RESP_ERROR);
			rs.setRES_MESSAGE("服务器异常,请重试");
		}
	}

	public void setPostService(PostService postService) {
		this.postService = postService;
	}
}