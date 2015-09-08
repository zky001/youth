package com.example.handsswjtu;

import org.json.JSONException;
import org.json.JSONObject;

import com.handsSwjtu.httpConnect.HttpConnect;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class SwjtuMapActivity extends Activity {

	private String aString;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_swjtu_map);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
//					
//					JSONObject jsonObject=new JSONObject(HttpConnect.getTest());
//					aString=jsonObject.getString("1");
					handler.sendEmptyMessage(1);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", e.toString());
				}
				
			}
		}).start();

		
	}

	Handler handler=new Handler(){
		public void handleMessage(Message msg){
			TextView textView=(TextView)findViewById(R.id.questionContent);
			textView.setText(aString);
		}
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.swjtu_map, menu);
		return true;
	}

}
