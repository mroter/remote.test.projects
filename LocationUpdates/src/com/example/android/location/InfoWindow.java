package com.example.android.location;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class InfoWindow implements InfoWindowAdapter {
	
	LayoutInflater inflater=null;
    Context context;
    Address address;

	public void setAddress(Address address) {
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
        tvLat.setText("Latitude: \t\t" + context.getString(R.string.lat_long, latLng.latitude));

        // Setting the longitude
        tvLng.setText("Longitude: \t"+ context.getString(R.string.lat_long, latLng.longitude));
        
        // Setting the address
        String sAddress = "";
        if (address != null) {
              // If there's a street address, add it
             if (address.getMaxAddressLineIndex() > 0)  {
                  sAddress = address.getAddressLine(0) + "\n";
             }
             // Locality is usually a city
             sAddress += address.getLocality() + "\n" ;

             // The country of the address
             sAddress += address.getCountryName();
             
             tvAddress.setText("Address: \n" + sAddress);
        }

        // Returning the view containing InfoWindow contents
        return v;

    }
}
