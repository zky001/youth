package com.example.handsswjtu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.mobstat.StatService;
import com.handsSwjtu.common.CommonOnTouchListener;
import com.handsSwjtu.common.DifferentColorListViewAdapter;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TakeOutDetailActivity extends Activity {

	private List<Map<String, Object>> takeOutDetails;
	private String localFilePath="/handsSwjtu";
	private String fileName="TakeOutDataBaseV2.db";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_take_out_detail);
		((TextView)findViewById(R.id.name)).setText(getIntent().getStringExtra("name"));
		((TextView)findViewById(R.id.phoneNum)).setText("电话： "+getIntent().getStringExtra("phoneNum"));
		((TextView)findViewById(R.id.location)).setText("地址： "+getIntent().getStringExtra("location"));
		initvalue();
	}

	public void initvalue() {
		takeOutDetails=new ArrayList<Map<String,Object>>();
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory()+localFilePath+"/"+fileName, null);
		String sql="select ID,name,price from Take_Out_Detail where categoryID="+getIntent().getStringExtra("categoryID");
		Cursor cursor=database.rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			Map<String, Object> temp=new HashMap<String, Object>();
			temp.put("ID", "序号");
			temp.put("name","名称");
			temp.put("price", "价格（元）");

			takeOutDetails.add(temp);
			
			do {
				temp=new HashMap<String, Object>();
				temp.put("ID", cursor.getString(0));
				temp.put("name",cursor.getString(1));
				temp.put("price", cursor.getString(2));

				takeOutDetails.add(temp);
			} while (cursor.moveToNext());
		}
		
		DifferentColorListViewAdapter adapter=new DifferentColorListViewAdapter(TakeOutDetailActivity.this, takeOutDetails, R.layout.listview_take_out_detail, new String[]{"ID","name","price"}, new int[]{R.id.ID,R.id.name,R.id.price});
	
		((ListView)findViewById(R.id.takeOutDetailList)).setAdapter(adapter);
		((ImageView)findViewById(R.id.iconPhone)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String name=getIntent().getStringExtra("name");
				final String phoneNum=getIntent().getStringExtra("phoneNum");
				LinearLayout confirmLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.my_message_box, null);
				final Dialog alertDialog = new Dialog(TakeOutDetailActivity.this, R.style.dialog);
				alertDialog.setContentView(confirmLayout);
				alertDialog.show();
				TextView messageBoxContent = ((TextView) confirmLayout.findViewById(R.id.messageBoxContent));
				messageBoxContent.setText("拨打电话给"+name+"("+phoneNum+")?");
				final RelativeLayout positiveButton = (RelativeLayout) confirmLayout.findViewById(R.id.positiveButton);
				RelativeLayout negativeButton = (RelativeLayout) confirmLayout.findViewById(R.id.negativeButton);
				positiveButton.setOnTouchListener(new CommonOnTouchListener());
				negativeButton.setOnTouchListener(new CommonOnTouchListener());
				positiveButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent phoneIntent = new Intent("android.intent.action.CALL",Uri.parse("tel:" + phoneNum));
						startActivity(phoneIntent);
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
		});
	}
	
	public void finishActivity(View view){
		finish();
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
