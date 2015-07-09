package com.zzn.aenote.http.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.oreilly.servlet.MultipartRequest;
import com.zzn.aenote.http.file.FilePolicy;
import com.zzn.aenote.http.utils.FilePathUtil;
import com.zzn.aenote.http.vo.BaseRep;

public abstract class CmHandlerFile implements CmHandler {

	protected static final Logger logger = Logger
			.getLogger(CmHandlerFile.class);

	private FilePolicy filePolicy;

	private String charset;

	private int maxSize;

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setFilePolicy(FilePolicy filePolicy) {
		this.filePolicy = filePolicy;
	}

	public FilePolicy getFilePolicy() {
		return filePolicy;
	}

	public abstract String filePath();

	@SuppressWarnings("rawtypes")
	public void doHandler(HttpServletRequest req, HttpServletResponse resp,
			BaseRep rs) throws Exception {
		logger.info("接收到请求，带附件....................");
		try {
			FilePathUtil.createDirectory(filePath());
			MultipartRequest multi = new MultipartRequest(req, filePath(),
					maxSize, charset, filePolicy);
			Enumeration files = multi.getFileNames();
			List<String> filePaths = new ArrayList<String>();
			while (files.hasMoreElements()) {
				String name = (String) files.nextElement();
				File f = multi.getFile(name);
				filePaths.add(f.getPath());
				logger.info("添加附件文件:" + f.getName() + ",name:" + name);
			}
			handleFiles(filePaths, multi, rs);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("接收文件失败.", e);
			throw new Exception(e);
		}
	}

	/**
	 * 处理文件,接收参数<br>
	 * filePaths为接收的文件路径<br>
	 * req.multi.getParameter可以获取客户端参数<br>
	 * rs返回给客户端的参数
	 */
	public abstract void handleFiles(List<String> filePaths,
			MultipartRequest req, BaseRep rs) throws Exception;
}
