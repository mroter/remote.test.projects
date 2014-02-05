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
        //LinearLayout linearLayout = (LinearLayout)v.findViewById(R.id.info);        

        // Getting the position from the marker
        LatLng latLng = arg0.getPosition();
        
        // Retrieve the Place using the markerId
        Place place = MainActivity.getPlace(arg0.getId());
        
        
        TextView tvName = (TextView) v.findViewById(R.id.tv_name);
        // if place has a name - display it
        if (place.get_name() != null) {
        	tvName.setText(place.get_name());
        } else
        	tvName.setText("");
        
        // Getting reference to the TextView to set latitude
        TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);

        // Getting reference to the TextView to set longitude
        TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
        
        // Getting reference to the TextView to set address
        TextView tvAddress = (TextView) v.findViewById(R.id.tv_address);
        
        // Getting reference to the TextView to set address
        TextView tvAltitude = (TextView) v.findViewById(R.id.tv_altitude);

        // Setting the latitude
        tvLat.setText("Latitude: \t\t" + context.getString(R.string.lat_long, latLng.latitude));

        // Setting the longitude
        tvLng.setText("Longitude: \t"+ context.getString(R.string.lat_long, latLng.longitude));
        
        // Setting the address
        String sAddress = "";
        Address address = place.get_address();
        if (address != null) {
              // If there's a street address, add it
             if (address.getMaxAddressLineIndex() > 0)  {
                  sAddress = "Street: \t" + address.getAddressLine(0) + "\n";
             }
             // Locality is usually a city
             sAddress += "City: \t";
             if (address.getPostalCode() != null) sAddress += address.getPostalCode() + " ";
             sAddress += address.getLocality() + "\n" ;

             // The country of the address
             sAddress += "Country: \t" + address.getCountryName();
             
             tvAddress.setText(sAddress);
        }
        
        // Setting the altitude
        tvAltitude.setText("Altitude: \t" + context.getString(R.string.altitude_format, place.get_altitude()));


        // Returning the view containing InfoWindow contents
        return v;

    }

}
