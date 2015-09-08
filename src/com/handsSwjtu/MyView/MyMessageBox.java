package com.handsSwjtu.MyView;

import com.example.handsswjtu.DeanOfficeLoginActivity;
import com.example.handsswjtu.MainActivity;
import com.example.handsswjtu.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyMessageBox{

	final Dialog alertDialog;
	
	
	public MyMessageBox(final Activity activity,String msg,final Class<?> cls) {
		// TODO Auto-generated constructor stub
		
		LinearLayout confirmLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.my_message_box,
				null);
		alertDialog = new Dialog(activity, R.style.dialog);
		alertDialog.setContentView(confirmLayout);

		
		TextView messageBoxContent = ((TextView) confirmLayout.findViewById(R.id.messageBoxContent));
		messageBoxContent.setText(msg);
		final RelativeLayout positiveButton = (RelativeLayout) confirmLayout
				.findViewById(R.id.positiveButton);
		RelativeLayout negativeButton = (RelativeLayout) confirmLayout.findViewById(R.id.negativeButton);
		positiveButton.setOnTouchListener(mainOnTouchListener);
		negativeButton.setOnTouchListener(mainOnTouchListener);
		positiveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(activity,cls);
				activity.startActivity(it);
				positiveButton.setBackgroundColor(0);
				alertDialog.dismiss();
			}
		});

		negativeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				alertDialog.dismiss();
			}
		});
	}
	View.OnTouchListener mainOnTouchListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.setBackgroundColor(Color.parseColor("#03b4fe"));
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				v.setBackgroundColor(0);
				;
			}
			return false;
		}
	};
	
	public void show(){
		alertDialog.show();
	}

}
