package com.example.handsswjtu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.acl.Group;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mobstat.StatService;
import com.example.handsswjtu.R.layout;
import com.handsSwjtu.common.Tools;
import com.handsSwjtu.httpConnect.HttpConnect;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.Contacts.Data;
import android.provider.Settings.System;
import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SearchEmptyClassrooms extends Activity {

	private Spinner selectBuildingSpinner;
	private ExpandableListView emptyClassTimeView;
	String[] timeString = { "第一讲，8:00-9:35", "第二讲，9:50-12:15", "第三讲，14:00-15:35", "第四讲，15:50-18:15", "第五讲,19:30-21:05" };
	String[] buildings = { "一教", "二教", "三教", "四教", "五教", "六教", "七教", "八教" };
	String[][] emptyClassroom;

	private List<Map<String, Object>> groupsList;
	private List<List<Map<String, Object>>> childsList;
	private String resultString;
	private RelativeLayout todaylLayout;
	private RelativeLayout tomorrowlLayout;
	private RelativeLayout dayAfterTomorrowLayout;
	private ProgressBar loadingpProgressBar;
	private int dayOffset = 0;
	private String buildingString = "1";
	private SimpleExpandableListAdapter simpleExpandableListAdapter;
	private String filePath = "/data" + Environment.getDataDirectory().getAbsolutePath()
			+ "/com.example.handsswjtu/raw_handsswjtu.db";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_empty_classrooms);
		selectBuildingSpinner = (Spinner) findViewById(R.id.selectBuilding);
		emptyClassTimeView = (ExpandableListView) findViewById(R.id.emptyClassTime);
		todaylLayout = (RelativeLayout) findViewById(R.id.today);
		tomorrowlLayout = (RelativeLayout) findViewById(R.id.tomorrow);
		dayAfterTomorrowLayout = (RelativeLayout) findViewById(R.id.dayAfterTomorrow);
		loadingpProgressBar = (ProgressBar) findViewById(R.id.loading);

		//
		// LayoutParams
		// linLayoutParams=(LayoutParams)emptyClassTimeView.getLayoutParams();
		// WindowManager
		// wm=(WindowManager)(this.getSystemService(Context.WINDOW_SERVICE));
		// int pxHeight=wm.getDefaultDisplay().getHeight();
		// int scale=
		// (SearchEmptyClassrooms.this.getResources().getDisplayMetrics().densityDpi);
		// Rect frame=new Rect();
		// getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		// RelativeLayout
		// totalLayout=(RelativeLayout)findViewById(R.id.totalLayout);
		// int c=totalLayout.getHeight();
		// linLayoutParams.height=(int)(c-100*(scale/160)-frame.top);

		// emptyClassTimeView.setLayoutParams(linLayoutParams);
		loadingpProgressBar.setVisibility(View.VISIBLE);
		ArrayAdapter<String> selectBuildingAdapter = new ArrayAdapter<String>(this, R.layout.my_spinner, buildings);
		selectBuildingSpinner.setAdapter(selectBuildingAdapter);
		selectBuildingSpinner.setOnItemSelectedListener(onItemSelectedListener);
		todaylLayout.setBackgroundResource(R.drawable.buttonfocused);
		todaylLayout.setOnClickListener(onClickListener);
		tomorrowlLayout.setOnClickListener(onClickListener);
		dayAfterTomorrowLayout.setOnClickListener(onClickListener);
		HttpConnect.isWap(this);

	}

	public void loadData(int dayOffset, final String building) {
		loadingpProgressBar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					if (!new File(filePath).exists()) {
						InputStream is = getResources().openRawResource(R.raw.raw_handsswjtu);
						FileOutputStream fos = new FileOutputStream(filePath);
						byte[] buffer = new byte[8192];
						int count = 0;
						while ((count = is.read(buffer)) > 0) {
							fos.write(buffer, 0, count);
						}
						fos.close();
						is.close();
					}
					setMyAdapter();

					handler.sendEmptyMessage(1);
				} catch (Exception e) {
					// TODO: handle exception
					Log.i("sgs", e.toString());
				}

			}
		}).start();
	}

	public void setMyAdapter() {
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(filePath, null);
		String weekNo = String.valueOf(Tools.weekNoNow(dayOffset));
		String dayTime = String.valueOf(Tools.classTimeNow());
		String weekDay = String.valueOf(Tools.WeekDayNow(dayOffset));
		childsList = new ArrayList<List<Map<String, Object>>>();
		groupsList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < 5; i++) {
			String sqlString = "select _name,_type,_capacity from empty_classes where _week_no='" + weekNo
					+ "' and _day_time ='" + String.valueOf(i + 1) + "' and _week_day='" + weekDay
					+ "' and _building ='" + buildingString + "'";
			;
			Cursor cursor = database.rawQuery(sqlString, null);
			cursor.moveToFirst();
			List<Map<String, Object>> childlist_list = new ArrayList<Map<String, Object>>();
			if (cursor.moveToFirst()) {
				do {
					String detail = cursor.getString(0) + "(" + cursor.getString(1) + "," + cursor.getString(2) + ")";
					Map<String, Object> temp = new HashMap<String, Object>();
					temp.put("emptyTime", detail);
					childlist_list.add(temp);

				} while (cursor.moveToNext());
			}

			childsList.add(childlist_list);

		}

		for (int i = 0; i < timeString.length; i++) {
			Map<String, Object> temp = new HashMap<String, Object>();
			temp.put("classTime", timeString[i]);
			groupsList.add(temp);
		}
		simpleExpandableListAdapter = new SimpleExpandableListAdapter(SearchEmptyClassrooms.this, groupsList,
				R.layout.expandable_listview_group, new String[] { "classTime" }, new int[] { R.id.TeachTime },
				childsList, R.layout.expandable_listview_child, new String[] { "emptyTime" },
				new int[] { R.id.emptyClassroomChild });

	}

	View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.today: {
				if (!(dayOffset == 0)) {
					dayOffset = 0;
					todaylLayout.setBackgroundResource(R.drawable.buttonfocused);
					tomorrowlLayout.setBackgroundResource(R.drawable.buttonunfocused);
					dayAfterTomorrowLayout.setBackgroundResource(R.drawable.buttonunfocused);
					loadData(dayOffset, buildingString);
				}

			}
				break;
			case R.id.tomorrow: {
				if (!(dayOffset == 1)) {
					dayOffset = 1;
					todaylLayout.setBackgroundResource(R.drawable.buttonunfocused);
					tomorrowlLayout.setBackgroundResource(R.drawable.buttonfocused);
					dayAfterTomorrowLayout.setBackgroundResource(R.drawable.buttonunfocused);
					loadData(dayOffset, buildingString);
				}

			}
				break;
			case R.id.dayAfterTomorrow: {
				if (!(dayOffset == 2)) {
					dayOffset = 2;
					todaylLayout.setBackgroundResource(R.drawable.buttonunfocused);
					tomorrowlLayout.setBackgroundResource(R.drawable.buttonunfocused);
					dayAfterTomorrowLayout.setBackgroundResource(R.drawable.buttonfocused);
					loadData(dayOffset, buildingString);
				}

			}
				break;
			default:
				break;
			}
		}
	};

	OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			buildingString = String.valueOf(arg2 + 1);
			loadData(dayOffset, buildingString);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}

	};

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1: {
				// try {

				emptyClassTimeView.setAdapter(simpleExpandableListAdapter);
				if (Tools.classTimeNow() == -1) {
					for (int i = 0; i < 5; i++) {
						emptyClassTimeView.expandGroup(i);
					}
				} else
					emptyClassTimeView.expandGroup(Tools.classTimeNow());
				loadingpProgressBar.setVisibility(View.INVISIBLE);

				/*
				 * List<JSONArray> jsonArrays = new ArrayList<JSONArray>(); //
				 * List<List<Map<String, Object>>> jsonList = new //
				 * ArrayList<List<Map<String, Object>>>(); jsonArrays.add(new
				 * JSONObject(resultString) .getJSONArray("1"));
				 * jsonArrays.add(new JSONObject(resultString)
				 * .getJSONArray("2")); jsonArrays.add(new
				 * JSONObject(resultString) .getJSONArray("3"));
				 * jsonArrays.add(new JSONObject(resultString)
				 * .getJSONArray("4")); jsonArrays.add(new
				 * JSONObject(resultString) .getJSONArray("5")); childsList =
				 * new ArrayList<List<Map<String, Object>>>(); groupsList = new
				 * ArrayList<Map<String, Object>>(); for (int i = 0; i <
				 * jsonArrays.size(); i++) { List<Map<String, Object>>
				 * jsonListChild = new ArrayList<Map<String, Object>>(); for
				 * (int j = 0; j < jsonArrays.get(i).length(); j++) {
				 * Map<String, Object> temp = new HashMap<String, Object>();
				 * String detail = ((JSONObject) jsonArrays.get(i)
				 * .opt(j)).getString("detail"); temp.put("emptyTime", detail);
				 * jsonListChild.add(temp); } childsList.add(jsonListChild); }
				 * 
				 * for (int i = 0; i < timeString.length; i++) { Map<String,
				 * Object> temp = new HashMap<String, Object>();
				 * temp.put("classTime", timeString[i]); groupsList.add(temp); }
				 * 
				 * SimpleExpandableListAdapter simpleExpandableListAdapter = new
				 * SimpleExpandableListAdapter( SearchEmptyClassrooms.this,
				 * groupsList, R.layout.expandable_listview_group, new String[]
				 * { "classTime" }, new int[] { R.id.classTime }, childsList,
				 * R.layout.expandable_listview_child, new String[] {
				 * "emptyTime" }, new int[] { R.id.emptyClassroomChild });
				 * 
				 * emptyClassTimeView.setAdapter(simpleExpandableListAdapter);
				 * if (Tools.classTimeNow() == -1) { for (int i = 0; i < 5; i++)
				 * { emptyClassTimeView.expandGroup(i); } } else
				 * emptyClassTimeView.expandGroup(Tools.classTimeNow());
				 * loadingpProgressBar.setVisibility(View.INVISIBLE); } catch
				 * (JSONException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); }
				 */
			}
				break;
			case 2: {
				Toast.makeText(SearchEmptyClassrooms.this, "数据为空", 2000).show();
				loadingpProgressBar.setVisibility(View.INVISIBLE);
			}
				break;
			default:
				break;
			}

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
