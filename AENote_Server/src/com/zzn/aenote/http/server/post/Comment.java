package com.zzn.aenote.http.server.post;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.server.CmHandler;
import com.zzn.aenote.http.service.PostService;
import com.zzn.aenote.http.utils.GsonUtil;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.vo.BaseRep;
import com.zzn.aenote.http.vo.CommentVO;
import com.zzn.aenote.http.vo.ProjectVO;

public class Comment implements CmHandler {
	private PostService postService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp, BaseRep rs) throws Exception {
		try {
			String content = req.getParameter("content");
			String projectString = req.getParameter("project");
			String attachIds = req.getParameter("attach_ids");
			String post_id = req.getParameter("post_id");
			if (StringUtil.isEmpty(projectString) || StringUtil.isEmpty(content) || StringUtil.isEmpty(post_id)) {
				rs.setRES_CODE(Global.RESP_PARAM_NULL);
				rs.setRES_MESSAGE("信息不完整");
				return;
			}
			if (attachIds == null) {
				attachIds = "";
			}
			ProjectVO project = GsonUtil.getInstance().fromJson(projectString, ProjectVO.class);
			CommentVO comment = postService.insertComment(post_id, content, attachIds, project);
			if (comment == null) {
				rs.setRES_CODE(Global.RESP_ERROR);
				rs.setRES_MESSAGE("评论失败");
			} else {
				rs.setRES_CODE(Global.RESP_SUCCESS);
				rs.setRES_OBJ(GsonUtil.getInstance().toJson(comment, CommentVO.class));
				rs.setRES_MESSAGE("评论成功");
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
