package kr.unipi.foodwith;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapGpsManager.onLocationChangedCallback;
import com.skp.Tmap.TMapView;

public class MapActivity extends Activity implements onLocationChangedCallback{
	double lat;
	double lon;

	TMapView mapView;
	TMapGpsManager mapGps;

	LinearLayout linear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUi();
		new setApiAsync().execute();
		setContentView(linear);
	}

	public void initUi() {
		linear = new LinearLayout(this);
		mapView = new TMapView(this);
		
		mapView.setLanguage(mapView.LANGUAGE_KOREAN);
		mapView.setMapType(mapView.MAPTYPE_STANDARD);
		mapView.setIconVisibility(true);
		linear.addView(mapView);

		mapGps = new TMapGpsManager(this);
		mapGps.setProvider(mapGps.GPS_PROVIDER);
		mapGps.OpenGps();
	}

	@Override
	public void onLocationChange(Location location) {
		lat = location.getLatitude();
		lon = location.getLongitude();
		mapView.setLocationPoint(lat, lon);
	}
	class setApiAsync extends AsyncTask<String, Integer, Long> {

		@Override
		protected Long doInBackground(String... params) {
			mapView.setSKPMapApiKey("c836fef9-1991-33b9-b131-7da768df3077");
			return null;
		}
	}
}
