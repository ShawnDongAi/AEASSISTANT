package com.zzn.aeassistant.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.zzn.aeassistant.R;

public class BitmapUtil {
	// 图片宽度的一般
	private static final int IMAGE_HALFWIDTH = 20;
	// 前景色
	private static int FOREGROUND_COLOR = 0xff000000;
	// 背景色
	private static int BACKGROUND_COLOR = 0xffffffff;

	public static Bitmap zoomBitmap(Drawable iconSource, int h) {
		Bitmap icon = drawable2Bitmap(iconSource);
		// 缩放图片
		Matrix m = new Matrix();
		float sx = (float) 2 * h / icon.getWidth();
		float sy = (float) 2 * h / icon.getHeight();
		m.setScale(sx, sy);
		// 重新构造一个2h*2h的图片
		return Bitmap.createBitmap(icon, 0, 0, icon.getWidth(), icon.getHeight(), m, false);
	}

	public static Bitmap drawable2Bitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		} else if (drawable instanceof NinePatchDrawable) {
			Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
					drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			drawable.draw(canvas);
			return bitmap;
		} else {
			return null;
		}
	}

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
			ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
			isBm.reset();
			// 把压缩后的数据baos存放到ByteArrayInputStream中
			ByteArrayInputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
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

	public static Bitmap cretaeTwoCode(Context context, String str, int drawableID) throws WriterException {
		// 缩放一个40*40的图片
		Bitmap icon = zoomBitmap(context.getResources().getDrawable(drawableID), IMAGE_HALFWIDTH);
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.MARGIN, 1);
		// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 300, 300, hints);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		// 二维矩阵转为一维像素数组,也就是一直横着排了
		int halfW = width / 2;
		int halfH = height / 2;
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (x > halfW - IMAGE_HALFWIDTH && x < halfW + IMAGE_HALFWIDTH && y > halfH - IMAGE_HALFWIDTH
						&& y < halfH + IMAGE_HALFWIDTH) {
					pixels[y * width + x] = icon.getPixel(x - halfW + IMAGE_HALFWIDTH, y - halfH + IMAGE_HALFWIDTH);
				} else {
					if (matrix.get(x, y)) {
						pixels[y * width + x] = FOREGROUND_COLOR;
					} else { // 无信息设置像素点为白色
						pixels[y * width + x] = BACKGROUND_COLOR;
					}
				}

			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		// 通过像素数组生成bitmap
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	public static Bitmap cretaeTwoCode(Context context, String str, Drawable drawable) throws WriterException {
		// 缩放一个40*40的图片
		if (drawable == null) {
			drawable = context.getResources().getDrawable(R.drawable.ic_head);
		}
		Bitmap icon = zoomBitmap(drawable, IMAGE_HALFWIDTH);
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.MARGIN, 1);
		// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 300, 300, hints);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		// 二维矩阵转为一维像素数组,也就是一直横着排了
		int halfW = width / 2;
		int halfH = height / 2;
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (x > halfW - IMAGE_HALFWIDTH && x < halfW + IMAGE_HALFWIDTH && y > halfH - IMAGE_HALFWIDTH
						&& y < halfH + IMAGE_HALFWIDTH) {
					pixels[y * width + x] = icon.getPixel(x - halfW + IMAGE_HALFWIDTH, y - halfH + IMAGE_HALFWIDTH);
				} else {
					if (matrix.get(x, y)) {
						pixels[y * width + x] = FOREGROUND_COLOR;
					} else { // 无信息设置像素点为白色
						pixels[y * width + x] = BACKGROUND_COLOR;
					}
				}

			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		// 通过像素数组生成bitmap
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	public static boolean writeToSdcard(Bitmap bitmap, String path, String fileName) {
		ByteArrayOutputStream by = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, by);
		byte[] data = by.toByteArray();
		FileOutputStream fos = null;
		try {
			// 判断有没有文件夹
			File filePath = new File(path);
			if (!filePath.exists()) {
				// 创建文件夹
				filePath.mkdirs();
			}
			// 判断有没有同名的文件
			File file = new File(path + fileName);
			// 有的话，删除
			if (file.exists()) {
				file.delete();
			}
			// 写文件
			fos = new FileOutputStream(file);
			fos.write(data);
			fos.flush();
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
