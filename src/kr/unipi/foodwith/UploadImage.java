package kr.unipi.foodwith;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class UploadImage extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... params) {
		HttpClient httpClient = new DefaultHttpClient();
	    HttpContext localContext = new BasicHttpContext();
	    HttpPost httpPost = new HttpPost("http://0x0.kr/belaunch/uploadimage.php");

	    try {
	        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

	        entity.addPart("image", new FileBody(new File(params[0])));
	        
	        httpPost.setEntity(entity);

	        HttpResponse response = httpClient.execute(httpPost, localContext);
	        JSONObject res = new JSONObject(EntityUtils.toString(response.getEntity()));
	        if(res.get("type").equals("accept"))
	        	return null;
	        else
	        	return res.get("msg").toString();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return e.getMessage();
	    }
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
	}
}
