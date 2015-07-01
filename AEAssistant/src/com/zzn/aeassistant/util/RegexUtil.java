package com.zzn.aeassistant.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

	/**
	 * 判断是否电话号码
	 * @param input
	 * @return
	 */
	public static boolean isPhoneNum(String input){
		String reg="(?<!\\d)(?:(?:1[358]\\d{9})|(?:\\d{7,8})|(?:0[1-9]\\d{1,2}-?\\d{7,8}))(?!\\d)";
		Pattern pattern=Pattern.compile(reg); 
		Matcher matcher=pattern.matcher(input);
		return matcher.find();
	}
}
