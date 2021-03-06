package com.zzn.aeassistant.database;

import java.util.ArrayList;
import java.util.List;

import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.vo.UserVO;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDBHelper {
	private static final String table = "ae_user";

	public static void createTable() {
		String sql = "create table "
				+ table
				+ " (logon_user_id varchar(32), user_phone varchar(32), user_name varchar(64))";
		AESQLiteHelper.creatTable(table, sql);
	}

	public static void insertUser(String userID, String phone, String name) {
		delete(userID, phone);
		SQLiteDatabase db = AEApp.getDbHelper().getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("logon_user_id", userID);
		values.put("user_phone", phone);
		values.put("user_name", name);
		db.insert(table, null, values);
		db.close();
	}

	public static void delete(String userID, String phone) {
		delete("user_phone=? and logon_user_id=?",
				new String[] { phone, userID });
	}

	private static int delete(String whereClause, String[] whereArgs) {
		SQLiteDatabase db = AEApp.getDbHelper().getWritableDatabase();
		int result = db.delete(table, whereClause, whereArgs);
		db.close();
		return result;
	}

	public static List<UserVO> getUserHistory(String userID) {
		List<UserVO> result = new ArrayList<UserVO>();
		SQLiteDatabase db = AEApp.getDbHelper().getWritableDatabase();
		Cursor cursor = db.query(table, null, "logon_user_id=?",
				new String[] { userID }, null, null, null);
		while (cursor.moveToNext()) {
			UserVO user = new UserVO();
			user.setUSER_NAME(cursor.getString(2));
			user.setPHONE(cursor.getString(1));
			result.add(user);
		}
		cursor.close();
		db.close();
		return result;
	}
}
