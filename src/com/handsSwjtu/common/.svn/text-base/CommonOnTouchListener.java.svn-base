package com.handsSwjtu.common;

import com.example.handsswjtu.R;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class CommonOnTouchListener implements View.OnTouchListener{


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			v.setBackgroundColor(Color.parseColor("#03b4fe"));
		}
		if(event.getAction() == MotionEvent.ACTION_UP){
			v.setBackgroundColor(0);
		}
		return false;
	}

}
