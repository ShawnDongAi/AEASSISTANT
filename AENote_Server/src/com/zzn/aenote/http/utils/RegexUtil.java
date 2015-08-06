package com.zzn.aenote.http.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

	/**
	 * 判断是否电话号码
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isPhoneNum(String input) {
		String reg = "^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(input);
		return matcher.find();
	}

	/**
	 * 判断是否身份证号码
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isIDCard(String input) {
		String reg15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
		String reg18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$";
		Pattern pattern15 = Pattern.compile(reg15);
		Matcher matcher15 = pattern15.matcher(input);
		if (matcher15.find()) {
			return true;
		}
		Pattern pattern18 = Pattern.compile(reg18);
		Matcher matcher18 = pattern18.matcher(input);
		return matcher18.find();
	}
}
