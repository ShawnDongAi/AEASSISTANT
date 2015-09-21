package com.zzn.aenote.http.server;

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
 * 问件上传接口
 * 
 * @author Shawn
 *
 */
public class UpLoadFile extends CmHandlerFile {
	protected static final Logger logger = Logger.getLogger(UpLoadFile.class);
	private AttchService attchService;

	@Override
	public void handleFiles(List<String> filePaths, MultipartRequest req, BaseRep rs) throws Exception {
		try {
			if (filePaths.size() > 0 && filePaths.get(0) != null) {
				String filePath = filePaths.get(0).toString();
				AttchVO attchVO = attchService.insertAttch(AttchVO.getAttchType(filePath), "post", filePath);
				if (attchVO != null) {
					rs.setRES_CODE(Global.RESP_SUCCESS);
					rs.setRES_OBJ(GsonUtil.getInstance().toJson(attchVO, AttchVO.class));
					rs.setRES_MESSAGE("文件上传成功");
				} else {
					rs.setRES_CODE(Global.RESP_ERROR);
					rs.setRES_MESSAGE("文件上传失败,请重试");
				}
			} else {
				rs.setRES_CODE(Global.RESP_ERROR);
				rs.setRES_MESSAGE("文件上传失败,请重试");
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

	@Override
	public String filePath() {
		return UtilConfig.getString("file.post.savePath", "D://AENote-file//Server//post//");
	}
}
