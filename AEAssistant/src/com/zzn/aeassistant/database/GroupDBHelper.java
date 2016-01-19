package com.zzn.aeassistant.database;

import com.zzn.aeassistant.app.AEApp;

import android.database.sqlite.SQLiteDatabase;
/**
 * 群组表
 * 
 * @author Shawn
 */
public class GroupDBHelper {
	private static final String table = "ae_group";

	public static void createTable() {
		String sql = "create table " + table + " (logon_user_id varchar(32),"
				+ "group_id varchar(32), group_name varchar(64),"
				+ "parent_group_id varchar(32), root_group_id varchar(32),"
				+ "pic_id varchar(32), create_time varchar(64))";
		AESQLiteHelper.creatTable(table, sql);
	}

	public static int delete(String whereClause, String[] whereArgs) {
		SQLiteDatabase db = AEApp.getDbHelper().getWritableDatabase();
		int result = db.delete(table, whereClause, whereArgs);
		return result;
	}
}
