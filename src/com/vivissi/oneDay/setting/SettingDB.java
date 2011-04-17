package com.vivissi.oneDay.setting;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SettingDB {
	private final String TAG = "SettingDB";
	private static final String DATABASE_NAME = "Setting.db";
	private static final int DATABASE_VERSION = 1;
	private SQLiteDatabase database;
	
	private static final String SQL_CREATE_TABLE_SETTING = "CREATE TABLE setting( " +
	"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
	"name TEXT, " +
	"value INTEGER);";
	public SettingDB(Context context) {
		// TODO Auto-generated constructor stub
		database = new DBHelper(context).getWritableDatabase();
	}
	
	private static class DBHelper extends SQLiteOpenHelper{

		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(SQL_CREATE_TABLE_SETTING);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS setting;");
		}
		
	}
}
