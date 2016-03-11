package com.zzn.aeassistant.util;

import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class ToolsUtil {
	
	/**
	 * 隐藏软键盘
	 * @param context
	 * @param view
	 */
	public static void hideInputManager(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	/**
	 * dp换算成像素
	 * 
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 像素换算成dp
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 根据经纬度坐标计算两点间的距离
	 * 
	 * @param lon1
	 * @param lat1
	 * @param lon2
	 * @param lat2
	 * @return
	 */
	public static double getDistance(double lon1, double lat1, double lon2,
			double lat2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lon1) - rad(lon2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	private static final double EARTH_RADIUS = 6378137;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	public static void setTextMaxLength(EditText view, final int length) {
		InputFilter inputFilter = new InputFilter() {
			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				try {
					String charsetName = "UTF-8";
					// 转换成中文字符集的长度
					int destLen = dest.toString().getBytes(charsetName).length;
					int sourceLen = source.toString().getBytes(charsetName).length;
					// 如果超过length个字符
					if (destLen + sourceLen > length) {
						return "";
					}
					// 如果按回退键
					if (source.length() < 1 && (dend - dstart >= 1)) {
						return dest.subSequence(dstart, dend - 1);
					}
					// 其他情况直接返回输入的内容
					return source;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return "";
			}
		};
		InputFilter[] inputFilters = new InputFilter[] { inputFilter };
		view.setFilters(inputFilters);
	}
}
