package com.zzn.aenote.http.server.user;

import java.util.List;

import org.apache.log4j.Logger;

import com.oreilly.servlet.MultipartRequest;
import com.zzn.aenote.http.Global;
import com.zzn.aenote.http.server.CmHandlerFile;
import com.zzn.aenote.http.service.AttchService;
import com.zzn.aenote.http.service.UserService;
import com.zzn.aenote.http.utils.GsonUtil;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.utils.UtilConfig;
import com.zzn.aenote.http.vo.AttchVO;
import com.zzn.aenote.http.vo.BaseRep;

/**
 * 头像更新接口
 * 
 * @author Shawn
 *
 */
public class UpdateHead extends CmHandlerFile {
	protected static final Logger logger = Logger.getLogger(UpdateHead.class);
	private UserService userService;
	private AttchService attchService;

	@Override
	public void handleFiles(List<String> filePaths, MultipartRequest req,
			BaseRep rs) throws Exception {
		try {
			String userID = req.getParameter("user_id");
			if (StringUtil.isEmpty(userID)) {
				logger.info("缺少用户ID");
				rs.setRES_CODE(Global.USER_ID_NULL);
				rs.setRES_MESSAGE("请先登录");
				return;
			}
			if (filePaths.size() > 0 && filePaths.get(0) != null) {
				String big_head = filePaths.get(0).toString();
				AttchVO bigAttch = attchService.insertAttch(AttchVO.TYPE_HEAD,
						"head", big_head);
				boolean result = true;
				result = userService.updateUserHead(bigAttch.getATTCH_ID(), bigAttch.getATTCH_ID(), userID);
				if (result) {
					rs.setRES_CODE(Global.RESP_SUCCESS);
					rs.setRES_OBJ(GsonUtil.getInstance().toJson(bigAttch,
							AttchVO.class));
					rs.setRES_MESSAGE("上传头像成功");
				} else {
					rs.setRES_CODE(Global.USER_HEAD_FAILED);
					rs.setRES_MESSAGE("头像更新失败,请重试");
				}
			} else {
				logger.info("文件上传失败");
				rs.setRES_CODE(Global.FILE_UPLOAD_FAILED);
				rs.setRES_MESSAGE("头像更新失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			rs.setRES_CODE(Global.RESP_ERROR);
			rs.setRES_MESSAGE("服务器异常,请重试");
		}
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setAttchService(AttchService attchService) {
		this.attchService = attchService;
	}

	@Override
	public String filePath() {
		return UtilConfig.getString("file.head.savePath",
				"D://AENote-file//Server//head//");
	}
}
