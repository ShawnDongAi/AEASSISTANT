package com.zzn.aeassistant.database;

import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.vo.AttchVO;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AttchDBHelper {
	private static final String table = "ae_attch";

	public static void createTable() {
		String sql = "create table " + table + " (attch_id varchar(32),"
				+ "type char(2), url varchar(255), path varchar(255))";
		AESQLiteHelper.creatTable(table, sql);
	}

	public static int delete(String whereClause, String[] whereArgs) {
		SQLiteDatabase db = AEApp.getDbHelper().getWritableDatabase();
		int result = db.delete(table, whereClause, whereArgs);
		return result;
	}

	public static AttchVO getAttch(String attchID) {
		AttchVO attch = new AttchVO();
		attch.setATTCH_ID(attchID);
		SQLiteDatabase db = AEApp.getDbHelper().getWritableDatabase();
		String selection = "attch_id=?";
		Cursor cursor = db.query(table, null, selection,
				new String[] { attchID }, null, null, null);
		if (cursor.getCount() > 0) {
			attch.setTYPE(cursor.getString(cursor.getColumnIndex("type")));
			attch.setURL(cursor.getString(cursor.getColumnIndex("url")));
			attch.setLOCAL_PATH(cursor.getString(cursor.getColumnIndex("path")));
		} else {
			attch = null;
		}
		cursor.close();
		db.close();
		return attch;
	}

	public static void insertAtth(AttchVO attch) {
		SQLiteDatabase db = AEApp.getDbHelper().getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("attch_id", attch.getATTCH_ID());
		values.put("type", attch.getTYPE());
		values.put("url", attch.getURL());
		values.put("path", attch.getLOCAL_PATH());
		db.insert(table, null, values);
		db.close();
	}
}
