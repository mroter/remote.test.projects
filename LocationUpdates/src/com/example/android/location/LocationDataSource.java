package com.example.android.location;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class LocationDataSource {
	
	// Database fields
	private SQLiteDatabase db;
	private DBHelper dbHelper;


	public LocationDataSource(Context context) {
		dbHelper = new DBHelper(context);
	}

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void addLocation(Place place) {

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, place.get_name());
        values.put(DBHelper.COLUMN_LATITUDE, place.get_latitude());
        values.put(DBHelper.COLUMN_LONGTITUDE, place.get_longtitude());
        values.put(DBHelper.COLUMN_ALTITUDE, place.get_altitude());
 
      
        db.insert(DBHelper.TABLE_LOCATIONS, null, values);
	}
		
	public Cursor findLocation(String name) {
	
		//String query = "Select * FROM " + DBHelper.TABLE_LOCATIONS + " WHERE " + DBHelper.COLUMN_NAME + " =  \"" + name + "\"";
		String query = "Select * FROM " + DBHelper.TABLE_LOCATIONS + " WHERE " + DBHelper.COLUMN_NAME + " LIKE '" + name + "%'";
	
		return db.rawQuery(query, null);
	}
	
	public void deleteLocation(int id){
		db.delete(DBHelper.TABLE_LOCATIONS, DBHelper.COLUMN_ID + " = " + id, null);	
	}
	
}
