package com.handsSwjtu.MyView;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyImageButton extends LinearLayout{

	ImageView mButtonImage;
	TextView mButtonText;
	public MyImageButton(Context context, int ImageResId, int textResId) {
		super(context);
		// TODO Auto-generated constructor stub
		mButtonImage=new ImageView(context);
		mButtonText=new TextView(context);
		mButtonImage.setImageResource(ImageResId);
		mButtonImage.setPadding(0, 0, 0, 0);
		mButtonText.setText(textResId);
		mButtonText.setTextColor(Color.RED);
		mButtonText.setPadding(0, 0, 0, 0);
		setClickable(true);
		setFocusable(true);
		setBackgroundResource(android.R.drawable.btn_default);
		setOrientation(LinearLayout.VERTICAL);
		addView(mButtonImage);
		addView(mButtonImage);
	}
	public void setImageResouce(int resId) {
		mButtonImage.setImageResource(resId);
	}
	public void setText(CharSequence buttonText){
		mButtonText.setText(buttonText);
	}
	 public void setTextColor(int color) { 
		    mButtonText.setTextColor(color); 
	 } 
}
