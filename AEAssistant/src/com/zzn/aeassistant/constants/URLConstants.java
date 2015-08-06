package com.zzn.aeassistant.constants;

/**
 * url接口
 * 
 * @author Shawn
 */
public class URLConstants {
	//个人电脑
//	public static final String URL_BASE = "http://192.168.0.105:8080/AENote_Server/";
	//服务器
	public static final String URL_BASE = "http://112.124.16.245:8080/AENote_Server/";
	//公司
//	public static final String URL_BASE = "http://192.168.3.125:8080/AENote_Server/";
	public static final String URL_HOST = URL_BASE + "app/";
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
	 * 根据用户ID查询用户资料
	 */
	public static final String URL_QUERY_USER_BY_ID = URL_HOST + "queryUserByID";
	/**
	 * 修改用户头像
	 */
	public static final String URL_UPDATE_HEAD = URL_HOST + "updateHead";
	/**
	 * 修改用户姓名
	 */
	public static final String URL_UPDATE_NAME = URL_HOST + "updateName";
	/**
	 * 修改个人说明
	 */
	public static final String URL_UPDATE_REMARK = URL_HOST + "updateRemark";
	/**
	 * 修改身份证号
	 */
	public static final String URL_UPDATE_IDCARD = URL_HOST + "updateIDCard";
	/**
	 * 修改身份证照
	 */
	public static final String URL_UPDATE_IDCARD_IMG = URL_HOST + "updateIDCardImg";
	/**
	 * 创建项目
	 */
	public static final String URL_CREATE_PROJECT = URL_HOST + "createProject";
	/**
	 * 更新项目名
	 */
	public static final String URL_UPDATE_PROJECT_NAME = URL_HOST + "updateProjectName";
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
	 * 查询项目组织架构
	 */
	public static final String URL_PROJECT_STRUCTURE = URL_HOST
			+ "queryProjectStructure";
	/**
	 * 查询项目成员
	 */
	public static final String URL_PROJECT_USERS = URL_HOST
			+ "queryProjectUsers";
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
	/**
	 * 根据项目查询考勤记录
	 */
	public static final String URL_SUM_BY_PROJECT = URL_HOST + "sumCountByProject";
	/**
	 * 根据项目查询考勤记录
	 */
	public static final String URL_SUM_LIST_BY_PROJECT = URL_HOST + "sumListByProject";
	/**
	 * 根据用户查询考勤记录
	 */
	public static final String URL_SUM_BY_USERS = URL_HOST + "sumListByUser";
	/**
	 * 文件下载
	 */
	public static final String URL_DOWNLOAD = URL_BASE + "file?attch=%s";
	/**
	 * 用户反馈
	 */
	public static final String URL_FEEDBACK = URL_HOST + "feedback";
	/**
	 * 版本更新
	 */
	public static final String URL_VERSION_UPDATE = URL_HOST + "versionUpdate";
	/**
	 * 安卓最新版本下载地址
	 */
	public static final String URL_APK_DOWNLOAD = URL_BASE + "download?platform=0";
}
