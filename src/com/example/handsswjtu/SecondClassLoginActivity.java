package com.example.handsswjtu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SecondClassLoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second_class_login);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.second_class_login, menu);
		return true;
	}

}
