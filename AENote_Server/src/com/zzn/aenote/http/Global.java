package com.zzn.aenote.http;

public class Global {

	/** 统一响应代码定义* */
	public static final String RESP_SUCCESS = "200"; // 成功
	public static final String RESP_NOT_FOUND = "404"; // 找不到服务
	public static final String RESP_FORBIDDEN = "403"; // 禁止访问
	public static final String RESP_ERROR = "401"; // 服务器异常
	public static final String RESP_PARAM_NULL = "405"; // 缺少参数
	public static final String RESP_PARAM_ERROR = "406"; // 参数错误

	/** 业务响应代码定义* */
	public static final String USER_ERROR = "901"; // 用户名或密码错误
	public static final String USER_PSW_NULL = "902"; // 登陆账号或密码为空
	public static final String USER_EXIST = "903"; // 用户已存在
	public static final String USER_NOT_EXIST = "904"; // 用户不存在
	public static final String REGISTER_ERROR = "905"; // 注册失败
	public static final String USER_ID_NULL = "906"; // 用户id未上传
	public static final String USER_HEAD_FAILED = "907"; // 用户头像更新失败
	public static final String SMSCODE_VERIFY_ERROR = "908"; //短信验证码验证失败
	public static final String RESET_PSW_FAILED = "909"; // 修改密码失败
	
	public static final String FILE_UPLOAD_FAILED = "501"; //文件上传失败
	public static final String ORACLE_ERROR = "502"; // 数据库操作异常
	
	public static final String PROJECT_NULL_PARAMS = "601"; // 创建分组信息不全
	public static final String PROJECT_CREATE_FAILED = "602"; // 项目创建失败
	public static final String ADDRESS_NULL = "603"; // 缺少位置信息
	public static final String PROJECT_NULL = "604"; // 当前未加入任何项目
}
