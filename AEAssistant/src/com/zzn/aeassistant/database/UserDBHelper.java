package com.zzn.aeassistant.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;

import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.vo.UserVO;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

public class UserDBHelper {
	private static final String table = "ae_user";

	public static void createTable() {
		String sql = "create table "
				+ table
				+ " (logon_user_id varchar(32), user_phone varchar(32), user_name varchar(64))";
		AESQLiteHelper.creatTable(table, sql);
	}

	public static void insertUser(String phone, String name) {
		delete(phone);
		SQLiteDatabase db = AEApp.getDbHelper().getWritableDatabase(
				AESQLiteHelper.ENCRYPT_KEY);
		ContentValues values = new ContentValues();
		values.put("logon_user_id", AEApp.getCurrentUser().getUSER_ID());
		values.put("user_phone", phone);
		values.put("user_name", name);
		db.insert(table, null, values);
	}

	public static void delete(String phone) {
		delete("user_phone=? and logon_user_id=?", new String[] { phone,
				AEApp.getCurrentUser().getUSER_ID() });
	}

	private static int delete(String whereClause, String[] whereArgs) {
		SQLiteDatabase db = AEApp.getDbHelper().getWritableDatabase(
				AESQLiteHelper.ENCRYPT_KEY);
		int result = db.delete(table, whereClause, whereArgs);
		db.close();
		return result;
	}
	
	public static List<UserVO> getUserHistory() {
		List<UserVO> result = new ArrayList<UserVO>();
		SQLiteDatabase db = AEApp.getDbHelper().getWritableDatabase(
				AESQLiteHelper.ENCRYPT_KEY);
		Cursor cursor = db.query(table, null, "logon_user_id=?", new String[]{AEApp.getCurrentUser().getUSER_ID()}, null, null, null);
		while(cursor.moveToNext()) {
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
