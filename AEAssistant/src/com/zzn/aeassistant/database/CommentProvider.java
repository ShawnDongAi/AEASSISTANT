package com.zzn.aeassistant.database;

import java.util.HashMap;

import com.zzn.aeassistant.app.AEApp;
import com.zzn.aeassistant.util.StringUtil;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class CommentProvider extends ContentProvider {
	public static final Uri CONTENT_URI = Uri
			.parse("content://com.zzn.aeassistant.providers.comment");
	private static final UriMatcher uriMatcher;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(CommentDBHelper.AUTHORITY, "item",
				CommentDBHelper.ITEM);
		uriMatcher.addURI(CommentDBHelper.AUTHORITY, "item/#",
				CommentDBHelper.ITEM_ID);
		uriMatcher.addURI(CommentDBHelper.AUTHORITY, "new/#",
				CommentDBHelper.ITEM_NEW);
	}

	private static final HashMap<String, String> commentProjectionMap;

	static {
		commentProjectionMap = new HashMap<String, String>();
		commentProjectionMap.put(CommentDBHelper.COMMENT_ID,
				CommentDBHelper.COMMENT_ID);
		commentProjectionMap.put(CommentDBHelper.POST_ID,
				CommentDBHelper.POST_ID);
		commentProjectionMap.put(CommentDBHelper.USER_ID,
				CommentDBHelper.USER_ID);
		commentProjectionMap.put(CommentDBHelper.USER_NAME,
				CommentDBHelper.USER_NAME);
		commentProjectionMap.put(CommentDBHelper.USER_HEAD,
				CommentDBHelper.USER_HEAD);
		commentProjectionMap.put(CommentDBHelper.CONTENT,
				CommentDBHelper.CONTENT);
		commentProjectionMap.put(CommentDBHelper.ATTCH_ID,
				CommentDBHelper.ATTCH_ID);
		commentProjectionMap.put(CommentDBHelper.PROJECT_ID,
				CommentDBHelper.PROJECT_ID);
		commentProjectionMap.put(CommentDBHelper.PROJECT_NAME,
				CommentDBHelper.PROJECT_NAME);
		commentProjectionMap.put(CommentDBHelper.ROOT_ID,
				CommentDBHelper.ROOT_ID);
		commentProjectionMap
				.put(CommentDBHelper.IS_NEW, CommentDBHelper.IS_NEW);
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
		case CommentDBHelper.ITEM:
			return CommentDBHelper.CONTENT_TYPE;
		case CommentDBHelper.ITEM_ID:
		case CommentDBHelper.ITEM_INSERT:
		case CommentDBHelper.ITEM_NEW:
			return CommentDBHelper.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Error Uri: " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = AEApp.getDbHelper().getWritableDatabase();
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		String limit = null;
		switch (uriMatcher.match(uri)) {
		case CommentDBHelper.ITEM: {
			sqlBuilder.setTables(CommentDBHelper.table);
			sqlBuilder.setProjectionMap(commentProjectionMap);
			break;
		}
		case CommentDBHelper.ITEM_ID: {
			String post_id = uri.getPathSegments().get(1);
			sqlBuilder.setTables(CommentDBHelper.table);
			sqlBuilder.setProjectionMap(commentProjectionMap);
			sqlBuilder.appendWhere(CommentDBHelper.POST_ID + "='" + post_id
					+ "')");
			break;
		}
		case CommentDBHelper.ITEM_NEW: {
			String post_id = uri.getPathSegments().get(1);
			sqlBuilder.setTables(CommentDBHelper.table);
			sqlBuilder.setProjectionMap(commentProjectionMap);
			sqlBuilder.appendWhere(CommentDBHelper.POST_ID + "='" + post_id
					+ "' and " + CommentDBHelper.IS_NEW + "='0')");
			break;
		}
		default:
			throw new IllegalArgumentException("Error Uri: " + uri);
		}
		Cursor cursor = sqlBuilder
				.query(db,
						projection,
						selection,
						selectionArgs,
						null,
						null,
						StringUtil.isEmpty(sortOrder) ? CommentDBHelper.DEFAULT_SORT_ORDER
								: sortOrder, limit);
		cursor.setNotificationUri(resolver, uri);
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = AEApp.getDbHelper().getWritableDatabase();
		long id = 0;
		try {
			id = db.insert(CommentDBHelper.table, CommentDBHelper.USER_ID,
					values);
		} catch (Exception e) {
		}
		db.close();
		if (id < 0) {
			throw new SQLiteException("Unable to insert " + values + " for "
					+ uri);
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
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = AEApp.getDbHelper().getWritableDatabase();
		int count = 0;
		switch (uriMatcher.match(uri)) {
		case CommentDBHelper.ITEM: {
			count = db.update(CommentDBHelper.table, values, selection,
					selectionArgs);
			break;
		}
		case CommentDBHelper.ITEM_ID: {
			String id = uri.getPathSegments().get(1);
			count = db.update(
					CommentDBHelper.table,
					values,
					CommentDBHelper.COMMENT_ID
							+ "="
							+ id
							+ (!StringUtil.isEmpty(selection) ? " and ("
									+ selection + ')' : ""), selectionArgs);
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