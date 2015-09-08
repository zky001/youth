package com.example.handsswjtu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.mobstat.StatService;
import com.handsSwjtu.common.CommonOnTouchListener;

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
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.SimpleExpandableListAdapter;

public class SwjtuPhoneBookActivity extends Activity {

	private String filePath = "/data" + Environment.getDataDirectory().getAbsolutePath()
			+ "/com.example.handsswjtu/raw_handsswjtu.db";
	private ExpandableListView phoneNumElv;
	private SimpleExpandableListAdapter simpleExpandableListAdapter;
	private List<Map<String, String>> groupsList;
	private List<List<Map<String, String>>> childLists;
	private String[] categoryNames = { "党群部门", "行政部门", "业务部门", "直属单位" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_swjtu_phone_book);
		phoneNumElv = (ExpandableListView) findViewById(R.id.phoneNumElv);
		groupsList = new ArrayList<Map<String, String>>();
		childLists = new ArrayList<List<Map<String, String>>>();
		if (!new File(filePath).exists()) {
			InputStream is = getResources().openRawResource(R.raw.raw_handsswjtu);
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(filePath);
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		initValues();
	}

	public void initValues() {
		SQLiteDatabase database=SQLiteDatabase.openOrCreateDatabase(filePath, null);
		for (int i = 0; i < 4; i++) {
			String sql="select Name,PhoneNum from Swjtu_Phone_Book where CategoryID="+i;
			Cursor cursor=database.rawQuery(sql, null);
			List<Map<String, String>> childList=new ArrayList<Map<String,String>>();
			if (cursor.moveToFirst()) {
				
				do {
					Map<String, String> temp=new HashMap<String, String>();
					temp.put("name", cursor.getString(0));
					temp.put("phoneNum", cursor.getString(1));
					childList.add(temp);
				} while (cursor.moveToNext());
			}
			childLists.add(childList);
		}
		
		for (int i = 0; i < categoryNames.length; i++) {
			Map<String, String> temp=new HashMap<String, String>();
			temp.put("categoryName", categoryNames[i]);
			groupsList.add(temp);
		}
		simpleExpandableListAdapter=new SimpleExpandableListAdapter(SwjtuPhoneBookActivity.this, groupsList, R.layout.expandable_listview_group, new String[]{"categoryName"}, new int[]{R.id.TeachTime},  childLists, R.layout.expandable_listview_child_phone	, new String[]{"name","phoneNum"}, new int[]{R.id.name,R.id.phoneNum});
		phoneNumElv.setAdapter(simpleExpandableListAdapter);
		for (int i = 0; i < categoryNames.length; i++) {
			phoneNumElv.expandGroup(i);
		}
		phoneNumElv.setOnChildClickListener(onChildClickListener);
	}

OnChildClickListener onChildClickListener=new OnChildClickListener() {
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		// TODO Auto-generated method stub
		String name=childLists.get(groupPosition).get(childPosition).get("name");
		final String phoneNum=childLists.get(groupPosition).get(childPosition).get("phoneNum");
		LinearLayout confirmLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.my_message_box, null);
		final Dialog alertDialog = new Dialog(SwjtuPhoneBookActivity.this, R.style.dialog);
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
		
		return false;
	}
};
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
