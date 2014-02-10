package com.example.android.location;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 5;
	private static final String DATABASE_NAME = "locationDB.db";
	
	public static final String TABLE_LOCATIONS = "locations";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGTITUDE = "longtitude";
	public static final String COLUMN_ALTITUDE = "altitude";
	
	public static final String CREATE_LOCATIONS_TABLE = "create table " +
			TABLE_LOCATIONS + "("
            + COLUMN_ID + " integer primary key autoincrement," + COLUMN_NAME 
            + " TEXT," + COLUMN_LATITUDE + " TEXT," + COLUMN_LONGTITUDE + " TEXT," + COLUMN_ALTITUDE + " TEXT"  + ")";
	
	/**
	 * This class is responsible for creating the database and upgrading it. 
	 * 
	 * @param context
	 */
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	      db.execSQL(CREATE_LOCATIONS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	   Log.w(DBHelper.class.getName(),
		        "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
	    onCreate(db);
	}
	


}
