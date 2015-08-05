package com.zzn.aenote.http;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.zzn.aenote.http.sqlmap.SqlMapTemplate;
import com.zzn.aenote.http.utils.StringUtil;

public class ImgServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ImgServlet.class);

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		String attch_id = req.getParameter("attch");
		resp.setContentType("image/jpeg");
		// 提供图片在线预览
		try {
			if (attch_id != null && !StringUtil.isEmpty(attch_id)) {
				JdbcTemplate jdbcTemplate = ServiceLocator.getBean2("jdbcTemplate");
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("attch_id", attch_id);
				String sql = SqlMapTemplate.convertToSQL("query_attch", data);
				List<Map<String, Object>> attchs = jdbcTemplate.queryForList(sql);
				if (attchs != null && attchs.size() > 0) {
					String filePath = attchs.get(0).get("url").toString();
					if (!StringUtil.isEmpty(filePath)) {
						File file = new File(filePath);
						if (file.exists()) {
							ServletOutputStream out = resp.getOutputStream();
							BufferedImage image = ImageIO.read(file);
//							Graphics graphics = image.getGraphics();
//							graphics.setColor(Color.green);
//							graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
//							graphics.setColor(Color.yellow);
//							graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
							JPEGImageEncoder encoder = JPEGCodec
									.createJPEGEncoder(out);
							encoder.encode(image);
							out.close(); 
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}
}
