package com.zzn.aenote.http.server.post;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.server.CmHandler;
import com.zzn.aenote.http.service.PostService;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.vo.BaseRep;
import com.zzn.aenote.http.vo.PostVO;

public class QueryPost implements CmHandler {
	protected static final Logger logger = Logger.getLogger(QueryPost.class);
	private PostService postService;

	@Override
	public void doHandler(HttpServletRequest req, HttpServletResponse resp, BaseRep rs) throws Exception {
		try {
			String project_id = req.getParameter("project_id");
			if (StringUtil.isEmpty(project_id)) {
				rs.setRES_CODE(Global.RESP_PARAM_NULL);
				rs.setRES_MESSAGE("缺少项目信息");
				return;
			}
			List<PostVO> postList = postService.queryPost(project_id);
			for (PostVO post : postList) {
				
			}
			rs.setRES_CODE(Global.RESP_SUCCESS);
			rs.setRES_MESSAGE("查询成功");
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