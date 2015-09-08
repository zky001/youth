package com.handsSwjtu.common;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class DifferentColorListViewAdapter extends SimpleAdapter{

	
	public DifferentColorListViewAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
	}
	
	public View getView(int position,View convertView,ViewGroup parent) {
		View view=super.getView(position, convertView, parent);
		if(position%2==0){
		view.setBackgroundColor(Color.argb(0, 0, 0, 0));
		return view;
	}else{
		view.setBackgroundColor(Color.argb(112, 112, 112, 110));
		return view;
	}
	}
}
