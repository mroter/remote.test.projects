package com.example.android.location;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

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

        handleIntent(intent);
    }
    
    /**
     * Get the intent, verify the action and get the query
     * @param intent
     */
	private void handleIntent(Intent intent) {
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // handles a click on a search suggestion; 
			//doMySearch("");
     
		} else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
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
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
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
	        	//startActivity(intent);
	        	
	        	setResult(RESULT_OK, intent);
	        	finish();      
	        }
	    });
        
        
        adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.search, menu);
		
	  	 if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){		 
	            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	            SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	            searchView.setIconifiedByDefault(false);
	        }

		return true;
	}
	
	   @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case R.id.action_search:
	                onSearchRequested();
	                return true;
	            default:
	                return false;
	        }
	    }
}
