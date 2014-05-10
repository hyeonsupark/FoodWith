package kr.unipi.foodwith;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.skp.openplatform.android.sdk.api.APIRequest;
import com.skp.openplatform.android.sdk.common.PlanetXSDKConstants.CONTENT_TYPE;
import com.skp.openplatform.android.sdk.common.PlanetXSDKConstants.HttpMethod;
import com.skp.openplatform.android.sdk.common.PlanetXSDKException;
import com.skp.openplatform.android.sdk.common.RequestBundle;
import com.skp.openplatform.android.sdk.common.RequestListener;
import com.skp.openplatform.android.sdk.common.ResponseMessage;
import com.skp.openplatform.android.sdk.oauth.OAuthInfoManager;
import com.skp.openplatform.android.sdk.oauth.OAuthListener;
import com.skp.openplatform.android.sdk.oauth.PlanetXOAuthException;

public class MainActivity extends Activity {

	APIRequest api;
	RequestBundle requestBundle;
	String url = "https://apis.skplanetx.com/users/me/profile";
	HashMap<String, Object> param;

	public static String result;

	SharedPreferences pref;
	SharedPreferences.Editor editor;

	ProgressDialog dlg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initOAuthData();
		initUI();

		goNextActivity();

	}

	public void goNextActivity() {
		if (pref.getBoolean("loginFlag", true)) {
			try {
				OAuthInfoManager.login(this, oauthListener);
				OAuthInfoManager.saveOAuthInfo();
				
				dlg.show();

			} catch (PlanetXOAuthException e) {
				e.printStackTrace();
			}

		} else {
			Handler mHandler = new Handler();
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					Intent mIntent = new Intent(MainActivity.this,
							SelectActivity.class);
					startActivity(mIntent);
					finish();
				}
			}, 1000);
		}
	}

	public void initUI() {
		dlg = new ProgressDialog(this);
		dlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pref = getSharedPreferences("pref", 0);
		editor = pref.edit();
	}

	public void initOAuthData() {

		APIRequest.setAppKey("c836fef9-1991-33b9-b131-7da768df3077");
		OAuthInfoManager.clientId = "7bfd9dff-41f5-3a40-b87f-ce23fa6ec8e2";
		OAuthInfoManager.clientSecret = "ee7f4c75-87f5-3de5-ab2a-1ba47071d0bd";
		OAuthInfoManager.scope = "user";
	}

	public void networking() {

		api = new APIRequest();
		// 보낼 파라미터
		param = new HashMap<String, Object>();
		param.put("version", 1);

		// 파라미터를 담아줌
		requestBundle = new RequestBundle();
		requestBundle.setUrl(url);
		requestBundle.setParameters(param);
		requestBundle.setHttpMethod(HttpMethod.GET);
		requestBundle.setResponseType(CONTENT_TYPE.JSON);

		// Request
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
			Intent mIntent = new Intent(MainActivity.this, SelectActivity.class);
			startActivity(mIntent);
			finish();
		}
	}

	RequestListener reqListener = new RequestListener() {

		@Override
		public void onPlanetSDKException(PlanetXSDKException arg0) {

		}

		@Override
		public void onComplete(ResponseMessage arg0) {
			result = arg0.toString();
			editor.putString("result", result);
			editor.commit();
			Log.d("FOOD", pref.getString("result", result));
		}
	};

	OAuthListener oauthListener = new OAuthListener() {

		@Override
		public void onError(String arg0) {
			Log.d("FOOD", "onError : " + arg0);
		}

		@Override
		public void onComplete(String arg0) {

			Log.d("FOOD", "onComplete : " + arg0);
			networking();
			editor.putBoolean("loginFlag", false);
			editor.commit();
			dlg.dismiss();
		}
	};

}
