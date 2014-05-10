package kr.unipi.foodwith;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.skp.Tmap.TMapGpsManager.onLocationChangedCallback;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;
import com.skp.openplatform.android.sdk.api.APIRequest;
import com.skp.openplatform.android.sdk.common.PlanetXSDKConstants.CONTENT_TYPE;
import com.skp.openplatform.android.sdk.common.PlanetXSDKException;
import com.skp.openplatform.android.sdk.common.RequestBundle;
import com.skp.openplatform.android.sdk.common.ResponseMessage;
import com.skp.openplatform.android.sdk.common.PlanetXSDKConstants.HttpMethod;
import com.skp.openplatform.android.sdk.common.RequestListener;

public class SelectActivity extends Activity implements OnClickListener,
		LocationListener {

	APIRequest api;
	RequestBundle requestBundle;
	String url = "https://apis.skplanetx.com/tmap/geo/reversegeocoding";
	HashMap<String, Object> param;

	String jsonResult;

	SharedPreferences pref;
	SharedPreferences.Editor editor;

	Button btnKoreanFood;
	Button btnWesternFood;
	Button btnChineseFood;
	Button btnJapaneseFood;
	Button btnFlourFood;
	Button btnMidnightFood;
	Button btnRefresh;

	TextView tvLocation;

	JSONObject json;

	Location location;
	LocationManager locManager;
	String locProvider;

	double lat;
	double lon;

	String fullAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);

		initUi();
		checkGps();
		allocBtn();
		networking();
		tvLocation.setText(pref.getString("location", "Press To Refresh"));
	}

	public void initUi() {
		requestBundle = new RequestBundle();

		locManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);

		tvLocation = (TextView) findViewById(R.id.TV_LOCATION);
		btnKoreanFood = (Button) findViewById(R.id.BTN_KOREAN_FOOD);
		btnWesternFood = (Button) findViewById(R.id.BTN_WESTERN_FOOD);
		btnChineseFood = (Button) findViewById(R.id.BTN_CHINESE_FOOD);
		btnJapaneseFood = (Button) findViewById(R.id.BTN_JAPANESE_FOOD);
		btnFlourFood = (Button) findViewById(R.id.BTN_FLOUR_FOOD);
		btnMidnightFood = (Button) findViewById(R.id.BTN_MIDNIGHT_FOOD);
		btnRefresh = (Button) findViewById(R.id.BTN_REFRESH);

		pref = getSharedPreferences("pref", 0);
		editor = pref.edit();
		try {
			json = new JSONObject(pref.getString("result", "Prefresh  to Refresh"));
			String profile = json.getString("profile");

			json = new JSONObject(profile);
			String userName = json.getString("userName");

			Log.d("FOOD", "userName : " + userName);

			editor.putString("userName", userName);
			editor.commit();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void allocBtn() {
		Button btns[] = { btnKoreanFood, btnWesternFood, btnChineseFood,
				btnJapaneseFood, btnFlourFood, btnMidnightFood, btnRefresh };

		for (Button btn : btns) {
			btn.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.BTN_KOREAN_FOOD:

			break;

		case R.id.BTN_WESTERN_FOOD:

			break;

		case R.id.BTN_CHINESE_FOOD:

			break;

		case R.id.BTN_JAPANESE_FOOD:

			break;

		case R.id.BTN_FLOUR_FOOD:

			break;

		case R.id.BTN_MIDNIGHT_FOOD:

			break;

		case R.id.BTN_REFRESH:
			checkGps();
			networking();
			break;
		}
	}

	public void networking() {
		Log.d("FOOD", "lat : " + lat + " lon : " + lon);
		api = new APIRequest();
		APIRequest.setAppKey("c836fef9-1991-33b9-b131-7da768df3077");

		param = new HashMap<String, Object>();
		param.put("version", 1);
		param.put("lat", lat);
		param.put("lon", lon);
		param.put("coordType", "WGS84GEO");

		requestBundle.setUrl(url);
		requestBundle.setParameters(param);
		requestBundle.setHttpMethod(HttpMethod.GET);
		requestBundle.setResponseType(CONTENT_TYPE.JSON);

		new GetAsyncTask().execute();
	}

	private class GetAsyncTask extends AsyncTask<String, Integer, Long> {

		@Override
		protected Long doInBackground(String... params) {
			try {
				api.request(requestBundle, reqListener);
			} catch (PlanetXSDKException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			tvLocation.setText(pref.getString("location", "Press To Refresh"));
		}
	}

	public void checkGps() {
		if (locManager.isProviderEnabled(locManager.NETWORK_PROVIDER) == true) {
			locProvider = LocationManager.NETWORK_PROVIDER;
		} else
			locProvider = LocationManager.GPS_PROVIDER;

		locManager.requestLocationUpdates(locProvider, 1111111, 111111, this);
		location = locManager.getLastKnownLocation(locProvider);
		lat = location.getLatitude();
		lon = location.getLongitude();
	}

	public void parseJson() {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(pref.getString("location", "Press To Refresh"));
			String addressInfo = jsonObject.getString("addressInfo");
			jsonObject = new JSONObject(addressInfo);
			fullAddress = jsonObject.getString("fullAddress");

			editor.putString("location", fullAddress);
			editor.commit();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	RequestListener reqListener = new RequestListener() {

		@Override
		public void onPlanetSDKException(PlanetXSDKException e) {
			e.printStackTrace();
		}

		@Override
		public void onComplete(ResponseMessage result) {
			jsonResult = result.toString();
			editor.putString("location", jsonResult);
			editor.commit();
			parseJson();
		}
	};

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}
