package com.zzn.aeassistant.database;

import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.util.PhoneUtil;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库操作类
 * 
 * @author Shawn
 *
 */
public class AESQLiteHelper extends SQLiteOpenHelper {
	private static AESQLiteHelper instance = null;
	public static String DATABASE_NAME = "AENote.db";// 数据库文件名
	private static int DATABASE_VERSION = 3;// 数据库版本
//	public final static String ENCRYPT_KEY = PhoneUtil.getIMEI();

	public AESQLiteHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		instance = this;
	}

	public AESQLiteHelper(Context context) {
		this(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public AESQLiteHelper(Context context, int version) {
		this(context, DATABASE_NAME, null, version);
	}

	public static AESQLiteHelper getInstance() {
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == 1) {
			db.execSQL(PostDBHelper.DB_CREATE);
			db.execSQL(CommentDBHelper.DB_CREATE);
		}
		if (oldVersion == 2) {
			db.execSQL(PostDBHelper.DB_DROP);
			db.execSQL(CommentDBHelper.DB_DROP);
			db.execSQL(PostDBHelper.DB_CREATE);
			db.execSQL(CommentDBHelper.DB_CREATE);
		}
		db.setVersion(DATABASE_VERSION);
	}

	/**
	 * 判断数据库表是否存在
	 * 
	 * @param tableName
	 * @return
	 */
	public static boolean isTableExist(String tableName) {
		boolean flag = false;
		try {
			SQLiteDatabase db = AEApp.getDbHelper().getReadableDatabase();
			flag = isTableExist(tableName, db);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	private static boolean isTableExist(String tableName, SQLiteDatabase database) {
		boolean flag = false;
		try {
			SQLiteDatabase db = database;
			String sql = "select count(*) as c from Sqlite_master where type ='table' and name ='" + tableName + "' ";
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				if (cursor.getInt(0) > 0)
					flag = true;
				else
					flag = false;
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 删除数据库表
	 * 
	 * @param tableName
	 * @return
	 */
	public static boolean deleteTable(String tableName) {
		try {
			SQLiteDatabase db = AEApp.getDbHelper().getReadableDatabase();
			String sql = "DROP TABLE IF EXISTS " + tableName;
			db.execSQL(sql);
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 创建数据库表
	 * 
	 * @param tableName
	 * @param sql
	 */
	public static void creatTable(String tableName, String sql) {
		boolean flag = isTableExist(tableName);// 判断表是否存在
		if (!flag) {
			try {
				SQLiteDatabase db = AEApp.getDbHelper().getReadableDatabase();
				db.execSQL(sql);
				db.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
