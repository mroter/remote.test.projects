/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.location;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * This is the app's main Activity. 
 *
 * {@link #getLocation} gets the current location using the Location Services getLastLocation()
 * function. {@link #getAddress} calls geocoding to get a street address for the current location.
 * {@link #startUpdates} sends a request to Location Services to send periodic location updates to
 * the Activity.
 * {@link #stopUpdates} cancels previous periodic update requests.
 *
 * The update interval is hard-coded to be 5 seconds.
 */
public class MainActivity extends FragmentActivity implements
        LocationListener,
        OnInfoWindowClickListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener  {

	private LocationDataSource ds;
		
	private static final int MENU_UPDATES = Menu.FIRST + 2;
		
	// A request to connect to Location Services
    private LocationRequest mLocationRequest;

    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;

    // Handles to UI widgets
    private TextView mSpeed;
    
    @SuppressWarnings("unused")
	private String mConnectionStatus;
    
    // Google Map
    private GoogleMap map;
    //TODO remove it
    private Marker mMarker;
    private static HashMap<String, Place> markers;


    // Handle to SharedPreferences for this app
    SharedPreferences mPrefs;

    // Handle to a SharedPreferences editor
    SharedPreferences.Editor mEditor;

    /*
     * Note if updates have been turned on. Starts out as "false"; is set to "true" in the
     * method handleRequestSuccess of LocationUpdateReceiver.
     *
     */
    boolean mUpdatesRequested = false;
        
    // Initially there is no bearing
    float mBearing = 0;
    
    // Create InfoWindow object
    InfoWindow infoWindow; 

    /*
     * Initialize the Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout);
                
        // keeping the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        markers = new HashMap<String, Place>();

        // Get handles to the UI view objects
        mSpeed = (TextView) findViewById(R.id.Speed);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
                        
        // Create a new global location parameters object
        mLocationRequest = LocationRequest.create();

        /*
         * Set the update interval
         */
        mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        // Note that location updates are off until the user turns them on
        mUpdatesRequested = false;

        // Open Shared Preferences
        mPrefs = getSharedPreferences(LocationUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        
        // Get an editor
        mEditor = mPrefs.edit();
        
        // Load Preferences
        loadPref();

        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);
        
        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // For the main activity, make sure the app icon in the action bar
            // does not behave as a button
            ActionBar actionBar = getActionBar();
            actionBar.setHomeButtonEnabled(false);
        }
        
        // Initial zoom of the map
        map.setMyLocationEnabled(true);
        
        // Set initial location and zoom of the map
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(32.074, 34.791), 10));
        

                      
     // Setting a custom info window adapter for the google map
        infoWindow = new InfoWindow(getLayoutInflater(), MainActivity.this);
        map.setInfoWindowAdapter(infoWindow);
     // Setup a listener for Marker click events
        map.setOnInfoWindowClickListener(this);
        
        // Open the local locations DB
        ds = new LocationDataSource(this);
        ds.open();


    }
        
	@Override
    public void onInfoWindowClick(Marker marker) {
    	//Retrieve the Place object from Markers HashMap using the marker id
    	final Place place = markers.get(marker.getId());
    	
    	// hide the marker (remove it?)
    	marker.setVisible(false);
    	
    	// display save dialog
    	LayoutInflater li = LayoutInflater.from(this);
		View saveDialog = li.inflate(R.layout.save_dialog, null);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(saveDialog);
		final EditText userInput = (EditText) saveDialog.findViewById(R.id.location_name);
		alertDialogBuilder
			.setTitle("Save Location to Database")
			.setCancelable(false)
			.setPositiveButton("OK",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
			    	place.set_name(userInput.getText().toString());
			    	ds.addLocation(place);
			    }
			  })
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
      }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	//super.onCreateOptionsMenu(menu);
    	buildMenu(menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
    	// Handle item selection
        switch (item.getItemId()) {
        
            case R.id.get_location:
                getLocation();
                return true;
                 
            case MENU_UPDATES:
            	if (mUpdatesRequested) {
            		stopUpdates();
            	} else {
            		startUpdates();
            	}
            	invalidateOptionsMenu ();
                return true;
                 
            case R.id.exit:
            	if (mUpdatesRequested) {
            		stopUpdates();
                }
                this.finish();
                return true; 
                
            case R.id.action_search:
            	//onSearchRequested(); 
            	
            	intent = new Intent(MainActivity.this, SearchActivity.class);
            	startActivityForResult(intent, 1);
            	
                return true;
                
            case R.id.settings:
            	intent = new Intent();
                intent.setClass(MainActivity.this, SetPreferenceActivity.class);
                startActivityForResult(intent, 0); 
                return true;
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onPrepareOptionsMenu (Menu menu){
    	/*
    	menu.clear();
    	buildMenu(menu);
    	*/
    	return super.onPrepareOptionsMenu(menu);
    }
   
    private void buildMenu(Menu menu) { 
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
      	
    	// Depending on the refresh state,  add refresh or stop-refresh menu option
    	if (!mUpdatesRequested) {
    		menu.add(Menu.NONE, MENU_UPDATES, Menu.NONE, R.string.start_updates)
    			.setIcon(R.drawable.refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    	} else {
    		menu.add(Menu.NONE, MENU_UPDATES, Menu.NONE, R.string.stop_updates)
    			.setIcon(R.drawable.cancel).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    	}

	}

	/*
     * Called when the Activity is no longer visible at all.
     * Stop updates and disconnect.
     */
    @Override
    public void onStop() {

        // If the client is connected
        if (mLocationClient.isConnected()) {
            stopPeriodicUpdates();
        }

        // After disconnect() is called, the client is considered "dead".
        mLocationClient.disconnect();
        
        super.onStop();

    }
    /*
     * Called when the Activity is going into the background.
     * Parts of the UI may be visible, but the Activity is inactive.
     */
    @Override
    public void onPause() {

        // Save the current setting for updates & bearing
        mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, mUpdatesRequested);
        mEditor.putFloat(LocationUtils.KEY_BEARING, mBearing);
        mEditor.commit();
        ds.close();
        super.onPause();
    }

    /*
     * Called when the Activity is restarted, even before it becomes visible.
     */
    @Override
    public void onStart() {

        super.onStart();

        /*
         * Connect the client. Don't re-start any requests here;
         * instead, wait for onResume()
         */
        mLocationClient.connect();

    }
    /*
     * Called when the system detects that this Activity is now visible.
     */
    @Override
    public void onResume() {
    	ds.open();
        super.onResume();       
        loadPref();
        
    }
    
    @Override
    protected void onDestroy() {
    	// This caused the application to die when orientation changed 
    	//android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }
    
    @Override
    protected void onNewIntent(Intent intent) {

        
    }

    /*
     * Handle results returned to this Activity by other Activities started with
     * startActivityForResult(). In particular, the method onConnectionFailed() in
     * LocationUpdateRemover and LocationUpdateRequester may call startResolutionForResult() to
     * start an Activity that handles Google Play services problems. The result of this
     * call returns here, to onActivityResult.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        // Choose what to do based on the request code
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        // Log the result
                        Log.d(LocationUtils.APPTAG, getString(R.string.resolved));

                        // Display the result
                        //mConnectionState.setText(R.string.connected);
                        Toast.makeText(this, R.string.connected, Toast.LENGTH_SHORT).show();
                        mConnectionStatus = getString(R.string.resolved);
                    break;

                    // If any other result was returned by Google Play services
                    default:
                        // Log the result
                        Log.d(LocationUtils.APPTAG, getString(R.string.no_resolution));

                        // Display the result
                        //mConnectionState.setText(R.string.disconnected);
                        Toast.makeText(this, R.string.disconnected, Toast.LENGTH_SHORT).show();
                        mConnectionStatus = getString(R.string.no_resolution);

                    break;
                }
            
            case LocationUtils.SETTINGS_REQUEST:
            		loadPref();
            	break;
            case LocationUtils.SEARCH_REQUEST:
            	  if (resultCode == RESULT_OK) {
            		    if (intent.hasExtra("latitude") && 
            		    		intent.hasExtra("longtitude") &&
            		    		intent.hasExtra("altitude") &&
            		    		intent.hasExtra("location_name")) {
            		    	
            		    	double lat = Double.valueOf(intent.getExtras().getString("latitude"));
            		    	double lng = Double.valueOf(intent.getExtras().getString("longtitude"));
            		    	double alt = Double.valueOf(intent.getExtras().getString("altitude"));
            		    	
            		    	// Create a Place object
            		    	Place place = new Place(intent.getExtras().getString("location_name"), lat, lng, alt);
            		    	getAddress(place);
            		    	
            		    	// Create a Location object to position the camera
            		    	LatLng lat_lng = new LatLng(lat, lng);
		        		    Location location = new Location(intent.getExtras().getString("location_name"));
		        		    location.setLatitude(lat);
		        		    location.setLongitude(lng);
		        		    location.setBearing(0);
		        		    location.setAltitude(alt);
		        		    updateCamera(location);
		        		    
		        		    // Add a marker on the Map
		        		    mMarker = map.addMarker(new MarkerOptions()
		        		    	.position(lat_lng)
		        		    	.icon(BitmapDescriptorFactory.fromResource(R.drawable.search)));
		        		    
		        		    // Add the marker and its place to the map
		        		    markers.put(mMarker.getId(), place);
            		    }
        		  }
            	break;
            // If any other request code was received
            default:
            	// through unsupported requestCode
            	Log.e(LocationUtils.APPTAG, "Unsupported requestCode " + requestCode);
            	break;
        }
    }
    
    private void loadPref(){
   	
        // If the app already has a setting for getting location updates, get it
        if (mPrefs.contains(LocationUtils.KEY_UPDATES_REQUESTED)) {
            mUpdatesRequested = mPrefs.getBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);

        // Otherwise, turn off location updates until requested
        } else {
            mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);
            mEditor.commit();
        }
        
        // If the app already has a setting for bearing, get it
        if (mPrefs.contains(LocationUtils.KEY_BEARING)) {
            mBearing = mPrefs.getFloat(LocationUtils.KEY_BEARING, 0f);

        // Otherwise add bearing of zero == no bearing
        } else {
            mEditor.putFloat(LocationUtils.KEY_BEARING, 0f);
            mEditor.commit();
        }
        
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);       
        if (mySharedPreferences.contains("traffic")) {
        	map.setTrafficEnabled(mySharedPreferences.getBoolean("traffic", false));
        }
        
        if (mySharedPreferences.contains("buildings")) {
        	map.setBuildingsEnabled(mySharedPreferences.getBoolean("buildings", false));
        }

        if (mySharedPreferences.contains("indoor")) {
        	map.setIndoorEnabled(mySharedPreferences.getBoolean("indoor", false));
        }

        if (mySharedPreferences.contains("maptype")) {
			String sMapType = mySharedPreferences.getString("maptype", "1");
        	map.setMapType(Integer.parseInt(sMapType));

        }
    }
    	        


   
    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(LocationUtils.APPTAG, getString(R.string.play_services_available));

            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
            }
            return false;
        }
    }

    /**
     * Invoked by the "Get Location" action.
     *
     * Calls getLastLocation() to get the current location
     *
     */
    public void getLocation() {

    	// If Google Play Services is available
        if (servicesConnected()) {

            // Get the current location
            Location cr = mLocationClient.getLastLocation();
            LatLng lat_lng = new LatLng(cr.getLatitude(), cr.getLongitude());
            Place place = new Place(cr.getLatitude(), cr.getLongitude(), cr.getAltitude());

            // Position the map to my current location and zoom in
            updateCamera(cr);
            
            // Get address async
            getAddress(place);
                
            mMarker = map.addMarker(new MarkerOptions()
            	.position(lat_lng)
            	.icon(BitmapDescriptorFactory.fromResource(R.drawable.action_marker))
            	.alpha(0.9f));
            
            markers.put(mMarker.getId(), place);
        }
    }


    // For Eclipse with ADT, suppress warnings about Geocoder.isPresent()
    @SuppressLint("NewApi")
    private void getAddress(Place place) {

        // In Gingerbread and later, use Geocoder.isPresent() to see if a geocoder is available.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && !Geocoder.isPresent()) {
            // No geocoder is present. Issue an error message
            Toast.makeText(this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
            return;
        }

        if (servicesConnected()) {

            // Start the background task
            (new MainActivity.GetAddressTask(this)).execute(place);
        }
    }

    /**
     * Invoked by the "Start Updates" button
     * Sends a request to start location updates
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    public void startUpdates() {
        mUpdatesRequested = true;

        if (servicesConnected()) {
            startPeriodicUpdates();
        }
    }

    /**
     * Invoked by the "Stop Updates" button
     * Sends a request to remove location updates
     * request them.
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    public void stopUpdates() {
        mUpdatesRequested = false;
        
        if (servicesConnected()) {
            stopPeriodicUpdates();
        }
    }

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        mConnectionStatus = getString(R.string.connected);

        if (mUpdatesRequested) {
            startPeriodicUpdates();
        }
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        mConnectionStatus = getString(R.string.disconnected);
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */

            } catch (IntentSender.SendIntentException e) {

                // Log the error
                e.printStackTrace();
            }
        } else {

            // If no resolution is available, display a dialog to the user with the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    /**
     * Report location updates to the UI.
     *
     * @param location The updated location.
     */
    @Override
    public void onLocationChanged(Location location) {

        // Report to the UI that the location was updated
        mConnectionStatus = getString(R.string.location_updated);
             
        // In the UI, set the speed
        mSpeed.setText(LocationUtils.getSpeed(this, location));
        
        // Set the location on the map
        updateCamera(location);
       
    }

    /**
     * In response to a request to start updates, send a request
     * to Location Services
     */
    private void startPeriodicUpdates() {

        mLocationClient.requestLocationUpdates(mLocationRequest, this);
        Toast.makeText(this, R.string.location_requested, Toast.LENGTH_SHORT).show();
    }

    /**
     * In response to a request to stop updates, send a request to
     * Location Services
     */
    private void stopPeriodicUpdates() {
        mLocationClient.removeLocationUpdates(this);
        //mConnectionState.setText(R.string.location_updates_stopped);
        Toast.makeText(this, R.string.location_updates_stopped, Toast.LENGTH_SHORT).show();
        mConnectionStatus = null;
        mSpeed.setText("");
    }
    
    /**
     * Updating the map view by moving the camera.
     * 
     * @param location
     */
    private void updateCamera(Location location) {
    	
    	if (location != null) {
    		if (location.hasBearing()) 	mBearing = location.getBearing();
	    	CameraPosition currentPlace = new CameraPosition.Builder()
	                .target(new LatLng(location.getLatitude(), location.getLongitude()))
	                .bearing(mBearing).zoom(LocationUtils.mapZoom(this, location)).build();
	    	map.moveCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
    	}
    }

    /**
     * An AsyncTask that calls getFromLocation() in the background.
     * The class uses the following generic types:
     * Place - A Place object containing the current location,
     *            passed as the input parameter to doInBackground()
     * Void     - indicates that progress units are not used by this subclass
     * Address  - An Address Object passed to onPostExecute()
     */
    protected class GetAddressTask extends AsyncTask<Place, Void, Address> {

        // Store the context passed to the AsyncTask when the system instantiates it.
        Context localContext;

        // Constructor called by the system to instantiate the task
        public GetAddressTask(Context context) {

            // Required by the semantics of AsyncTask
            super();

            // Set a Context for the background task
            localContext = context;
        }

        /**
         * Get a geocoding service instance, pass latitude and longitude to it, format the returned
         * address, and return the address to the UI thread.
         */
        @Override
        protected Address doInBackground(Place... params) {
            /*
             * Get a new geocoding service instance, set for localized addresses. This example uses
             * android.location.Geocoder, but other geocoders that conform to address standards
             * can also be used.
             */
        	
        	// Set Locale to Hebrew
        	Locale mLocale = new Locale("iw");
            Geocoder geocoder = new Geocoder(localContext, mLocale);

            // Get the current location from the input parameter list
            Place place = params[0];

            // Create a list to contain the result address
            List <Address> addresses = null;

            // Try to get an address for the current location. Catch IO or network problems.
            try {

                /*
                 * Call the synchronous getFromLocation() method with the latitude and
                 * longitude of the current location. Return at most 1 address.
                 */
                addresses = geocoder.getFromLocation(place.get_latitude(), place.get_longtitude(), 1);

                // Catch network or other I/O problems.
                } catch (IOException exception1) {

                    // Log an error and return an error message
                    Log.e(LocationUtils.APPTAG, getString(R.string.IO_Exception_getFromLocation));

                    // print the stack trace
                    exception1.printStackTrace();

                    // Return an error message
                    //return (getString(R.string.IO_Exception_getFromLocation));
                    //return (getString(R.string.try_again));
                    return null;

                // Catch incorrect latitude or longitude values
                } catch (IllegalArgumentException exception2) {

                    // Construct a message containing the invalid arguments
                    String errorString = getString(
                            R.string.illegal_argument_exception,
                            place.get_latitude(),
                            place.get_longtitude()
                    );
                    // Log the error and print the stack trace
                    Log.e(LocationUtils.APPTAG, errorString);
                    exception2.printStackTrace();

                    //
                    //return errorString;
                    return null;
                }
            
                // If the reverse geocode returned an address
                if (addresses != null && addresses.size() > 0) {

                    // Set the Address in the Place
                	place.set_address(addresses.get(0));
                	// Return an Address Object
                    return addresses.get(0);

                // If there aren't any addresses, post a message
                } else {
                  //return getString(R.string.no_address_found);
                	Log.e(LocationUtils.APPTAG, getString(R.string.no_address_found));
                	return null;
                }
        }

        /**
         * A method that's called once doInBackground() completes. Set the text of the
         * UI element that displays the address. This method runs on the UI thread.
         */
        @Override
        protected void onPostExecute(Address address) {

            // Set the address in the UI
            //infoWindow.setAddress(address);
            
            
        }
    }

    /**
     * Show a dialog returned by Google Play services for the
     * connection error code
     *
     * @param errorCode An error code returned from onConnectionFailed
     */
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
            errorCode,
            this,
            LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
        }
    }

    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
    public static Place getPlace(String markerId) {
    	if (markers.containsKey(markerId)) {
    		return markers.get(markerId);
    	} else {
    		return null;
    	}
    }
}
