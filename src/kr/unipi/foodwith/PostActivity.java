package kr.unipi.foodwith;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.api.client.http.HttpResponse;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

public class PostActivity extends Activity {
	double lat;
	double lon;

	SharedPreferences pref;
	SharedPreferences.Editor editor;

	LinearLayout linear;

	Spinner peopleSpinner;
	Spinner categorySpinner;

	ArrayAdapter<CharSequence> peopleAdapter;
	ArrayAdapter<CharSequence> categoryAdapter;

	EditText etTitle;
	EditText etArticle;

	TimePicker timePicker;
	DatePicker datePicker;
	Button mapButton;

	int mHour;
	int mMinute;

	StringBuilder builder;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setBackgroundDrawable(new ColorDrawable(0xffeb5655));
		setContentView(R.layout.activity_post);
		initUi();
		
		/**
		 * SharedPreference title : 제목 article : 내용 people : 인원 category : 음식
		 * 카테고리 time : 시간 XX:XX
		 * 
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.post, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String title = etTitle.getText().toString();
		String article = etArticle.getText().toString();
		if (title.equals("") || article.equals("")) {
			Toast.makeText(this, "공백을 채워주세요", Toast.LENGTH_SHORT).show();
		} else {
			editor.putString("title", title);
			editor.putString("article", article);
			editor.putString("time", mHour + ":" + mMinute);
			editor.commit();
			Log.d("FOOD", pref.getString("title", "title"));
			Log.d("FOOD", pref.getString("article", "article"));
			Log.d("FOOD", pref.getString("time", "time"));
			new PostAsync().execute();
		}

		return super.onOptionsItemSelected(item);
	}

	public void initUi() {
		pref = getSharedPreferences("pref", MODE_PRIVATE);
		editor = pref.edit();
		builder = new StringBuilder();

		peopleSpinner = (Spinner) findViewById(R.id.SPINNER_PEOPLE);
		categorySpinner = (Spinner) findViewById(R.id.SPINNER_CATEGORY);

		etTitle = (EditText) findViewById(R.id.ET_TITLE);
		etArticle = (EditText) findViewById(R.id.ET_ARTICLE);

		timePicker = (TimePicker) findViewById(R.id.post_time);
		datePicker = (DatePicker) findViewById(R.id.post_date);
		datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
			
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				editor.putString("date", year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
				editor.commit();
			}
		});
		mHour = timePicker.getCurrentHour();
		mMinute = timePicker.getCurrentMinute();
		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				mHour = hourOfDay;
				mMinute = minute;
			}
		});
		peopleSpinner.setPrompt("인원을 선택해주세요");
		peopleAdapter = ArrayAdapter.createFromResource(this, R.array.num,
				android.R.layout.simple_spinner_item);
		peopleAdapter
				.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		peopleSpinner.setAdapter(peopleAdapter);
		peopleSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int pos,
					long id) {
				editor.putString("people", (String) peopleAdapter.getItem(pos));
				editor.commit();
				Log.d("FOOD", "people : " + pref.getString("people", "999"));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		mapButton = (Button)findViewById(R.id.post_mapbutton);
		mapButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(PostActivity.this, MapActivity.class);
				startActivity(intent);
			}
		});
		categorySpinner.setPrompt("카테고리를 선택해주세요");
		categoryAdapter = ArrayAdapter.createFromResource(this,
				R.array.category, android.R.layout.simple_spinner_item);
		categoryAdapter
				.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		categorySpinner.setAdapter(categoryAdapter);
		categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int pos,
					long id) {
				editor.putString("category",
						(String) categoryAdapter.getItem(pos));
				editor.commit();
				Log.d("FOOD", "category : " + pref.getString("category", "999"));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	class PostAsync extends AsyncTask<String, Integer, Long> {
		HttpClient hc = new DefaultHttpClient();
		HttpPost hp = new HttpPost("http://0x0.kr/belaunch/newpost.php");

		@Override
		protected Long doInBackground(String... params) {
			try {
				ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new BasicNameValuePair("title", "\""
						+ pref.getString("title", "제목") + "\""));
				list.add(new BasicNameValuePair("type", "\""
						+ pref.getString("category", "카테고리") + "\""));
				list.add(new BasicNameValuePair("count", "\""
						+ pref.getString("people", "999") + "\""));
				list.add(new BasicNameValuePair("date", "\""
						+ pref.getString("date", "null") + "\""));
				list.add(new BasicNameValuePair("time", "\""
						+ pref.getString("time", "00:00:00") + "\""));
				list.add(new BasicNameValuePair("author", "\""
						+ pref.getString("userName", "null") + "\""));
				list.add(new BasicNameValuePair("author_id", "\""
						+ pref.getString("email", "999") + "\""));
				list.add(new BasicNameValuePair("article", "\""
						+ pref.getString("article", "내용") + "\""));
				list.add(new BasicNameValuePair("placename", "\""
						+ pref.getString("poiAddress", "장소") + "\""));
				list.add(new BasicNameValuePair("placelat", "\""
						+ pref.getString("placelat", "0.0") + "\""));
				list.add(new BasicNameValuePair("placelon", "\""
						+ pref.getString("placelon", "0.0") + "\""));
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,
						"UTF-8");
				hp.setEntity(entity);

				org.apache.http.HttpResponse res = hc.execute(hp);

				String text = EntityUtils.toString(res.getEntity());
				Log.d("entity", EntityUtils.toString(entity));
				Log.d("FOOD", "http : " + text);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			finish();
		}

	}
}
