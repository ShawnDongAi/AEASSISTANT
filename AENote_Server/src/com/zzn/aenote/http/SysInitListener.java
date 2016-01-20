package com.zzn.aenote.http;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.zzn.aenote.http.push.PushServer;
import com.zzn.aenote.http.sqlmap.SqlMapCache;

public class SysInitListener implements ServletContextListener {
	private static final Logger logger = Logger
			.getLogger(SysInitListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		sce = null;
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("==============开始启动系统===================");
		logger.info("应用名称:工程笔记-服务端");
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(sce.getServletContext());
		// 加载根路径
		ServiceLocator.init(ctx);
		SqlMapCache.getSqlMapCache().reflash();
//		PushServer.push();
	}
}
