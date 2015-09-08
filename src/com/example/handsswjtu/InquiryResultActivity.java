package com.example.handsswjtu;

import com.baidu.mobstat.StatService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

public class InquiryResultActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inquiry_result);
		TextView textViewInquiryResult=(TextView)findViewById(R.id.inquiryResult);
		Intent it=getIntent();
		Bundle bundle=it.getBundleExtra("result");
		String a=bundle.getString("inquiryResult");
		textViewInquiryResult.setText(a);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.inquiry_result, menu);

		return true;
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
