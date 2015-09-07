package com.zzn.aenote.http.server.post;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.oreilly.servlet.MultipartRequest;
import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.server.CmHandlerFile;
import com.zzn.aenote.http.service.AttchService;
import com.zzn.aenote.http.service.PostService;
import com.zzn.aenote.http.utils.GsonUtil;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.utils.UtilConfig;
import com.zzn.aenote.http.vo.AttchVO;
import com.zzn.aenote.http.vo.BaseRep;
import com.zzn.aenote.http.vo.PostVO;
import com.zzn.aenote.http.vo.ProjectVO;

public class PostFile extends CmHandlerFile {
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	private AttchService attchService;
	private PostService postService;

	@Override
	public String filePath() {
		Date now = new Date(System.currentTimeMillis());
		String today = format.format(now);
		String end = today + "//";
		return UtilConfig.getString("file.post.savePath" + end,
				"D://AENote-file//Server//post//" + end);
	}

	@Override
	public void handleFiles(List<String> filePaths, MultipartRequest req,
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
			if (filePaths.size() > 0) {
				StringBuilder attachIDs = new StringBuilder();
				for (String path : filePaths) {
					AttchVO attch = attchService.insertAttch(
							AttchVO.TYPE_IMG,
							project.getROOT_PROJECT_NAME()
									+ "_"
									+ format.format(new Date(System
											.currentTimeMillis())), path);
					attachIDs.append(attch.getATTCH_ID() + "#");
				}
				if (attachIDs.length() > 0) {
					attachIDs.deleteCharAt(attachIDs.length() - 1);
				}
				PostVO post = postService.insertPost(content,
						attachIDs.toString(), project, is_private,
						sendProjectVOs);
				if (post == null) {
					rs.setRES_CODE(Global.USER_ERROR);
					rs.setRES_MESSAGE("发帖失败");
				} else {
					rs.setRES_CODE(Global.RESP_SUCCESS);
					rs.setRES_OBJ(GsonUtil.getInstance().toJson(post,
							PostVO.class));
					rs.setRES_MESSAGE("发帖成功");
				}
			} else {
				logger.info("文件上传失败");
				rs.setRES_CODE(Global.FILE_UPLOAD_FAILED);
				rs.setRES_MESSAGE("照片上传失败,请重试");
			}
		} catch (Exception e) {
			e.printStackTrace();
			rs.setRES_CODE(Global.RESP_ERROR);
			rs.setRES_MESSAGE("服务器异常,请重试");
		}
	}

	public void setAttchService(AttchService attchService) {
		this.attchService = attchService;
	}

	public void setPostService(PostService postService) {
		this.postService = postService;
	}
}
