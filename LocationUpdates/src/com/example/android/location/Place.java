package com.example.android.location;

import android.location.Address;

public class Place {
	
	private int _id;
	private String _name;
	private double _longtitude;
	private double _latitude;
	private double _altitude;
	private Address _address;
	
	// Empty constructor  
	public Place(){
		
	}
	
	public Place(String name, double latitude, double longtitude, double altitude) {
		this._name = name;
		this._latitude = latitude;
		this._longtitude = longtitude;
		this._altitude = altitude;
	}
	
	public Place(double latitude, double longtitude, double altitude) {
		this._latitude = latitude;
		this._longtitude = longtitude;
		this._altitude = altitude;
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

	public double get_altitude() {
		return _altitude;
	}

	public void set_altitude(double _altitude) {
		this._altitude = _altitude;
	}

	public Address get_address() {
		return _address;
	}

	public void set_address(Address _address) {
		this._address = _address;
	}

}
