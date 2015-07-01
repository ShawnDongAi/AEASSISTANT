package com.zzn.aeassistant.constants;

/**
 * url接口
 * 
 * @author Shawn
 */
public class URLConstants {
//	public static final String URL_HOST = "http://192.168.0.105:8080/AENote_Server/app/";
	 public static final String URL_HOST =
	 "http://112.124.16.245:8080/AENote_Server/app/";
	/**
	 * 注册接口
	 */
	public static final String URL_REGISTER = URL_HOST + "register";
	/**
	 * 登陆接口
	 */
	public static final String URL_LOGIN = URL_HOST + "login";
	/**
	 * 短信验证码验证
	 */
	public static final String URL_VERIFY = URL_HOST + "verifySmsCode";
	/**
	 * 修改密码
	 */
	public static final String URL_RESET_PSW = URL_HOST + "resetPassword";
	/**
	 * 修改用户头像
	 */
	public static final String URL_UPDATE_HEAD = URL_HOST + "updateHead";
	/**
	 * 创建项目
	 */
	public static final String URL_CREATE_PROJECT = URL_HOST + "createProject";
	/**
	 * 创建子项目接口
	 */
	public static final String URL_CREATE_LEAF_PROJECT = URL_HOST
			+ "createLeafProject";
	/**
	 * 查询管理项目列表
	 */
	public static final String URL_PROJECT_MANAGER_LIST = URL_HOST
			+ "queryProjectList";
	/**
	 * 查询项目详情
	 */
	public static final String URL_PROJECT_DETAIL = URL_HOST
			+ "queryProjectDetail";
	/**
	 * 打卡接口
	 */
	public static final String URL_SCANNING = URL_HOST + "scanning";
	/**
	 * 更新上级项目
	 */
	public static final String URL_UPDATE_PARENT = URL_HOST + "joinProject";
	/**
	 * 删除项目
	 */
	public static final String URL_DELETE_PRJECT = URL_HOST + "deleteProject";
}
