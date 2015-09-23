package com.zzn.aeassistant.database;

import java.util.ArrayList;
import java.util.List;

import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.vo.CommentVO;

import android.content.ContentValues;
import android.net.Uri;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

public class CommentDBHelper {

	/**
	 * 表字段
	 */
	public static final String COMMENT_ID = "comment_id";
	public static final String POST_ID = "post_id";
	public static final String USER_ID = "user_id";
	public static final String USER_NAME = "user_name";
	public static final String USER_HEAD = "user_head";
	public static final String CONTENT = "content";
	public static final String ATTCH_ID = "attch_id";
	public static final String PROJECT_ID = "project_id";
	public static final String PROJECT_NAME = "project_name";
	public static final String ROOT_ID = "root_id";
	public static final String TIME = "time";
	public static final String IS_NEW = "is_new";

	/**
	 * 排序依据
	 */
	public static final String DEFAULT_SORT_ORDER = "time desc";
	public static final String METHOD_GET_ITEM_COUNT = "METHOD_GET_ITEM_COUNT";
	public static final String KEY_ITEM_COUNT = "KEY_ITEM_COUNT";

	public static final String AUTHORITY = "com.zzn.aeassistant.providers.comment";

	/**
	 * URI过滤
	 */
	public static final int ITEM = 1;
	public static final int ITEM_ID = 2;
	public static final int ITEM_INSERT = 3;
	public static final int ITEM_NEW = 4;

	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.zzn.aeassistant.comment";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.zzn.aeassistant.comment";

	/**
	 * URI
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/query_comment");
	public static final Uri CONTENT_INSERT_URI = Uri.parse("content://" + AUTHORITY + "/insert_comment");

	public static final String table = "ae_comment";

	public static final String DB_CREATE = "create table " + table + " (" + COMMENT_ID + " varchar(32) NOT NULL,"
			+ POST_ID + " varchar(32) NOT NULL," + USER_ID + " varchar(32) NOT NULL," + USER_NAME
			+ " varchar(64) NOT NULL," + USER_HEAD + " varchar(32)," + CONTENT + " varchar(3000) NOT NULL," + ATTCH_ID
			+ " varchar(400)," + PROJECT_ID + " varchar(32) NOT NULL," + PROJECT_NAME + " varchar(64) NOT NULL,"
			+ ROOT_ID + " varchar(32) NOT NULL," + TIME + " varchar(20) NOT NULL," + IS_NEW + " varchar(1))";

	public static void createTable() {
		AESQLiteHelper.creatTable(table, DB_CREATE);
	}

	public static void insertComment(CommentVO commentVO) {
		SQLiteDatabase db = AEApp.getDbHelper().getWritableDatabase(AESQLiteHelper.ENCRYPT_KEY);
		ContentValues values = new ContentValues();
		values.put(POST_ID, commentVO.getPost_id());
		values.put(USER_ID, commentVO.getUser_id());
		values.put(USER_NAME, commentVO.getUser_name());
		values.put(USER_HEAD, commentVO.getUser_head());
		values.put(CONTENT, commentVO.getContent());
		values.put(ATTCH_ID, commentVO.getAttch_id());
		values.put(PROJECT_ID, commentVO.getProject_id());
		values.put(PROJECT_NAME, commentVO.getProject_name());
		values.put(ROOT_ID, commentVO.getRoot_id());
		values.put(TIME, commentVO.getTime());
		values.put(IS_NEW, commentVO.getIs_new());
		db.insert(CommentDBHelper.table, CommentDBHelper.USER_ID, values);
		db.close();
	}

	public static List<CommentVO> queryList(String post_id) {
		List<CommentVO> result = new ArrayList<>();
		SQLiteDatabase db = AEApp.getDbHelper().getWritableDatabase(AESQLiteHelper.ENCRYPT_KEY);
		Cursor cursor = db.query(table, null, CommentDBHelper.POST_ID + "='" + post_id + "'", new String[] {}, null,
				null, "time desc");
		while (cursor.moveToNext()) {
			CommentVO vo = new CommentVO();
			vo.setComment_id(cursor.getString(cursor.getColumnIndex(COMMENT_ID)));
			vo.setPost_id(cursor.getString(cursor.getColumnIndex(POST_ID)));
			vo.setUser_id(cursor.getString(cursor.getColumnIndex(USER_ID)));
			vo.setUser_name(cursor.getString(cursor.getColumnIndex(USER_NAME)));
			vo.setUser_head(cursor.getString(cursor.getColumnIndex(USER_HEAD)));
			vo.setContent(cursor.getString(cursor.getColumnIndex(CONTENT)));
			vo.setAttch_id(cursor.getString(cursor.getColumnIndex(ATTCH_ID)));
			vo.setProject_id(cursor.getString(cursor.getColumnIndex(PROJECT_ID)));
			vo.setProject_name(cursor.getString(cursor.getColumnIndex(PROJECT_NAME)));
			vo.setRoot_id(cursor.getString(cursor.getColumnIndex(ROOT_ID)));
			vo.setTime(cursor.getString(cursor.getColumnIndex(TIME)));
			vo.setIs_new(cursor.getString(cursor.getColumnIndex(IS_NEW)));
			result.add(vo);
		}
		cursor.close();
		db.close();
		return result;
	}
}
