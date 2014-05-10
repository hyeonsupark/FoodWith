package kr.unipi.foodwith;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.TimePicker.OnTimeChangedListener;

public class PostActivity extends Activity{
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
	
	int mHour;
	int mMinute;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);
		initUi();
		/**
		 * SharedPreference
		 * title : 제목
		 * article : 내용
		 * people : 인원
		 * category : 음식 카테고리
		 * time : 시간 XX:XX
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
		if( title.equals(null) || article.equals("") ) {
			Toast.makeText(this, "공백을 채워주세요", Toast.LENGTH_SHORT).show();
		} else {
			editor.putString("title", title);
			editor.putString("article", article);
			editor.putString("time", mHour + ":" + mMinute);
			editor.commit();
			Log.d("FOOD", pref.getString("title", "title"));
			Log.d("FOOD", pref.getString("article", "article"));
			Log.d("FOOD", pref.getString("time", "time"));
			
			finish();
		}
		
		return super.onOptionsItemSelected(item);
	}

	public void initUi() {
		pref = getSharedPreferences("post", MODE_PRIVATE);
		editor = pref.edit();
		
		peopleSpinner = (Spinner) findViewById(R.id.SPINNER_PEOPLE);
		categorySpinner = (Spinner) findViewById(R.id.SPINNER_CATEGORY);
		
		etTitle = (EditText) findViewById(R.id.ET_TITLE);
		etArticle = (EditText) findViewById(R.id.ET_ARTICLE);
		
		timePicker = (TimePicker) findViewById(R.id.timePicker1);
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
		peopleAdapter = ArrayAdapter.createFromResource(this, R.array.num, android.R.layout.simple_spinner_item);
		peopleAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		peopleSpinner.setAdapter(peopleAdapter);
		peopleSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int pos, long id) {
				editor.putString("people", (String) peopleAdapter.getItem(pos));
				editor.commit();
				Log.d("FOOD", "people : " + pref.getString("people", "999"));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		categorySpinner.setPrompt("카테고리를 선택해주세요");
		categoryAdapter = ArrayAdapter.createFromResource(this, R.array.category, android.R.layout.simple_spinner_item);
		categoryAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		categorySpinner.setAdapter(categoryAdapter);
		categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int pos, long id) {
						editor.putString("category", (String) categoryAdapter.getItem(pos));
						editor.commit();
						Log.d("FOOD", "category : " + pref.getString("category", "999"));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
