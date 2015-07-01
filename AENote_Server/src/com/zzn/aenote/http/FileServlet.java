package com.zzn.aenote.http;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.utils.UtilConfig;

public class FileServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(FileServlet.class);

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		resp.setContentType("application/x-download");//
		resp.addHeader("Content-Disposition", "attachment;filename=update.zip");
		String file = "";
		if (req.getParameter("apkPath") != null) {
			file = req.getParameter("apkPath");
		}
		;
		String filePath = req.getParameter("filePath");
		// 提供HTTP文件下载
		try {
			java.io.OutputStream os = resp.getOutputStream();
			if (!file.equals("")) {
				String path = UtilConfig.getString("file.apkPath",
						"D://AENote-file//Server//apk//") + file;
				if (filePath != null && !filePath.equals("null")
						&& !filePath.equals("")) {
					path = filePath;
				}
				resp.setContentLength((int) new File(path).length());
				java.io.FileInputStream fis = new java.io.FileInputStream(path);
				byte[] b = new byte[1024];
				int i = 0;
				while ((i = fis.read(b)) > 0) {
					os.write(b, 0, i);
				}
				fis.close();
			} else {
				String path = "";
				if (filePath != null && !filePath.equals("null")
						&& !filePath.equals("")) {
					path = filePath;
				}
				resp.setContentLength((int) new File(path).length());
				java.io.FileInputStream fis = new java.io.FileInputStream(path);
				byte[] b = new byte[1024];
				int i = 0;
				while ((i = fis.read(b)) > 0) {
					os.write(b, 0, i);
				}
				fis.close();
			}
			os.flush();
			os.close();
		} catch (Exception e) {
		}
	}
}
