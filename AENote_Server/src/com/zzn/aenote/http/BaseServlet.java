package com.zzn.aenote.http;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.server.CmHandler;
import com.zzn.aenote.http.utils.GsonUtil;
import com.zzn.aenote.http.vo.BaseRep;

public class BaseServlet extends HttpServlet {

	private static final long serialVersionUID = 800036632300601693L;

	private static final String END = "app";
	
	protected static final Logger logger = Logger
			.getLogger(BaseServlet.class);

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/plain;charset=UTF-8");
		String uri = req.getRequestURI();
		if (uri.endsWith(END)) {
			return;
		}
		if (!req.getMethod().equals("POST")) {
			return;
		}
		PrintWriter writer = resp.getWriter();
		BaseRep rs = new BaseRep();
		String clazz = uri.substring(uri.lastIndexOf("/") + 1);
		try {
			if (ServiceLocator.contains(clazz)) {
				CmHandler handler = (CmHandler) ServiceLocator.getBean(clazz);
				handler.doHandler(req, resp, rs);
			} else {
				logger.error("不存在bean名为[" + clazz + "].");
				rs.setRES_CODE(Global.RESP_NOT_FOUND);
				rs.setRES_OBJ(null);
				rs.setRES_MESSAGE("服务器出走了,请联系系统管理员");
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			rs.setRES_CODE(Global.RESP_FORBIDDEN);
			rs.setRES_MESSAGE("服务器内部异常");
			rs.setRES_OBJ(null);
		} finally {
			String jsonString = GsonUtil.getInstance().toJson(rs);
			writer.print(jsonString);
			writer.close();
		}
	}
}
