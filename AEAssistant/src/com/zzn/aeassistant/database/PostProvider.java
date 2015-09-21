package com.zzn.aeassistant.database;

import java.util.HashMap;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteQueryBuilder;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.util.StringUtil;

public class PostProvider extends ContentProvider {
	public static final Uri CONTENT_URI = Uri.parse("content://com.zzn.aeassistant.providers.post");
	private static final UriMatcher uriMatcher;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PostDBHelper.AUTHORITY, "item", PostDBHelper.ITEM);
		uriMatcher.addURI(PostDBHelper.AUTHORITY, "item/#", PostDBHelper.ITEM_ID);
		uriMatcher.addURI(PostDBHelper.AUTHORITY, "new/#", PostDBHelper.ITEM_NEW);
	}

	private static final HashMap<String, String> postProjectionMap;

	static {
		postProjectionMap = new HashMap<String, String>();
		postProjectionMap.put(PostDBHelper.POST_ID, PostDBHelper.POST_ID);
		postProjectionMap.put(PostDBHelper.USER_ID, PostDBHelper.USER_ID);
		postProjectionMap.put(PostDBHelper.USER_NAME, PostDBHelper.USER_NAME);
		postProjectionMap.put(PostDBHelper.USER_HEAD, PostDBHelper.USER_HEAD);
		postProjectionMap.put(PostDBHelper.CONTENT, PostDBHelper.CONTENT);
		postProjectionMap.put(PostDBHelper.ATTCH_ID, PostDBHelper.ATTCH_ID);
		postProjectionMap.put(PostDBHelper.PROJECT_ID, PostDBHelper.PROJECT_ID);
		postProjectionMap.put(PostDBHelper.PROJECT_NAME, PostDBHelper.PROJECT_NAME);
		postProjectionMap.put(PostDBHelper.ROOT_ID, PostDBHelper.ROOT_ID);
		postProjectionMap.put(PostDBHelper.ROOT_PROJECT_NAME, PostDBHelper.ROOT_PROJECT_NAME);
		postProjectionMap.put(PostDBHelper.TIME, PostDBHelper.TIME);
		postProjectionMap.put(PostDBHelper.SEND_USER_ID, PostDBHelper.SEND_USER_ID);
		postProjectionMap.put(PostDBHelper.SEND_USER_NAME, PostDBHelper.SEND_USER_NAME);
		postProjectionMap.put(PostDBHelper.SEND_PROJECT_ID, PostDBHelper.SEND_PROJECT_ID);
		postProjectionMap.put(PostDBHelper.SEND_PROJECT_NAME, PostDBHelper.SEND_PROJECT_NAME);
		postProjectionMap.put(PostDBHelper.IS_PRIVATE, PostDBHelper.IS_PRIVATE);
		postProjectionMap.put(PostDBHelper.IS_NEW, PostDBHelper.IS_NEW);
	}

	private ContentResolver resolver = null;

	@Override
	public boolean onCreate() {
		Context context = getContext();
		resolver = context.getContentResolver();
		return true;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case PostDBHelper.ITEM:
			return PostDBHelper.CONTENT_TYPE;
		case PostDBHelper.ITEM_ID:
		case PostDBHelper.ITEM_INSERT:
		case PostDBHelper.ITEM_NEW:
			return PostDBHelper.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Error Uri: " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = AEApp.getDbHelper().getWritableDatabase(AESQLiteHelper.ENCRYPT_KEY);
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		String limit = null;
		switch (uriMatcher.match(uri)) {
		case PostDBHelper.ITEM: {
			sqlBuilder.setTables(PostDBHelper.table);
			sqlBuilder.setProjectionMap(postProjectionMap);
			break;
		}
		case PostDBHelper.ITEM_ID: {
			String user_id = uri.getPathSegments().get(1);
			sqlBuilder.setTables(PostDBHelper.table);
			sqlBuilder.setProjectionMap(postProjectionMap);
			sqlBuilder.appendWhere(PostDBHelper.USER_ID + "=" + user_id + " or (" + PostDBHelper.SEND_USER_ID + "like %"
					+ user_id + "% and" + PostDBHelper.IS_PRIVATE + "='1')");
			break;
		}
		case PostDBHelper.ITEM_NEW: {
			String user_id = uri.getPathSegments().get(1);
			sqlBuilder.setTables(PostDBHelper.table);
			sqlBuilder.setProjectionMap(postProjectionMap);
			sqlBuilder.appendWhere(
					PostDBHelper.SEND_USER_ID + "like %" + user_id + "% and " + PostDBHelper.IS_NEW + "='0'");
			break;
		}
		default:
			throw new IllegalArgumentException("Error Uri: " + uri);
		}
		Cursor cursor = sqlBuilder.query(db, projection, selection, selectionArgs, null, null,
				StringUtil.isEmpty(sortOrder) ? PostDBHelper.DEFAULT_SORT_ORDER : sortOrder, limit);
		cursor.setNotificationUri(resolver, uri);
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = AEApp.getDbHelper().getWritableDatabase(AESQLiteHelper.ENCRYPT_KEY);
		long id = db.insert(PostDBHelper.table, CommentDBHelper.USER_ID, values);
		db.close();
		if (id < 0) {
			throw new SQLiteException("Unable to insert " + values + " for " + uri);
		}
		Uri newUri = ContentUris.withAppendedId(uri, id);
		resolver.notifyChange(uri, null);
		return newUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = AEApp.getDbHelper().getWritableDatabase(AESQLiteHelper.ENCRYPT_KEY);
		int count = 0;
		switch (uriMatcher.match(uri)) {
		case PostDBHelper.ITEM: {
			count = db.update(PostDBHelper.table, values, selection, selectionArgs);
			break;
		}
		case PostDBHelper.ITEM_ID: {
			String id = uri.getPathSegments().get(1);
			count = db
					.update(PostDBHelper.table, values,
							PostDBHelper.POST_ID + "=" + id
									+ (!StringUtil.isEmpty(selection) ? " and (" + selection + ')' : ""),
							selectionArgs);
			break;
		}
		default:
			throw new IllegalArgumentException("Error Uri: " + uri);
		}
		db.close();
		resolver.notifyChange(uri, null);
		return count;
	}
}