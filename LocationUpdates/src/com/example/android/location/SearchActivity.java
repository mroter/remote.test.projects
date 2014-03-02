package com.example.android.location;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends Activity  {

	private LocationDataSource ds;
	private ListView mListView;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
	
		mListView = (ListView) findViewById(R.id.list);
		
        // Open the local locations DB
        ds = new LocationDataSource(this);
        ds.open();
        
		handleIntent(getIntent());
             
	}
	
    @Override
    protected void onNewIntent(Intent intent) {
    	setIntent(intent);
        handleIntent(intent);
    }
    
    /**
     * Get the intent, verify the action and get the query
     * @param intent
     */
	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// handles a search query
		    String query = intent.getStringExtra(SearchManager.QUERY);
		    doMySearch(query);
		}		
	}

	private void doMySearch(String query) {
		 
        // Specify the columns we want to display in the result
        String[] from = new String[] { DBHelper.COLUMN_NAME,
                                       DBHelper.COLUMN_LATITUDE,
                                       DBHelper.COLUMN_LONGTITUDE,
                                       DBHelper.COLUMN_ALTITUDE};

        // Specify the corresponding layout elements where we want the columns to go
        int[] to = new int[] { R.id.result_name,
                               R.id.result_latitude,
                               R.id.result_longtitude,
                               R.id.result_altitude};
        
        
        // Create a simple cursor adapter for the definitions and apply them to the ListView
        @SuppressWarnings("deprecation")
		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.result, ds.findLocation(query), from, to);
              
        mListView.setAdapter(adapter);
        
        // Define the on-click listener for the list items
        mListView.setOnItemClickListener(new OnItemClickListener() {
        	
        	@Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	String location_name = ((TextView) view.findViewById(R.id.result_name)).getText().toString();
	        	String latitude = ((TextView) view.findViewById(R.id.result_latitude)).getText().toString();
	        	String longtitude = ((TextView) view.findViewById(R.id.result_longtitude)).getText().toString();
	        	String altitude = ((TextView) view.findViewById(R.id.result_altitude)).getText().toString();
	        	     	
	        	Intent intent = new Intent(view.getContext(), MainActivity.class);
	        	intent.putExtra("location_name", location_name);
	        	intent.putExtra("latitude", latitude);
	        	intent.putExtra("longtitude", longtitude);
	        	intent.putExtra("altitude", altitude);
	        	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        	
	        	startActivity(intent);
	        	finish();      
	        }
	    });
        

        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
    		@Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
    			 final int _id = (int) id;
    			 final View _arg1 = arg1;
    	    	// display delete confirmation dialog
    			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(arg1.getContext());
    			alertDialogBuilder
    				.setTitle("Delete Location")
    				.setMessage("Are you sure you want to delete this location?")
    				.setCancelable(false)
    				.setPositiveButton("OK",
    				  new DialogInterface.OnClickListener() {
    				    public void onClick(DialogInterface dialog, int id) {

    		    			ds.deleteLocation(_id);
    		    			Toast.makeText(_arg1.getContext(), "Location deleted", Toast.LENGTH_SHORT).show();
    		    			adapter.notifyDataSetChanged();
    		    			// TODO remove the item and return to the list
    		    			finish();  
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

                return true;
            }
        }); 
        

	}


}
