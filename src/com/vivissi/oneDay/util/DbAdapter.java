package com.vivissi.oneDay.util;

import static com.vivissi.oneDay.util.OneDay.*;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
/**
 * 和数据库有关的帮助类
 * @author Agassi
 *
 */
public class DbAdapter {

	private static final String TAG = "DbAdapter";

	private static final String DATABASE_NAME = "oneDay.db";
	private static final int DATABSE_VERSION = 44;

	private static final String SQL_CREATE_TABLE_THINGS = "CREATE TABLE things("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "name TEXT UNIQUE NOT NULL,"
			+ "visible_flag INTEGER DEFAULT (1)," 
			+ "comment TEXT ,"
			+ "share_flag INTEGER DEFAULT (1),"
			+ "chart_need_flag INTEGER DEFAULT (0)"
			+ ");";

	private static final String SQL_CREATE_TABLE_RATING = "CREATE TABLE rating("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "day_of_month INTEGER NOT NULL,"
			+ "month INTEGER NOT NULL,"
			+ "year INTEGER NOT NULL,"
			+ "thing_id INTEGER NOT NULL,"
			+ "rating REAL NOT NULL,"
			+ "note TEXT,"
			+ "UNIQUE(day_of_month,month,year,thing_id)" + ");";

	private static final String SQL_CREATE_TABLE_WSTIME = "CREATE TABLE wstime("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "day_of_month INTEGER NOT NULL,"
			+ "month INTEGER NOT NULL,"
			+ "year INTEGER NOT NULL,"
			+ "wakeup_time REAL,"
			+ "sleep_time REAL" 
			+ ");";

	private final Context mCtx;
	private DatabaseHelper mDatabaseHelper;
	private SQLiteDatabase mDatabase;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABSE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_TABLE_THINGS);
			db.execSQL(SQL_CREATE_TABLE_RATING);
			db.execSQL(SQL_CREATE_TABLE_WSTIME);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			
			db.execSQL("DROP TABLE IF EXISTS things;");
			db.execSQL("DROP TABLE IF EXISTS rating;");
			db.execSQL("DROP TABLE IF EXISTS wstime;");
			onCreate(db);
		}

	}

	public DbAdapter(Context mCtx) {
		super();
		this.mCtx = mCtx;
	}

	public DbAdapter open() throws SQLException {
		mDatabaseHelper = new DatabaseHelper(mCtx);
		mDatabase = mDatabaseHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDatabaseHelper.close();
	}

	public long createThing(String name, String comment) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(THINGS_NAME, name);
		initialValues.put(THINGS_COMMENT, comment);
		
		return mDatabase.insert(TABLE_THINGS, null, initialValues);
	}

	public boolean deleteThing(long rowId) {
		mDatabase.delete(TABLE_RATING, RATING_THING_ID + "=" + rowId, null);
		return mDatabase.delete(TABLE_THINGS, ID + "=" + rowId, null) > 0;
	}

	public Cursor fetchAllThings(){
		String[] columns = new String[]{
			ID,
			THINGS_NAME,
			THINGS_SHARE_FLAG,
			THINGS_VISIBLE_FLAG,
			THINGS_COMMENT
		};
		
		return mDatabase.query(TABLE_THINGS, columns, null, null, null, null, null);
	}
	
	public Cursor fetch(int day, int month, int year) {
		return mDatabase.query(TABLE_THINGS + " as T, " + TABLE_RATING
				+ " as D", new String[] { "D." + ID + " AS " + "_id",
				RATING_THING_ID, "T." + THINGS_NAME, RATING_RATING,
				THINGS_COMMENT }, "T." + ID + "=" + RATING_THING_ID + " AND "
				+ OneDay.DAY_OF_MONTH + "=" + "'" + day + "'" + " AND "
				+ OneDay.MONTH + "=" + "'" + month + "'" + " AND "
				+ OneDay.YEAR + "=" + "'" + year + "'" + " AND "
				+ THINGS_VISIBLE_FLAG + "=1", null, null, null, null);
	}

	public Cursor fetchVisibleThings() {
		Cursor cursor = mDatabase.query(TABLE_THINGS, new String[] {
				THINGS_COMMENT, ID, THINGS_NAME },
				THINGS_VISIBLE_FLAG + "='1'", null, null, null, null);
		return cursor;
	}

	public long createDailything(float rating, long thingId, int day,
			int month, int year) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(RATING_RATING, rating);
		contentValues.put(RATING_THING_ID, thingId);
		contentValues.put(DAY_OF_MONTH, day);
		contentValues.put(MONTH, month);
		contentValues.put(YEAR, year);
		return mDatabase.insert(TABLE_RATING, null, contentValues);
	}

	public boolean updateDay(long rowID, float rating) {
		ContentValues values = new ContentValues();
		values.put(RATING_RATING, rating);
		return mDatabase.update(TABLE_RATING, values, ID + "=" + rowID, null) > 0;
	}

	public long createWSTime(float time, int day, int month, int year,String ws) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DAY_OF_MONTH, day);
		contentValues.put(MONTH, month);
		contentValues.put(YEAR, year);
		contentValues.put(ws, time);
		return mDatabase.insert(OneDay.TABLE_WSTIME, null, contentValues);
	}

	public Cursor fetchDiffMonth() {
		String sql = "select distinct year,month from rating " +
				"union select year,month from WSTime " +
				"order by year DESC,month DESC;";
		return mDatabase.rawQuery(sql , null);

	}

	public Cursor fetchTimeByMonth(int year, int month,String ws) {
		return mDatabase.query(TABLE_WSTIME, new String[] { DAY_OF_MONTH,
				ws }, YEAR + "=" + year + " AND " + MONTH + "="
				+ month + " AND " + ws + " not null", null,
				null, null, DAY_OF_MONTH + " DESC," + ws
						+ " ASC");
	}
	
	public Cursor fetchMonthThingsName(int year,int month){
		
		String[] columns = new String[]{
			THINGS_NAME,
			RATING_THING_ID + " AS _id",
			THINGS_CHART_FLAG
		};
		return mDatabase.query(true,TABLE_RATING+" AS r"+","+TABLE_THINGS+" AS t",
				columns , 
				YEAR + "=" +year +" AND "+"t."+ID + "=" + RATING_THING_ID + " AND "+MONTH+"="+month, 
				null,null, null, null,null);
	}
	
	public int updateThingIsChart(int row,int flag){
		ContentValues values = new ContentValues();
		values.put(THINGS_CHART_FLAG, flag);
		return mDatabase.update(TABLE_THINGS, values, ID + "=" + row, null);
	}
	
	public Cursor fetchNeedChartThingsRating(int year,int month,String name){
		String[] columns = new String[]{THINGS_NAME,RATING_RATING, DAY_OF_MONTH};
		
		String selection = RATING_THING_ID+"="+TABLE_THINGS+"."+ID +
				" AND " +THINGS_CHART_FLAG + "=" + 1 +
				" AND " + YEAR + "=" + year +
				" AND " + MONTH + "=" + month  +
				" AND " + RATING_RATING + ">=" + 0 +
				" AND " + THINGS_NAME + "=" + "'"+name+"'";
		return mDatabase.query(TABLE_RATING+","+TABLE_THINGS, columns , selection , null, null, null, DAY_OF_MONTH+" DESC");
	}
	
	public Cursor getNeedChartThingsName(){
		return mDatabase.query(TABLE_THINGS, null, THINGS_CHART_FLAG + "=" + 1, null, null, null, null);
	}
	
	public Cursor fetchAfterDate(int day,int year,int month){
		String[] columns = new String[]{YEAR,MONTH,DAY_OF_MONTH};
		String selection = YEAR+">="+year +
			" AND " + MONTH+">="+month +
			" AND " + DAY_OF_MONTH+">="+day;
		return mDatabase.query(TABLE_RATING, columns, selection, null, null, null, null);
	}
	
	public void deleteThins(int id){
		mDatabase.delete(TABLE_THINGS, ID+"="+id, null);
		mDatabase.delete(TABLE_RATING, RATING_THING_ID+"="+id, null);
	}
	
	public void changeVisFlag(int i, int rowId){
		ContentValues values = new ContentValues();
		values.put(THINGS_VISIBLE_FLAG, i);
		mDatabase.update(TABLE_THINGS, values , ID + "=" + rowId, null);
	}
	
	public void changeShareFlag(int i, int rowId){
		ContentValues values = new ContentValues();
		values.put(THINGS_SHARE_FLAG, i);
		mDatabase.update(TABLE_THINGS, values , ID + "=" + rowId, null);
	}
}
