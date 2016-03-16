package com.zzn.aenote.http.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.BaseService;
import com.zzn.aenote.http.utils.MD5Utils;
import com.zzn.aenote.http.utils.UtilUniqueKey;
import com.zzn.aenote.http.vo.UserVO;

/**
 * 用户相关信息操作接口
 * 
 * @author Shawn
 */
public class UserService extends BaseService {
	private static final Logger logger = Logger.getLogger(UserService.class);
	private static SimpleDateFormat fromat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	/**
	 * 注册
	 * 
	 * @param userName
	 * @param phone
	 * @param sex
	 * @param password
	 * @return null表示注册失败,否则返回用户id
	 */
	public UserVO register(String phone, String password) {
		try {
			UserVO user = new UserVO();
			Map<String, Object> data = new HashMap<String, Object>();
			String userID = UtilUniqueKey.getKey(phone);
			data.put("user_id", userID);
			user.setUSER_ID(userID);
			data.put("user_name", phone);
			user.setUSER_NAME(phone);
			data.put("phone", phone);
			user.setPHONE(phone);
			data.put("sex", "0");
			user.setSEX("0");
			data.put("password", password);
			String date = fromat.format(new Date(System.currentTimeMillis()));
			data.put("create_time", date);
			data.put("time", date);
			user.setCREATE_TIME(date);
			getJdbc().execute(getSql("insert_user_info", data));
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 注册
	 * @param phone
	 * @param name
	 * @param password
	 * @return
	 */
	public UserVO register(String phone, String name, String password) {
		try {
			UserVO user = new UserVO();
			Map<String, Object> data = new HashMap<String, Object>();
			String userID = UtilUniqueKey.getKey(phone);
			data.put("user_id", userID);
			user.setUSER_ID(userID);
			data.put("user_name", name);
			user.setUSER_NAME(name);
			data.put("phone", phone);
			user.setPHONE(phone);
			data.put("sex", "0");
			user.setSEX("0");
			data.put("password", password);
			String date = fromat.format(new Date(System.currentTimeMillis()));
			data.put("create_time", date);
			data.put("time", date);
			user.setCREATE_TIME(date);
			getJdbc().execute(getSql("insert_user_info", data));
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 校验用户密码
	 * 
	 * @param phone
	 * @param password
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryUser(String phone, String password) {
		password = MD5Utils.getMD5ofStr(password);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("phone", phone);
		data.put("password", password);
		List<Map<String, Object>> userList = getJdbc().queryForList(
				getSql("valid_user_info", data));
		return userList;
	}

	/**
	 * 根据用户id检索用户
	 * 
	 * @param userID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryUserByID(String userID) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("user_id", userID);
		List<Map<String, Object>> userList = getJdbc().queryForList(
				getSql("query_user_by_id", data));
		return userList;
	}

	/**
	 * 根据用户手机检索用户
	 * 
	 * @param phone
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryUserByPhone(String phone) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("phone", phone);
		List<Map<String, Object>> userList = getJdbc().queryForList(
				getSql("query_user_by_phone", data));
		return userList;
	}

	/**
	 * 修改用户头像
	 * 
	 * @param small_head
	 * @param big_head
	 * @return
	 */
	public boolean updateUserHead(String small_head, String big_head,
			String user_id) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("small_head", small_head);
			data.put("big_head", big_head);
			data.put("user_id", user_id);
			getJdbc().execute(getSql("update_user_head", data));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public void updateLoginTime(String user_id) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("user_id", user_id);
			String date = fromat.format(new Date(System.currentTimeMillis()));
			data.put("time", date);
			getJdbc().execute(getSql("update_login_time", data));
		} catch (Exception e) {
		}
	}

	/**
	 * 修改身份证照
	 * 
	 * @param img
	 * @param imgType
	 * @param user_id
	 * @return
	 */
	public boolean updateIDCardImg(String img, int imgType, String user_id) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("img", img);
			data.put("user_id", user_id);
			switch (imgType) {
			case 0:
				getJdbc().execute(getSql("update_idcard_front", data));
				break;
			case 1:
				getJdbc().execute(getSql("update_idcard_back", data));
				break;
			case 2:
				getJdbc().execute(getSql("update_idcard_hand", data));
				break;
			default:
				break;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean updateUserName(String user_id, String user_name) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("user_id", user_id);
			data.put("user_name", user_name);
			getJdbc().execute(getSql("update_user_name", data));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean updateUserRemark(String user_id, String remark) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("user_id", user_id);
			data.put("remark", remark);
			getJdbc().execute(getSql("update_user_remark", data));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean updateUserIDCard(String user_id, String idcard) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("user_id", user_id);
			data.put("idcard", idcard);
			getJdbc().execute(getSql("update_user_idcard", data));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 重置密码
	 * 
	 * @param phone
	 * @param password
	 * @return
	 */
	public boolean resetPassword(String userID, String password) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("user_id", userID);
			data.put("password", password);
			getJdbc().execute(getSql("reset_password", data));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
