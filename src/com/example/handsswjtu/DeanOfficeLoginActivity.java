package com.example.handsswjtu;

import com.baidu.mobstat.StatService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DeanOfficeLoginActivity extends Activity {

	TextView studentNumtTextView;
	TextView passwordTextView;
	RelativeLayout loginButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dean_office_login);
		studentNumtTextView=(TextView)findViewById(R.id.stuCode);
		passwordTextView=(TextView)findViewById(R.id.password);
		loginButton=(RelativeLayout)findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			Intent it=new Intent(DeanOfficeLoginActivity.this,Schedule.class);
			Bundle bundle=new Bundle();
			bundle.putString("username", studentNumtTextView.getText().toString());
			bundle.putString("password", passwordTextView.getText().toString());
			bundle.putBoolean("isNeedLoad", true);
			it.putExtras(bundle);
			startActivity(it);
			}
		});
		

		
	}
	protected void onRestart(){
		finish();super.onRestart();
	}
	
	public void onResume() {
		
		super.onResume();
		StatService.onResume(this);
	}

	public void onPause() {
		
		super.onPause();
		StatService.onPause(this);
	}


}
