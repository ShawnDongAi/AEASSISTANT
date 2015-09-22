package com.zzn.aeassistant.database;

import java.util.ArrayList;
import java.util.List;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.content.ContentValues;
import android.net.Uri;

import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.vo.PostVO;

public class PostDBHelper {

	/**
	 * 表字段
	 */
	public static final String POST_ID = "post_id";
	public static final String USER_ID = "user_id";
	public static final String USER_NAME = "user_name";
	public static final String USER_HEAD = "user_head";
	public static final String CONTENT = "content";
	public static final String ATTCH_ID = "attch_id";
	public static final String PROJECT_ID = "project_id";
	public static final String PROJECT_NAME = "project_name";
	public static final String ROOT_ID = "root_id";
	public static final String ROOT_PROJECT_NAME = "root_project_name";
	public static final String TIME = "time";
	public static final String SEND_USER_ID = "send_user_id";
	public static final String SEND_USER_NAME = "send_user_name";
	public static final String SEND_PROJECT_ID = "send_project_id";
	public static final String SEND_PROJECT_NAME = "send_project_name";
	public static final String IS_PRIVATE = "is_private";
	public static final String IS_NEW = "is_new";

	/**
	 * 排序依据
	 */
	public static final String DEFAULT_SORT_ORDER = "time asc";

	public static final String METHOD_GET_ITEM_COUNT = "METHOD_GET_ITEM_COUNT";
	public static final String KEY_ITEM_COUNT = "KEY_ITEM_COUNT";

	public static final String AUTHORITY = "com.zzn.aeassistant.providers.post";

	/**
	 * URI过滤
	 */
	public static final int ITEM = 1;
	public static final int ITEM_ID = 2;
	public static final int ITEM_INSERT = 3;
	public static final int ITEM_NEW = 4;

	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.zzn.aeassistant.post";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.zzn.aeassistant.post";

	/**
	 * URI
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/query_post");
	public static final Uri CONTENT_INSERT_URI = Uri.parse("content://" + AUTHORITY + "/insert_post");

	public static final String table = "ae_post";

	public static final String DB_CREATE = "create table " + table + " (" + POST_ID + " varchar(32) NOT NULL," + USER_ID
			+ " varchar(32) NOT NULL," + USER_NAME + " varchar(64) NOT NULL," + USER_HEAD + " varchar(32)," + CONTENT
			+ " varchar(3000) NOT NULL," + ATTCH_ID + " varchar(400)," + PROJECT_ID + " varchar(32) NOT NULL,"
			+ PROJECT_NAME + " varchar(64) NOT NULL," + ROOT_ID + " varchar(32) NOT NULL," + ROOT_PROJECT_NAME
			+ " varchar(64) NOT NULL," + TIME + " varchar(20) NOT NULL," + SEND_USER_ID + " varchar(200),"
			+ SEND_USER_NAME + " varchar(400)," + SEND_PROJECT_ID + " varchar(200)," + SEND_PROJECT_NAME
			+ " varchar(400)," + IS_PRIVATE + " varchar(1)," + IS_NEW + " varchar(1))";

	public static void createTable() {
		AESQLiteHelper.creatTable(table, DB_CREATE);
	}

	public static void insertPost(PostVO postVO) {
		SQLiteDatabase db = AEApp.getDbHelper().getWritableDatabase(AESQLiteHelper.ENCRYPT_KEY);
		ContentValues values = new ContentValues();
		values.put(POST_ID, postVO.getPost_id());
		values.put(USER_ID, postVO.getUser_id());
		values.put(USER_NAME, postVO.getUser_name());
		values.put(USER_HEAD, postVO.getUser_head());
		values.put(CONTENT, postVO.getContent());
		values.put(ATTCH_ID, postVO.getAttch_id());
		values.put(PROJECT_ID, postVO.getProject_id());
		values.put(PROJECT_NAME, postVO.getProject_name());
		values.put(ROOT_ID, postVO.getRoot_id());
		values.put(ROOT_PROJECT_NAME, postVO.getRoot_project_name());
		values.put(TIME, postVO.getTime());
		values.put(SEND_USER_ID, postVO.getSend_user_id());
		values.put(SEND_USER_NAME, postVO.getSend_user_name());
		values.put(SEND_PROJECT_ID, postVO.getSend_project_id());
		values.put(SEND_PROJECT_NAME, postVO.getSend_project_name());
		values.put(IS_PRIVATE, postVO.getIs_private());
		values.put(IS_NEW, postVO.getIs_new());
		db.insert(PostDBHelper.table, PostDBHelper.USER_ID, values);
		db.close();
	}

	public static List<PostVO> queryList(String project_id) {
		List<PostVO> result = new ArrayList<>();
		SQLiteDatabase db = AEApp.getDbHelper().getWritableDatabase(AESQLiteHelper.ENCRYPT_KEY);
		Cursor cursor = db.query(table, null, PostDBHelper.PROJECT_ID + "='" + project_id + "' or "
				+ PostDBHelper.SEND_PROJECT_ID + " like '%" + project_id + "%'", new String[] {}, null, null,
				"time desc");
		while (cursor.moveToNext()) {
			PostVO vo = new PostVO();
			vo.setPost_id(cursor.getString(cursor.getColumnIndex(POST_ID)));
			vo.setUser_id(cursor.getString(cursor.getColumnIndex(USER_ID)));
			vo.setUser_name(cursor.getString(cursor.getColumnIndex(USER_NAME)));
			vo.setUser_head(cursor.getString(cursor.getColumnIndex(USER_HEAD)));
			vo.setContent(cursor.getString(cursor.getColumnIndex(CONTENT)));
			vo.setAttch_id(cursor.getString(cursor.getColumnIndex(ATTCH_ID)));
			vo.setProject_id(cursor.getString(cursor.getColumnIndex(PROJECT_ID)));
			vo.setProject_name(cursor.getString(cursor.getColumnIndex(PROJECT_NAME)));
			vo.setRoot_id(cursor.getString(cursor.getColumnIndex(ROOT_ID)));
			vo.setRoot_project_name(cursor.getString(cursor.getColumnIndex(ROOT_PROJECT_NAME)));
			vo.setTime(cursor.getString(cursor.getColumnIndex(TIME)));
			vo.setSend_user_id(cursor.getString(cursor.getColumnIndex(SEND_USER_ID)));
			vo.setSend_user_name(cursor.getString(cursor.getColumnIndex(SEND_USER_NAME)));
			vo.setSend_project_id(cursor.getString(cursor.getColumnIndex(SEND_PROJECT_ID)));
			vo.setSend_project_name(cursor.getString(cursor.getColumnIndex(SEND_PROJECT_NAME)));
			vo.setIs_private(cursor.getString(cursor.getColumnIndex(IS_PRIVATE)));
			vo.setIs_new(cursor.getString(cursor.getColumnIndex(IS_NEW)));
			result.add(vo);
		}
		cursor.close();
		db.close();
		return result;
	}
}
