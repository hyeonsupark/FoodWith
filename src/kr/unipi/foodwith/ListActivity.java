package kr.unipi.foodwith;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		Intent mIntent = new Intent(this.getIntent());
		String cat = mIntent.getStringExtra("category");
		Log.d("FOOD", "Category : " + cat);
		postList = new ArrayList<PostLists>();
		listAdapter = new ListAdapter(this, R.layout.list, postList);
		listView = (ListView) findViewById(R.id.listView1);
		pref = getSharedPreferences("pref", MODE_PRIVATE);
		
		category = pref.getString("category", "카테고리");
		title = pref.getString("title", "제목");
		author = pref.getString("userName", "개설자");
		people = pref.getString("people", "인원");
		time = pref.getString("time", "시간");
		
		PostLists p1 = new PostLists(R.drawable.ic_launcher, category, title, author, people, time);
		postList.add(p1);
		
		listView.setAdapter(listAdapter);
	}

}
// 글을 쓰면 preference에올라가며넛 

class PostLists {
	int id;
	int photo;
	String category;
	String title;
	String author;
	String people;
	String time;
	
	public PostLists(int photo, String category, String title, String author, String people, String time) {
		this.category = category;
		this.photo = photo;
		this.title = title;
		this.author = author;
		this.people = people;
		this.time = time;
	}
	
	public int getPhoto() {
		return photo;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public String getPeople() {
		return people;
	}

	public String getTime() {
		return time;
	}
	
	
}

class ListAdapter extends BaseAdapter {
	LayoutInflater inflater;
	ArrayList<PostLists> postLists;
	int layout;
	
	public ListAdapter(Context context, int layout, ArrayList<PostLists> postLists) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		if(convertView == null) {
			convertView = inflater.inflate(layout, parent, false);
		}
		
		PostLists postList = postLists.get(position);
		
		ImageView photo = (ImageView) convertView.findViewById(R.id.LIST_IMG);
		photo.setImageResource(postList.getPhoto());
		
		TextView title = (TextView) convertView.findViewById(R.id.LIST_TV_TITLE);
		title.setText(postList.getTitle());
		
		TextView author = (TextView) convertView.findViewById(R.id.LIST_TV_AUTHOR);
		author.setText(postList.getAuthor());
		
		TextView people = (TextView) convertView.findViewById(R.id.LIST_TV_PEOPLE);
		people.setText(postList.getPeople());
		
		TextView time = (TextView) convertView.findViewById(R.id.LIST_TV_TIME);
		time.setText(postList.getTime());
		
		
		
		return convertView;
	}
	
}
