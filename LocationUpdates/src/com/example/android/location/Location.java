package com.example.android.location;

public class Location {
	
	private int _id;
	private String _name;
	private double _longtitude;
	private double _latitude;
	
	// Empty constructor  
	public Location(){
		
	}
	
	public Location(int id, String name, double longtitude, double latitude) {
		this._id = id;
		this._name = name;
		this._latitude = latitude;
		this._longtitude = longtitude;
	}
	
	public Location(String name, double longtitude, double latitude) {
		this._name = name;
		this._latitude = latitude;
		this._longtitude = longtitude;
	}
	
	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
	}

	public double get_longtitude() {
		return _longtitude;
	}

	public void set_longtitude(double _longtitude) {
		this._longtitude = _longtitude;
	}

	public double get_latitude() {
		return _latitude;
	}

	public void set_latitude(double _latitude) {
		this._latitude = _latitude;
	}

}
