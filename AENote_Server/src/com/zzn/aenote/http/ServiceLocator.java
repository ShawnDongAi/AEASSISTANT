package com.zzn.aenote.http;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceLocator {
	private static ServiceLocator selfLocator = null;

	private static ApplicationContext ContextFactory = null;

	private static final Object synObject = new Object();

	private ServiceLocator(ApplicationContext ctx) {
		ContextFactory = ctx;
	}

	public static void init(ApplicationContext ctx) {
		if (selfLocator == null) {
			synchronized (synObject) {
				if (selfLocator == null) {
					selfLocator = new ServiceLocator(ctx);
				}
			}
		}
	}

	public static void destroyed() {
		selfLocator = null;
	}

	public static boolean contains(String name) {
		return ContextFactory.containsBean(name);
	}

	public static Object getBean(String name) {
		if (selfLocator == null) {
			throw new AppException("ServiceLocator没有被初始化，ApplicationContext为空");
		}
		return ContextFactory.getBean(name);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean2(String name) {
		if (selfLocator == null) {
			throw new AppException("ServiceLocator没有被初始化，ApplicationContext为空");
		}
		return (T) ContextFactory.getBean(name);
	}

	public static void refresh() {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				new String[] { "/resource/applicationContext.xml" });
		ContextFactory = appContext;

	}

}
