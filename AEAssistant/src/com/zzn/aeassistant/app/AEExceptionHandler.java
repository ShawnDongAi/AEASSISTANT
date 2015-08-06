package com.zzn.aeassistant.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.Date;

import android.os.Environment;
import android.text.format.DateFormat;

import com.zzn.aeassistant.R;
import com.zzn.aeassistant.constants.FileCostants;
import com.zzn.aeassistant.util.PhoneUtil;
import com.zzn.aeassistant.util.ToastUtil;

/**
 * 全局异常捕获类
 * 
 * @author Shawn
 */
public class AEExceptionHandler implements UncaughtExceptionHandler {
	private UncaughtExceptionHandler exceptionHandler;

	public AEExceptionHandler() {
		exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		ToastUtil.show(R.string.app_error);
		try {
			Runtime.getRuntime().exec(
					new String[] { "logcat", "-d", "-v", "time", "-f",
							FileCostants.DIR_BASE + "aeassistant_log.txt",
							"*:W" });
		} catch (IOException e) {
			e.printStackTrace();
		}
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		writeToFile(
				writer.toString() + "\n" + thread.getName() + "\n"
						+ thread.toString(), FileCostants.DIR_BASE
						+ "aeassistant_trace.txt");
		exceptionHandler.uncaughtException(thread, ex);
	}

	private void writeToFile(String stacktrace, String filename) {
		try {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_UNMOUNTED)) {
				return;
			}
			File targetFile = new File(filename);
			File fileDir = new File(FileCostants.DIR_BASE);
			if (targetFile.exists()) {
				File newPath = new File(FileCostants.DIR_BASE
						+ "/aeassistant_trace" + (fileDir.list().length + 1)
						+ ".txt");
				targetFile.renameTo(newPath);
			}
			FileWriter fileWriter = new FileWriter(targetFile);
			BufferedWriter bos = new BufferedWriter(fileWriter);
			bos.write(android.os.Build.VERSION.SDK_INT + "\n");
			bos.write(android.os.Build.MODEL + "\n");
			bos.write("APP :" + PhoneUtil.getAppVersionName() + "\n");
			bos.write("time :"
					+ DateFormat.format("yyyy-MM-dd kk:mm:ss",
							new Date(System.currentTimeMillis())) + "\n");
			bos.write(stacktrace);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
