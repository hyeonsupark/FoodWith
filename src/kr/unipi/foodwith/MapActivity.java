package kr.unipi.foodwith;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapGpsManager.onLocationChangedCallback;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;
import com.skp.Tmap.TMapView.OnClickListenerCallback;
import com.skp.openplatform.android.sdk.api.APIRequest;
import com.skp.openplatform.android.sdk.common.PlanetXSDKException;
import com.skp.openplatform.android.sdk.common.RequestBundle;
import com.skp.openplatform.android.sdk.common.ResponseMessage;
import com.skp.openplatform.android.sdk.common.PlanetXSDKConstants.CONTENT_TYPE;
import com.skp.openplatform.android.sdk.common.PlanetXSDKConstants.HttpMethod;
import com.skp.openplatform.android.sdk.common.RequestListener;

public class MapActivity extends Activity implements onLocationChangedCallback,
		OnClickListenerCallback
{

	APIRequest api;
	RequestBundle requestBundle;
	String url = "https://apis.skplanetx.com/tmap/geo/reversegeocoding";
	HashMap<String, Object> param;

	double lat;
	double lon;

	TMapView mapView;
	TMapGpsManager mapGps;

	LinearLayout linear;

	SharedPreferences pref;
	SharedPreferences.Editor editor;

	ProgressDialog dlg;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getActionBar().setBackgroundDrawable(new ColorDrawable(0xffeb5655));
		initUi();
		dlg.show();
		new setApiAsync().execute();

		mapView.setOnClickListenerCallBack(this);

		pref = getSharedPreferences("pref", MODE_PRIVATE);
		editor = pref.edit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		networking();
		dlg.show();
		return super.onOptionsItemSelected(item);
	}

	public void initUi()
	{
		requestBundle = new RequestBundle();

		dlg = new ProgressDialog(this);
		dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dlg.setMessage("잠시만 기다려주세요..");

		linear = new LinearLayout(this);
		mapView = new TMapView(this);

		mapView.setLanguage(mapView.LANGUAGE_KOREAN);
		mapView.setMapType(mapView.MAPTYPE_STANDARD);
		mapView.setIconVisibility(true);
		linear.addView(mapView);

		mapGps = new TMapGpsManager(this);
		mapGps.setProvider(mapGps.GPS_PROVIDER);
		mapGps.OpenGps();

		mapView.addMarkerItem("ClickMarker", clickMarker);
	}

	public void networking()
	{
		Log.d("networking", "yes");
		api = new APIRequest();
		APIRequest.setAppKey("c836fef9-1991-33b9-b131-7da768df3077");

		param = new HashMap<String, Object>();
		param.put("version", 1);
		param.put("lat", Double.parseDouble(pref.getString("placelat", null)));
		param.put("lon", Double.parseDouble(pref.getString("placelon", null)));
		param.put("coordType", "WGS84GEO");

		requestBundle.setUrl(url);
		requestBundle.setParameters(param);
		requestBundle.setHttpMethod(HttpMethod.GET);
		requestBundle.setResponseType(CONTENT_TYPE.JSON);
		
		new GetAsyncTask().execute();
	}

	RequestListener reqListener = new RequestListener()
	{

		@Override
		public void onPlanetSDKException(PlanetXSDKException w)
		{
			w.printStackTrace();
		}

		@Override
		public void onComplete(ResponseMessage arg0)
		{
			Log.d("oncomplete", "yes");

			String jsonResult = arg0.toString();
			try
			{
				JSONObject jsonObj = new JSONObject(jsonResult);
				String info = jsonObj.getString("addressInfo");
				jsonObj = new JSONObject(info);
				String poiAddress = jsonObj.getString("fullAddress");
				editor.putString("poiAddress", poiAddress);
				editor.commit();
				Log.d("poiAddress", pref.getString("poiAddress", null));
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	};

	@Override
	public void onLocationChange(Location location)
	{
		lat = location.getLatitude();
		lon = location.getLongitude();
		mapView.setLocationPoint(lat, lon);
	}

	class setApiAsync extends AsyncTask<String, Integer, Long>
	{

		@Override
		protected Long doInBackground(String... params)
		{
			mapView.setSKPMapApiKey("c836fef9-1991-33b9-b131-7da768df3077");
			return null;
		}

		@Override
		protected void onPostExecute(Long result)
		{
			setContentView(linear);
			dlg.dismiss();
			super.onPostExecute(result);
		}
	}

	TMapMarkerItem clickMarker = new TMapMarkerItem();

	@Override
	public boolean onPressEvent(ArrayList<TMapMarkerItem> arg0,
			ArrayList<TMapPOIItem> arg1, final TMapPoint tp, PointF pf)
	{
		new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected Void doInBackground(Void... params)
			{
				mapView.setCenterPoint(tp.getLongitude(), tp.getLatitude(),
						true);
				Log.d("placelat", "" + tp.getLatitude());
				Log.d("placelon", "" + tp.getLongitude());
				clickMarker.setTMapPoint(tp);
				clickMarker.setVisible(clickMarker.VISIBLE);
				editor.putString("placelat", Double.toString(tp.getLatitude()));
				editor.putString("placelon", Double.toString(tp.getLongitude()));
				editor.commit();
				return null;
			}
			
			protected void onPostExecute(Void result) {
			};
		}.execute();
		return true;
	}
	
	class GetAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params)
		{
			try
			{
				Log.d("apiRequest", "yes");

				api.request(requestBundle, reqListener);
			}
			catch (PlanetXSDKException e)
			{
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			dlg.dismiss();
			finish();
			super.onPostExecute(result);
		}
	}

	@Override
	public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arg0,
			ArrayList<TMapPOIItem> arg1, TMapPoint arg2, PointF arg3)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
