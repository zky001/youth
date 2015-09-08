package com.example.handsswjtu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.handsSwjtu.Objects.SportTimeResult;
import com.handsSwjtu.common.CommonOnTouchListener;
import com.handsSwjtu.common.DataProvider;
import com.handsSwjtu.common.Tools;
import com.handsSwjtu.httpConnect.HttpConnect;

public class SportTimeResultActivity extends Activity {

	private ProgressBar loadingProgressBar;
	private Button buttonGetSportsTimeSubmit;
	private EditText editTextSemester;
	private EditText editTextWeek;
	private String username;
	private String password;
	private String validateCode;
	private String semester;
	private String week;
	private TextView TextViewSportsTimeResult;
	private Spinner weekNoSpinner;
	private String[] weekNoStrings = { "第1周", "第2周", "第3周", "第4周", "第5周", "第6周", "第7周", "第8周", "第9周", "第10周", "第11周",
			"第12周", "第13周", "第14周", "第15周", "第16周", "第17周", "第18周", "第19周", };
	private int weekNo;
	private List<SportTimeResult> sportsTimeResults;
	private SimpleAdapter simpleAdapter;
	private ListView sportsTimeResultListView;
	private boolean isSpinnerFirstLoad=true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sport_time_result);
		sportsTimeResults = new ArrayList<SportTimeResult>();
		loadingProgressBar = (ProgressBar) findViewById(R.id.loading);
		sportsTimeResultListView = (ListView) findViewById(R.id.sportsTimeContentList);
		weekNoSpinner = (Spinner) findViewById(R.id.weekNo);
		ArrayAdapter<String> weekNoAdapter = new ArrayAdapter<String>(this, R.layout.my_spinner, weekNoStrings);
		weekNoSpinner.setAdapter(weekNoAdapter);
		weekNoSpinner.setOnItemSelectedListener(onItemSelectedListener);
		weekNo = Tools.weekNoNow(0);
		loadData();
		sportsTimeResults = new ArrayList<SportTimeResult>();
		loadingProgressBar.setVisibility(View.VISIBLE);
		// TextViewSportsTimeResult=(TextView)findViewById(R.id.sportsTimeResult);
		// buttonGetSportsTimeSubmit=(Button)findViewById(R.id.buttonGetSportsTimeSubmit);
		// editTextSemester=(EditText)findViewById(R.id.editTextSemester);
		// editTextWeek=(EditText)findViewById(R.id.editTextWeek);

		// ProgressBarLoading.setVisibility(View.INVISIBLE);
		// TextView
	}

	public void setMobileDataEnabled(boolean enabled) {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

	}

	OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			if (!isSpinnerFirstLoad) {
				weekNo = arg2 + 1;
				loadData();
				sportsTimeResults = new ArrayList<SportTimeResult>();
				loadingProgressBar.setVisibility(View.VISIBLE);
			}else {
				isSpinnerFirstLoad=false;
			}


		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};

	public void loadData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				if (DataProvider.sportTimeResultProvider(sportsTimeResults, weekNo)) {
					if (sportsTimeResults.size()==0) {
						handler.sendEmptyMessage(1);
					} else {
						handler.sendEmptyMessage(2);
					}
				} else {
					handler.sendEmptyMessage(3);
				}
			}
		}).start();
	}

	public Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				Toast.makeText(SportTimeResultActivity.this, "这周没有刷卡记录哦", Toast.LENGTH_SHORT).show();
				 sportsTimeResultListView.setAdapter(null);
				// simpleAdapter=new
				// SimpleAdapter(SportTimeResultActivity.this,, resource, from,
				// to);
				break;
			case 2:
				Toast.makeText(SportTimeResultActivity.this, "数据加载成功", Toast.LENGTH_SHORT).show();
				List<Map<String, Object>> sportsResultMaps = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < sportsTimeResults.size(); i++) {
					Map<String, Object> sportResltMap = new HashMap<String, Object>();
					sportResltMap.put("sportPlace", sportsTimeResults.get(i).getSportsPlace());
					sportResltMap.put("enterTime", sportsTimeResults.get(i).getEnterTime());
					sportResltMap.put("exitTime", sportsTimeResults.get(i).getExitTime());
					sportResltMap.put("totalTime", sportsTimeResults.get(i).getTotalTime());
					sportResltMap.put("validTime", sportsTimeResults.get(i).getValidTime());
					sportsResultMaps.add(sportResltMap);
				}
				simpleAdapter = new SimpleAdapter(SportTimeResultActivity.this, sportsResultMaps,
						R.layout.listview_sport_time, new String[] { "sportPlace", "enterTime", "exitTime",
								"totalTime", "validTime" }, new int[] { R.id.sportsPlace, R.id.enterTime,
								R.id.exitTime, R.id.totalTime, R.id.validTime });
				sportsTimeResultListView.setAdapter(simpleAdapter);
			     
			     weekNoSpinner.setSelection(weekNo-1,true);
				break;
			case 3:
				// LinearLayout confirmLayout = (LinearLayout)
				// getLayoutInflater().inflate(R.layout.my_message_box, null);
				// final Dialog alertDialog = new
				// Dialog(SportTimeResultActivity.this, R.style.dialog);
				// alertDialog.setContentView(confirmLayout);
				// alertDialog.show();
				// TextView messageBoxContent = ((TextView)
				// confirmLayout.findViewById(R.id.messageBoxContent));
				// messageBoxContent.setText("数据获取失败？");
				// final RelativeLayout positiveButton = (RelativeLayout)
				// confirmLayout.findViewById(R.id.positiveButton);
				// RelativeLayout negativeButton = (RelativeLayout)
				// confirmLayout.findViewById(R.id.negativeButton);
				// positiveButton.setOnTouchListener(new
				// CommonOnTouchListener());
				// negativeButton.setOnTouchListener(new
				// CommonOnTouchListener());
				// positiveButton.setOnClickListener(new OnClickListener() {
				//
				// @Override
				// public void onClick(View v) {
				// // TODO Auto-generated method stub
				// loadData();
				// alertDialog.dismiss();
				// }
				// });
				// negativeButton.setOnClickListener(new OnClickListener() {
				//
				// @Override
				// public void onClick(View v) {
				// // TODO Auto-generated method stub
				//
				// alertDialog.dismiss();
				// }
				// });
				Toast.makeText(SportTimeResultActivity.this, "数据加载失败", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}

			loadingProgressBar.setVisibility(View.INVISIBLE);
		}

	};

	public void onResume() {
		
		super.onResume();
		StatService.onResume(this);
	}

	public void onPause() {
		
		super.onPause();
		StatService.onPause(this);
	}

	
	public void finishActivity(View view){
		finish();
	}

	class SportsTimeDetail {

	}

}
