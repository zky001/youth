package com.example.handsswjtu;

import com.baidu.mobstat.StatService;
import com.handsSwjtu.MyView.MyMessageBox;
import com.handsSwjtu.common.CommonOnTouchListener;
import com.handsSwjtu.common.ContextUtil;
import com.handsSwjtu.common.SharePreferenceHelper;
import com.handsSwjtu.common.Tools;
import com.handsSwjtu.httpConnect.HttpConnect;

import android.R.color;
import android.R.integer;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {

	ImageView imageButtonScoreInquiry;
	ImageView imageButtonSportsCenter;
	ImageView imageButtonLibrary;
	ScrollView MainScrollView;
	LinearLayout lyPppoe;
	LinearLayout lySportsCenter;
	LinearLayout lyLibrary;
	LinearLayout lyEmptyClassroom;
	LinearLayout lyShedules;
	LinearLayout lyNews;
	LinearLayout lySwjtuMap;
	LinearLayout lyIndividualCenter;
	LinearLayout lyTakeOut;
	private LinearLayout lySwjtuChat;
	private LinearLayout lySecondClass;
	private LinearLayout lyPhoneBook;
	private TextView tvTimtNow;
	// LinearLayout lyPppoe;
	Dialog alertDialog;
	SharePreferenceHelper sharePreferenceHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		tvTimtNow=(TextView)findViewById(R.id.timeNow);
		String weekNo = String.valueOf(Tools.weekNoNow(0));
		String weekDay = String.valueOf(Tools.WeekDayNow(0));
		tvTimtNow.setText("第"+weekNo+"周   星期"+Tools.convertNumToCn(Integer.parseInt(weekDay)));
		MainScrollView = (ScrollView) findViewById(R.id.scrollViewMain);
		lyPppoe = (LinearLayout) findViewById(R.id.pppoeActivity);
		lySportsCenter = (LinearLayout) findViewById(R.id.sportsCenter);
		lyPhoneBook = (LinearLayout) findViewById(R.id.phoneBook);
		lyLibrary = (LinearLayout) findViewById(R.id.library);
		lyEmptyClassroom = (LinearLayout) findViewById(R.id.emptyClassroom);
		lyShedules = (LinearLayout) findViewById(R.id.schedules);
		lyNews = (LinearLayout) findViewById(R.id.news);
		lySwjtuMap = (LinearLayout) findViewById(R.id.swjtuMap);
		lyIndividualCenter = (LinearLayout) findViewById(R.id.individualCenter);
		lyTakeOut=(LinearLayout)findViewById(R.id.takeout);
		lyShedules.setOnClickListener(mainOnClickListener);
		lyShedules.setOnTouchListener(mainOnTouchListener);
		lyEmptyClassroom.setOnClickListener(mainOnClickListener);
		lyPppoe.setOnClickListener(mainOnClickListener);
		lyNews.setOnClickListener(mainOnClickListener);
		lyNews.setOnTouchListener(mainOnTouchListener);
		lySportsCenter.setOnClickListener(mainOnClickListener);
		lyLibrary.setOnClickListener(mainOnClickListener);
		lySwjtuMap.setOnClickListener(mainOnClickListener);
		lySwjtuMap.setOnTouchListener(mainOnTouchListener);
		lyIndividualCenter.setOnClickListener(mainOnClickListener);
		lyIndividualCenter.setOnTouchListener(mainOnTouchListener);
		lyPppoe.setOnTouchListener(mainOnTouchListener);
		lySwjtuChat = (LinearLayout) findViewById(R.id.swjtuChat);
		lySwjtuChat.setOnTouchListener(mainOnTouchListener);
		lySwjtuChat.setOnClickListener(mainOnClickListener);
		lySecondClass = (LinearLayout) findViewById(R.id.secondClass);
		lySecondClass.setOnTouchListener(mainOnTouchListener);
		lySecondClass.setOnClickListener(mainOnClickListener);
		lyPhoneBook.setOnTouchListener(mainOnTouchListener);
		lyPhoneBook.setOnClickListener(mainOnClickListener);
		lyTakeOut.setOnTouchListener(mainOnTouchListener);
		lyTakeOut.setOnClickListener(mainOnClickListener);
		sharePreferenceHelper = new SharePreferenceHelper();
		lySportsCenter.setOnTouchListener(mainOnTouchListener);
		lyLibrary.setOnTouchListener(mainOnTouchListener);

		lyEmptyClassroom.setOnTouchListener(mainOnTouchListener);
	}

	View.OnTouchListener mainOnTouchListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.setBackgroundColor(Color.parseColor("#03b4fe"));
				return false;
				// MainScrollView.requestDisallowInterceptTouchEvent(true);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				v.setBackgroundResource(R.drawable.balloon_white);
				// MainScrollView.requestDisallowInterceptTouchEvent(true);
				;
			} else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
				v.setBackgroundResource(R.drawable.balloon_white);
				// MainScrollView.requestDisallowInterceptTouchEvent(true);
				;
			}
			return false;
		}
	};

	View.OnClickListener mainOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {

			case R.id.news: {
				
				Intent it = new Intent(MainActivity.this, NewsListActivity.class);
				startActivity(it);
				lyPppoe.setBackgroundResource(R.drawable.balloon_white);
			}
				break;
			case R.id.sportsCenter: {
				Intent it = new Intent(MainActivity.this, SportsCenterLoginActivity.class);
				lySportsCenter.setBackgroundResource(R.drawable.balloon_white);
				startActivity(it);
			}
				break;
			case R.id.library: {
				Intent it = new Intent(MainActivity.this, SearchBooksActivity.class);
				startActivity(it);
				lyLibrary.setBackgroundResource(R.drawable.balloon_white);
			}
				break;
			case R.id.emptyClassroom: {
				Intent it = new Intent(MainActivity.this, SearchEmptyClassrooms.class);
				startActivity(it);
				lyEmptyClassroom.setBackgroundResource(R.drawable.balloon_white);
			}
				break;
			case R.id.pppoeActivity: {
				Intent it = new Intent(MainActivity.this, PPPoeActivity.class);
				startActivity(it);
			}
				break;
			case R.id.swjtuChat: {

				if (sharePreferenceHelper.isHaveLogged()) {
					Intent it = new Intent(MainActivity.this, SwjtuChatListActivity.class);
					startActivity(it);
				} else {
					if (sharePreferenceHelper.getIsAutoLogin()) {
						Intent it = new Intent(MainActivity.this, IndividualCenterLoginActivity.class);
						startActivity(it);
					}else {
					MyMessageBox myMessageBox = new MyMessageBox(MainActivity.this, "该操作需要登录哦！",
							IndividualCenterLoginActivity.class);
					myMessageBox.show();
					}
				}
			}
				break;
			case R.id.individualCenter: {

				if (sharePreferenceHelper.isHaveLogged()) {
					Intent it = new Intent(MainActivity.this, IndividualCenterMainActivity.class);
					startActivity(it);
				} else {
					if (sharePreferenceHelper.getIsAutoLogin()) {
						Intent it = new Intent(MainActivity.this, IndividualCenterLoginActivity.class);
						startActivity(it);
					}else {
					MyMessageBox myMessageBox = new MyMessageBox(MainActivity.this, "该操作需要登录哦！",
							IndividualCenterLoginActivity.class);
					myMessageBox.show();
					}
				}
			}
				break;
			case R.id.swjtuMap:
				Intent ita = new Intent(MainActivity.this, SwjtuKnowListActivity.class);
				startActivity(ita);
				break;
			case R.id.secondClass:
				if (sharePreferenceHelper.isHaveLogged()) {
					Intent it = new Intent(MainActivity.this, SecondClassMainActivity.class);
					startActivity(it);
				} else {
					if (sharePreferenceHelper.getIsAutoLogin()) {
						Intent it = new Intent(MainActivity.this, IndividualCenterLoginActivity.class);
						startActivity(it);
					}else {
						
					
					MyMessageBox myMessageBox = new MyMessageBox(MainActivity.this, "该操作需要登录哦！",
							IndividualCenterLoginActivity.class);
					myMessageBox.show();
					}
				}
				break;
			case R.id.phoneBook: {
				Intent it = new Intent(MainActivity.this, SwjtuPhoneBookActivity.class);
				startActivity(it);
			}
				break;
			case R.id.takeout:{
				Intent it=new Intent(MainActivity.this,TakeOutActivity.class);
				startActivity(it);
			}
				break;
			case R.id.schedules: {

				SharedPreferences mysSharedPreferences = getSharedPreferences("handsSwjtu", Activity.MODE_PRIVATE);
				boolean isScheduleLoaded = mysSharedPreferences.getBoolean("isScheduleLoaded", false);
				if (isScheduleLoaded) {
					Intent it = new Intent(MainActivity.this, Schedule.class);
					Bundle bundle = new Bundle();
					bundle.putInt("gotoPage", Tools.WeekDayNow(0) - 1);
					bundle.putBoolean("isNeedLoad", false);
					it.putExtras(bundle);
					startActivity(it);
				} else {
					LinearLayout confirmLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.my_message_box,
							null);
					alertDialog = new Dialog(MainActivity.this, R.style.dialog);
					alertDialog.setContentView(confirmLayout);

					TextView messageBoxContent = ((TextView) confirmLayout.findViewById(R.id.messageBoxContent));
					messageBoxContent.setText("课程表数据还没有导入，现在登录教务导入？");
					final RelativeLayout positiveButton = (RelativeLayout) confirmLayout
							.findViewById(R.id.positiveButton);
					RelativeLayout negativeButton = (RelativeLayout) confirmLayout.findViewById(R.id.negativeButton);
					positiveButton.setOnTouchListener(new CommonOnTouchListener());
					negativeButton.setOnTouchListener(new CommonOnTouchListener());
					positiveButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent it = new Intent(MainActivity.this, DeanOfficeLoginActivity.class);
							startActivity(it);
							positiveButton.setBackgroundColor(0);
						}
					});

					negativeButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							alertDialog.dismiss();
						}
					});

					alertDialog.show();
				}

				lyShedules.setBackgroundResource(R.drawable.balloon_white);
			}
				break;

			}
		}
	};



	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		return super.onKeyDown(keyCode, event);

	}

	public void startIndividualCenter(View v) {
		Intent it = new Intent(MainActivity.this, IndividualCenterLoginActivity.class);
		startActivity(it);
	}
	
	
	public void share(View v) {
		Intent it = new Intent(Intent.ACTION_SEND);
		it.setType("text/plain");
		it.putExtra(Intent.EXTRA_SUBJECT, "分享交大YOUTH");
		it.putExtra(Intent.EXTRA_TEXT, "我下载了交大YOUTH客户端，感觉很好用哦，下载地址：http://youth.swjtu.edu.cn/develops/handsSwjtu/youth.apk");
		startActivity(Intent.createChooser(it, "分享交大YOUTH"));
	}
	
	public void startActivityInfo(View v) {
		Intent it = new Intent(MainActivity.this, InfoActivity.class);
		startActivity(it);
	}
	

	protected void onRestart() {

		if (alertDialog != null) {
			alertDialog.dismiss();
		}
		super.onRestart();

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
