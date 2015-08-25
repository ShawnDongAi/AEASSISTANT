package com.zzn.aenote.http;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.zzn.aenote.http.sqlmap.SqlMapTemplate;
import com.zzn.aenote.http.utils.StringUtil;

public class DownLoad extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(DownLoad.class);

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		String platform = req.getParameter("platform");
		logger.info("下载安装文件===>"+platform);
		// 提供HTTP文件下载
		try {
			java.io.OutputStream os = resp.getOutputStream();
			if (platform != null && !StringUtil.isEmpty(platform)) {
				JdbcTemplate jdbcTemplate = ServiceLocator.getBean2("jdbcTemplate");
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("platform", platform);
				String sql = SqlMapTemplate.convertToSQL("download", data);
				List<Map<String, Object>> files = jdbcTemplate.queryForList(sql);
				if (files != null && files.size() > 0) {
					String filePath = files.get(0).get("url").toString();
					logger.info("下载文件===>"+filePath);
					if (!StringUtil.isEmpty(filePath)) {
						File file = new File(filePath);
						if (file.exists()) {
							resp.setContentType("application/x-download");//
							resp.setContentType("application/vnd.android.package-archive");
							resp.addHeader("Content-Disposition", "attachment;filename="+file.getName());
							resp.setContentLength((int) file.length());
							java.io.FileInputStream fis = new java.io.FileInputStream(filePath);
							byte[] b = new byte[1024];
							int i = 0;
							while ((i = fis.read(b)) > 0) {
								os.write(b, 0, i);
							}
							fis.close();
						}
					}
				}
			}
			os.flush();
			os.close();
		} catch (Exception e) {
		}
	}
}
