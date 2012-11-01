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
import android.widget.TextView;

public class GetLocation extends Activity {

	private LocationManager locationManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lbs);

		 locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// check if location services is enabled
		final boolean gpsEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!gpsEnabled) {
			// Alert user that location settings is disabled
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.app_name));
			builder.setPositiveButton(getString(android.R.string.yes),
					new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Open Location services
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

	LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			displayLocation(location);
			locationManager.removeUpdates(this);
		}

		public void onProviderDisabled(String provider) {}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};

	private void displayLocation(Location location) {
		TextView locationTextView = (TextView) findViewById(R.id.location_text_view);
		locationTextView.setText("Location: (" + location.getLatitude() + ", " + location.getLongitude() + ")");
	}
}
