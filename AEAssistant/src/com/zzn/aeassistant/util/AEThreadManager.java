package com.zzn.aeassistant.util;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

public class AEThreadManager {
	private ThreadPoolExecutor poolExecutor;
	private int corePoolSize = 2;
	private int maximumPoolSize = 5;
	private int keepAliveTime = 0;
	private Queue<Runnable> runnables = new LinkedList<Runnable>();
	private ScheduledExecutorService scheduledExecutorService;
	private static AEThreadManager instance;

	private final RejectedExecutionHandler defaultHandler = new AbortPolicy() {

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
			try {
				if (r != null) {
					runnables.offer(r);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

		}

	};
	private Runnable reloadCommand = new Runnable() {

		@Override
		public void run() {
			if (runnables.size() > 0) {
				Runnable runnable = runnables.poll();
				poolExecutor.execute(runnable);
			}
		}
	};

	private AEThreadManager() {
		poolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
				keepAliveTime, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(10), defaultHandler);
		scheduledExecutorService = Executors.newScheduledThreadPool(2);
		scheduledExecutorService.scheduleAtFixedRate(reloadCommand, 0, 1,
				TimeUnit.SECONDS);
		instance = this;
	}

	/**
	 * 获取线程池实例
	 * 
	 * @return
	 */
	public static AEThreadManager getInstance() {
		if (instance == null) {
			new AEThreadManager();
		}
		return instance;
	}

	/**
	 * 往线程池中添加runnable
	 * 
	 * @param runnable
	 */
	public void addThread(Runnable runnable) {
		poolExecutor.execute(runnable);
	}

	/**
	 * 从线程池中移除runnable
	 * 
	 * @param runnable
	 */
	public void removeThread(Runnable runnable) {
		if (runnable != null) {
			poolExecutor.remove(runnable);
		}
	}

	/**
	 * 判断当前线程池是否含有此runnable对象
	 * 
	 * @param runnable
	 * @return
	 */
	public boolean hasThread(Runnable runnable) {
		if (runnables.contains(runnable)
				|| poolExecutor.getQueue().contains(runnable)) {
			return true;
		}
		return false;
	}
}
