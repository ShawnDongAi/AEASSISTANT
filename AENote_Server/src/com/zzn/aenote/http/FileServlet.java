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
		String attch_id = req.getParameter("attch");
		logger.info("下载===>"+attch_id);
		// 提供HTTP文件下载
		try {
			java.io.OutputStream os = resp.getOutputStream();
			if (!attch_id.equals("")) {
				JdbcTemplate jdbcTemplate = ServiceLocator.getBean2("jdbcTemplate");
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("attch_id", attch_id);
				String sql = SqlMapTemplate.convertToSQL("query_attch", data);
				List<Map<String, Object>> attchs = jdbcTemplate.queryForList(sql);
				if (attchs != null && attchs.size() > 0) {
					String filePath = attchs.get(0).get("url").toString();
					logger.info("下载文件===>"+filePath);
					if (!StringUtil.isEmpty(filePath)) {
						File file = new File(filePath);
						if (file.exists()) {
							resp.setContentType("application/x-download");//
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
