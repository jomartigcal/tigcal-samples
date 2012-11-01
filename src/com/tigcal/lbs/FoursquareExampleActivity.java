package com.tigcal.lbs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;

public class FoursquareExampleActivity extends Activity {

	private LocationManager locationManager;

	LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			displayPlaces(location);
			locationManager.removeUpdates(this);
		}

		public void onProviderDisabled(String provider) {}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_foursquare);

		displayFoursquareVenues();
	}

	private void displayFoursquareVenues() {
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		final boolean gpsEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!gpsEnabled) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.app_name));
			builder.setPositiveButton(getString(android.R.string.yes),
					new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent settingsIntent = new Intent(
							Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(settingsIntent);
				}
			});
			builder.setMessage("Unable to get location. Please turn on location services.");
			builder.show();
		} else {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		}

		final boolean networkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (networkEnabled) {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		}
	}
	
	private void displayPlaces(Location location) {
		FoursquareApi foursquareApi = new FoursquareApi(getString(R.string.foursquare_client_id), getString(R.string.foursquare_client_secret), "http://www.example.com");
		try {
			Result<VenuesSearchResult> results = foursquareApi.venuesSearch(String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude()), null, null, null, null, null, null, null, null, null, null, 
					500, null);
			CompactVenue[] venues = results.getResult().getVenues();
			updatePlacesList(venues);
		} catch (FoursquareApiException exception) {
			Log.d("Foursquare", exception.getMessage());
			Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
		}
		
	}

	private void updatePlacesList(CompactVenue[] venues) {
		ListView listView = (ListView) findViewById(R.id.venues_list);
		listView.setAdapter(new VenuesAdapter(this, venues));
	}
	
	private class VenuesAdapter extends ArrayAdapter<CompactVenue> {

		public VenuesAdapter(Context context, CompactVenue[] venues) {
			super(context, R.layout.list_item_foursquare, R.id.venue_name, venues);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view =  super.getView(position, convertView, parent);
			
			CompactVenue infoPage = (CompactVenue) getItem(position);
			if(infoPage != null) {
				TextView venueName = (TextView) view.findViewById(R.id.venue_name);
				venueName.setText(infoPage.getName());
			}
			
			return view;
		}
	}
}
