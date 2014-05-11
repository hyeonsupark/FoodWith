package kr.unipi.foodwith;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skp.Tmap.TMapView;

public class DetailActivity extends Activity {

	TMapView mapView;
	
	Button btn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setBackgroundDrawable(new ColorDrawable(0xffeb5655));
		setContentView(R.layout.activity_detail);
		Intent it = getIntent();
		btn = (Button) findViewById(R.id.btn_join);
		
		tvset(R.id.detail_date, "%s 게시물", it.getStringExtra("date"));
		tvset(R.id.detail_title, it.getStringExtra("title"));
		tvset(R.id.detail_author, "개설자: %s", it.getStringExtra("author"));
		tvset(R.id.detail_article, it.getStringExtra("article"));
		tvset(R.id.detail_count, "%d명", it.getIntExtra("count", 0));
		tvset(R.id.detail_time, it.getStringExtra("time"));
		tvset(R.id.detail_placename, it.getStringExtra("placename"));
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(DetailActivity.this, "참가되었습니다.", Toast.LENGTH_SHORT).show();
			}
		});
		mapView = new TMapView(this);
		final double lon = it.getDoubleExtra("lon", -1);
		final double lat = it.getDoubleExtra("lat", -1);
		if(lon > 0 && lat > 0) {
			((LinearLayout)findViewById(R.id.detail_map)).addView(mapView);
			new Thread(new Runnable() {
				@Override
				public void run() {
					mapView.setCenterPoint(lon, lat);
				}
			}).start();
		}
	}

	private void tvset(int id, String text) {
		((TextView)findViewById(id)).setText(text);
	}
	private void tvset(int id, String format, Object ...args) {
		((TextView)findViewById(id)).setText(String.format(format, args));
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}

}
