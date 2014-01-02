package com.example.android.location;

import java.util.ArrayList;
import java.util.List;

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

	public void addLocation(Location location) {

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, location.get_name());
        values.put(DBHelper.COLUMN_LATITUDE, location.get_latitude());
        values.put(DBHelper.COLUMN_LONGTITUDE, location.get_longtitude());
 
      
        db.insert(DBHelper.TABLE_LOCATIONS, null, values);
	}
		
	public List<Location> findLocation(String name) {
		
		List<Location> locations = new ArrayList<Location>();
		
		String query = "Select * FROM " + DBHelper.TABLE_LOCATIONS + " WHERE " + DBHelper.COLUMN_NAME + " =  \"" + name + "\"";
				
		Cursor cursor = db.rawQuery(query, null);
		
	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Location location = cursorToLocation(cursor);
	      locations.add(location);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();		
		return locations;
	}
	
	private Location cursorToLocation(Cursor cursor) {
		Location location = new Location();
		location.set_id(Integer.parseInt(cursor.getString(0)));
		location.set_name(cursor.getString(1));
		location.set_latitude(Double.parseDouble(cursor.getString(2)));
		location.set_longtitude(Double.parseDouble(cursor.getString(3)));
		return location;
	}
}
