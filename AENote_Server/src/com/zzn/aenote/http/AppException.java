package com.zzn.aenote.http;

/**
 * <pre>
 * 功能说明
 * </pre>
 * 
 * <br>
 * JDK版本:1.4 或更高
 * 
 * @author lzz
 * @version 1.0
 * @since 1.0
 */

public class AppException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String message;

	public AppException() {
		super();
		this.message = "不明错误.";
	}

	public AppException(Throwable cause) {
		super(cause);
		this.message = "不明错误.";
	}

	public AppException(String message) {
		super(message);
		this.message = message;
	}

	public AppException(String message, Throwable cause) {
		super(message, cause);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
