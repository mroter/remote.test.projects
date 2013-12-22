package com.example.android.location;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class InfoWindow implements InfoWindowAdapter {
	
	LayoutInflater inflater=null;
    Context context;
    String address;

	public void setAddress(String address) {
		this.address = address;
	}


	public InfoWindow (LayoutInflater inflater, Context context){
    	 this.inflater=inflater;
         this.context = context;    	
    }
	
	
	// Use default InfoWindow frame
    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    // Defines the contents of the InfoWindow
    @Override
    public View getInfoContents(Marker arg0) {

        // Getting view from the layout file info_window_layout
        View v = inflater.inflate(R.layout.info_window, null);

        // Getting the position from the marker
        LatLng latLng = arg0.getPosition();
        arg0.getSnippet();

        // Getting reference to the TextView to set latitude
        TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);

        // Getting reference to the TextView to set longitude
        TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
        
     // Getting reference to the TextView to set address
        TextView tvAddress = (TextView) v.findViewById(R.id.tv_address);

        // Setting the latitude
        tvLat.setText("Latitude:  " + latLng.latitude);

        // Setting the longitude
        tvLng.setText("Longitude: "+ latLng.longitude);
        
     // Setting the address
        tvAddress.setText("Address: " + address);

        // Returning the view containing InfoWindow contents
        return v;

    }
}
