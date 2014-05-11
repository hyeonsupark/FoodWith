package kr.unipi.foodwith;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ListActivity extends Activity {

	ArrayList<PostLists> postList;
	ListAdapter listAdapter;

	ListView listView;

	SharedPreferences pref;
	SharedPreferences.Editor editor;

	int photo;
	String category;
	String title;
	String author;
	String people;
	String time;
	String place;
	
	String cat;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setBackgroundDrawable(new ColorDrawable(0xffeb5655));
		setContentView(R.layout.activity_list);
		Intent mIntent = new Intent(this.getIntent());
		cat = mIntent.getStringExtra("category");
		initUi();
		Log.d("FOOD", "Category : " + cat);
		
		
		new AsyncRequest().execute();
		
	}
	
	class AsyncRequest extends AsyncTask<Void, Void, Void> {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://0x0.kr/belaunch/getposts.php");
		ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
		
		@Override
		protected Void doInBackground(Void... params) {
			
			try {
				
				add("type", cat);
				
				
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
				post.setEntity(entity);
				org.apache.http.HttpResponse res = client.execute(post);
				
				String text = EntityUtils.toString(res.getEntity());
				Log.d("REQUEST", EntityUtils.toString(entity));
				Log.d("RESPONSE", text);
				
				
				JSONObject object = new JSONObject(text);
				
				// Success
				if(object.get("type").equals("accept"))
				{
					JSONArray array = object.getJSONArray("result");
					for(int i = 0; i < array.length(); ++i)
					{
						JSONObject post = array.getJSONObject(i);
						PostLists lists = new PostLists(post);

						postList.add(lists);
						Log.d("ADD", lists.toString());
					}
				}
				else
					Log.d("REQ", "엥");
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			listView.setAdapter(listAdapter);
			super.onPostExecute(result);
		}
		
		private void add(String name, String value) {
			list.add(new BasicNameValuePair(name, value));
		}
	}

	void initUi() {
		postList = new ArrayList<PostLists>();
		listAdapter = new ListAdapter(this, R.layout.list, postList);
		listView = (ListView) findViewById(R.id.listView1);
		pref = getSharedPreferences("pref", MODE_PRIVATE);

		category = pref.getString("category", "카테고리");
		title = pref.getString("title", "제목");
		author = pref.getString("userName", "개설자");
		people = pref.getString("people", "인원");
		time = pref.getString("time", "시간");
		place = pref.getString("poiAddress", "위치");
	}

}

// 글을 쓰면 preference에올라가며넛

class PostLists {
	int id;
	int count;
	public String title, article, placename;
	String author, author_id;
	String date, time;
	public double lat, lon;

	public PostLists(JSONObject object) throws JSONException {
		id = object.getInt("id");
		article = object.getString("article");
		count = object.getInt("count");
		title = object.getString("title");
		placename = object.getString("placename");
		author = object.getString("author");
		author_id = object.getString("author_id");
		date = object.getString("date");
		time = object.getString("time");
		lat = object.getDouble("lat");
		lon = object.getDouble("lon");
	}

	public int getId() {
		return id;
	}

	public int getCount() {
		return count;
	}

	public String getTitle() {
		return title;
	}

	public String getPlacename() {
		return placename;
	}

	public String getAuthor() {
		return author;
	}

	public String getAuthor_id() {
		return author_id;
	}

	public String getDate() {
		return date;
	}

	public String getTime() {
		return time;
	}

}

class ListAdapter extends BaseAdapter {
	LayoutInflater inflater;
	ArrayList<PostLists> postLists;
	int layout;
	Context context;
	
	public ListAdapter(Context context, int layout,
			ArrayList<PostLists> postLists) {
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.postLists = postLists;
		this.layout = layout;
	}

	@Override
	public int getCount() {
		return postLists.size();
	}

	@Override
	public Object getItem(int position) {
		return postLists.get(position).getTitle();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(layout, parent, false);
		}

		final PostLists postList = postLists.get(position);

		ImageView photo = (ImageView) convertView.findViewById(R.id.LIST_IMG);
		// photo.setImageResource(postList.getPhoto());

		TextView title = (TextView) convertView
				.findViewById(R.id.LIST_TV_TITLE);
		title.setText(postList.getTitle());

		TextView author = (TextView) convertView
				.findViewById(R.id.LIST_TV_AUTHOR);
		author.setText(postList.getAuthor());

		TextView people = (TextView) convertView
				.findViewById(R.id.LIST_TV_PEOPLE);
		people.setText(postList.getCount() + "명");

		TextView time = (TextView) convertView.findViewById(R.id.LIST_TV_TIME);
		time.setText(postList.getTime());
		
		TextView place = (TextView) convertView.findViewById(R.id.LIST_TV_LOCATION);
		place.setText(postList.getPlacename());
		final int id = postList.getId();
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, DetailActivity.class);
				intent.putExtra("title", postList.getTitle());
				intent.putExtra("author", postList.getAuthor());
				intent.putExtra("article", postList.article);
				intent.putExtra("count", postList.getCount());
				intent.putExtra("placename", postList.getPlacename());
				intent.putExtra("time", postList.getTime());
				intent.putExtra("date", postList.getDate());
				
				context.startActivity(intent);
			}
		});
		return convertView;
	}

}
