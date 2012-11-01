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

public class FoursquareExampleActivity extends Activity {

	private LocationManager locationManager;
	private Location location;

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
		// TODO
		
	}
	
}
