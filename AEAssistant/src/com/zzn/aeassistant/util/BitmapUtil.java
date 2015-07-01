package com.zzn.aeassistant.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtil {

	public static String zoomOutBitmap(String imgPath, float width, float height) {
		try {
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
			newOpts.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);// 此时返回bm为空
			newOpts.inJustDecodeBounds = false;
			int w = newOpts.outWidth;
			int h = newOpts.outHeight;
			// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
			float be = 1;// be=1表示不缩放
			if (w / width >= h / height && w > width) {// 如果宽度大的话根据宽度固定大小缩放
				be = newOpts.outWidth / width;
			} else if (w / width < h / height && h > height) {// 如果高度高的话根据宽度固定大小缩放
				be = newOpts.outHeight / height;
			}
			if (be <= 0) {
				be = 1;
			} else if (be % 1 > 0) {
				be += 1;
			}
			newOpts.inSampleSize = (int) be;// 设置缩放比例
			// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
			bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
			return compressBitmap(bitmap, imgPath);
		} catch (Exception e) {
			return imgPath;
		} catch (OutOfMemoryError e) {
			return imgPath;
		}
	}

	/**
	 * 对传入的图片进行压缩,至100k左右
	 * 
	 * @param image
	 * @param imgPath
	 * @return
	 */
	private static String compressBitmap(Bitmap image, String imgPath) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			int options = 100;
			while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
				baos.reset();// 重置baos即清空baos
				image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
				options -= 10;// 每次都减少10
			}
			image.recycle();
			image = null;
			ByteArrayInputStream isBm = new ByteArrayInputStream(
					baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
			isBm.reset();
			// 把压缩后的数据baos存放到ByteArrayInputStream中
			ByteArrayInputStream inputStream = new ByteArrayInputStream(
					baos.toByteArray());
			try {
				baos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			baos.reset();
			File imgFile = new File(imgPath);
			if (imgFile.exists()) {
				imgFile.delete();
			}
			int maxBufferSize = 16 * 1024;
			try {
				imgFile.createNewFile();
				FileOutputStream outStream = new FileOutputStream(imgFile);
				int bytesAvailable = inputStream.available();
				int bufferSize = Math.min(bytesAvailable, maxBufferSize);
				byte[] bufferByte = new byte[bufferSize];
				while ((inputStream.read(bufferByte)) != -1) {
					outStream.write(bufferByte, 0, bufferSize);
				}
				outStream.flush();
				outStream.close();
				inputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return imgPath;
		} catch (Exception e) {
			return imgPath;
		} catch (OutOfMemoryError e) {
			return imgPath;
		}
	}
}
