package com.example.handsswjtu;

import java.util.ArrayList;

import com.baidu.mobstat.StatService;
import com.handsSwjtu.common.CommonOnTouchListener;
import com.handsSwjtu.common.DataProvider;
import com.handsSwjtu.common.DatabaseHelper;
import com.handsSwjtu.common.SharePreferenceHelper;
import com.handsSwjtu.common.Tools;
import com.handsSwjtu.httpConnect.HttpConnect;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Schedule extends Activity {

	private ArrayList<View> views;
	private ArrayList<String> titles;
	private ArrayList<TextView> textViews;
	private ArrayList<TextView> teachers;
	private ArrayList<TextView> classrooms;
	private ArrayList<TextView> threeClassFlags;
	private ArrayList<LinearLayout> classLayouts;
	private ViewPager mViewPager;
	private int offset = 0;
	private int currIndex = 0;
	private int bmpw;
	private View view1, view2, view3, view4, view5, view6, view7;
	public static String username;
	public static String password;
	private Boolean isNeedLoad;
	private Bundle bundle;
	private int errorCode;
	private int gotoPage;
	private ProgressBar loadingProgressBar;
	private CheckBox checkBoxIsMute;
	private SharePreferenceHelper sharePreferenceHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedules);
		LayoutInflater mli = LayoutInflater.from(this);
		sharePreferenceHelper=new SharePreferenceHelper();
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		checkBoxIsMute=(CheckBox)findViewById(R.id.checkBoxIsMute);
		checkBoxIsMute.setChecked(sharePreferenceHelper.getIsMuteWhenClass());
		checkBoxIsMute.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				SharePreferenceHelper sharePreferenceHelper=new SharePreferenceHelper();
				if (checkBoxIsMute.isChecked()) {
					
					sharePreferenceHelper.setIsMuteWhenClass(true);
					
					Toast.makeText(Schedule.this, "您选择了上课时自动静音，将会在重新启动您的手机后生效，注意请勿关闭软件的开机自动启动功能，否则将无法自动静音", Toast.LENGTH_LONG).show();
				}else {
					sharePreferenceHelper.setIsMuteWhenClass(false);
					Toast.makeText(Schedule.this, "您取消了上课时自动静音，将会在重新启动您的手机后生效", Toast.LENGTH_LONG).show();

				}
			}
		});
		loadingProgressBar = (ProgressBar) findViewById(R.id.loading);
		bundle = getIntent().getExtras();
		isNeedLoad = bundle.getBoolean("isNeedLoad");

		mViewPager.setOnPageChangeListener(new myOnPageChangeListener());

		view1 = mli.inflate(R.layout.daily_class, null);
		view2 = mli.inflate(R.layout.daily_class, null);
		view3 = mli.inflate(R.layout.daily_class, null);
		view4 = mli.inflate(R.layout.daily_class, null);
		view5 = mli.inflate(R.layout.daily_class, null);
		view6 = mli.inflate(R.layout.daily_class, null);
		view7 = mli.inflate(R.layout.daily_class, null);

		views = new ArrayList<View>();
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);
		views.add(view5);
		views.add(view6);
		views.add(view7);

		titles = new ArrayList<String>();
		titles.add("星期一");
		titles.add("星期二");
		titles.add("星期三");
		titles.add("星期四");
		titles.add("星期五");
		titles.add("星期六");
		titles.add("星期日");
		if (isNeedLoad) {
			loadingProgressBar.setVisibility(View.VISIBLE);
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				DatabaseHelper databaseHelper = new DatabaseHelper(Schedule.this);

				if (isNeedLoad) {
					username = bundle.getString("username");
					password = bundle.getString("password");
					errorCode = databaseHelper.refresh(username, password);
					if (errorCode != 1) {
						handler.sendEmptyMessage(2);
					} else {
						handler.sendEmptyMessage(1);
					}

				} else {
					handler.sendEmptyMessage(1);
				}
				// databaseHelper.dml("insert into schedule(code,name)values(?,?)",
				// new Object[]{"235235","英语"});
				databaseHelper.close();

			}
		}).start();

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				mViewPager.setAdapter(mPagerAdapter);
				try {
					gotoPage = bundle.getInt("gotoPage");
					mViewPager.setCurrentItem(gotoPage);
				} catch (Exception e) {
					// TODO: handle exception
					mViewPager.setCurrentItem(Tools.WeekDayNow(0) - 1);
				}

				SharedPreferences mysSharedPreferences = getSharedPreferences("handsSwjtu", Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = mysSharedPreferences.edit();
				editor.putBoolean("isScheduleLoaded", true);
				editor.commit();
				break;
			case 2: {
				switch (errorCode) {
				case 1: {
					
				}
					break;
				case 2: {
					Toast.makeText(Schedule.this, "未知错误，请检查学号和密码", Toast.LENGTH_LONG).show();
				}
					break;
				case 3: {
					Toast.makeText(Schedule.this, "网络错误，请检查网络连接", Toast.LENGTH_LONG).show();
				}
					break;
				default:
					break;
				}
			}
				break;
			default:
				break;
			}
			loadingProgressBar.setVisibility(View.INVISIBLE);
		}

	};

	PagerAdapter mPagerAdapter = new PagerAdapter() {

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return views.size();
		}

		public CharSequence getPageTitle(int position) {

			return titles.get(position);// 直接用适配器来完成标题的显示，所以从上面可以看到，我们没有使用PagerTitleStrip。当然你可以使用。

		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(views.get(position));
		}

		@Override
		public Object instantiateItem(View container, int position) {

			((ViewPager) container).addView(views.get(position));
			oncreatePage(position);
			return views.get(position);
		}

	};

	public class myOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			Animation animation = null;
			currIndex = arg0;
			// oncreatePage(currIndex);
		}

	}

	OnTouchListener onTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.setBackgroundColor(Color.parseColor("#03b4fe"));
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				v.setBackgroundColor(0);
			}
			return false;
		}
	};

	public void oncreatePage(final int arg0) {
		textViews = new ArrayList<TextView>();
		teachers = new ArrayList<TextView>();
		classrooms = new ArrayList<TextView>();
		classLayouts = new ArrayList<LinearLayout>();
		threeClassFlags = new ArrayList<TextView>();

		classLayouts.add((LinearLayout) views.get(arg0).findViewById(R.id.class1));
		classLayouts.add((LinearLayout) views.get(arg0).findViewById(R.id.class2));
		classLayouts.add((LinearLayout) views.get(arg0).findViewById(R.id.class3));
		classLayouts.add((LinearLayout) views.get(arg0).findViewById(R.id.class4));
		classLayouts.add((LinearLayout) views.get(arg0).findViewById(R.id.class5));
		for (int i = 0; i < classLayouts.size(); i++) {
			// classLayouts.get(i).setOnTouchListener(onTouchListener);
			classLayouts.get(i).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int weekDay = arg0;
					int dayTime = 0;
					switch (v.getId()) {
					case R.id.class1:
						dayTime = 0;
						break;
					case R.id.class2:
						dayTime = 2;
						break;
					case R.id.class3:
						dayTime = 5;
						break;
					case R.id.class4:
						dayTime = 7;
						break;
					case R.id.class5:
						dayTime = 10;
						break;
					default:
						break;
					}
					Bundle bundle = new Bundle();
					bundle.putInt("weekDay", weekDay);
					bundle.putInt("dayTime", dayTime);
					Intent it = new Intent(Schedule.this, ClassDetailActivity.class);
					it.putExtras(bundle);
					startActivity(it);

				}
			});
		}

		textViews.add((TextView) views.get(arg0).findViewById(R.id.text1));
		textViews.add((TextView) views.get(arg0).findViewById(R.id.text2));
		textViews.add((TextView) views.get(arg0).findViewById(R.id.text3));
		textViews.add((TextView) views.get(arg0).findViewById(R.id.text4));
		textViews.add((TextView) views.get(arg0).findViewById(R.id.text5));
		teachers.add((TextView) views.get(arg0).findViewById(R.id.teacher1));
		teachers.add((TextView) views.get(arg0).findViewById(R.id.teacher2));
		teachers.add((TextView) views.get(arg0).findViewById(R.id.Presenter));
		teachers.add((TextView) views.get(arg0).findViewById(R.id.teacher4));
		teachers.add((TextView) views.get(arg0).findViewById(R.id.teacher5));

		threeClassFlags.add((TextView) views.get(arg0).findViewById(R.id.threeClassFlag1));
		threeClassFlags.add((TextView) views.get(arg0).findViewById(R.id.threeClassFlag2));
		threeClassFlags.add((TextView) views.get(arg0).findViewById(R.id.threeClassFlag3));
		threeClassFlags.add((TextView) views.get(arg0).findViewById(R.id.threeClassFlag4));
		threeClassFlags.add((TextView) views.get(arg0).findViewById(R.id.threeClassFlag5));

		classrooms.add((TextView) views.get(arg0).findViewById(R.id.classroom1));
		classrooms.add((TextView) views.get(arg0).findViewById(R.id.classroom2));
		classrooms.add((TextView) views.get(arg0).findViewById(R.id.ctime));
		classrooms.add((TextView) views.get(arg0).findViewById(R.id.classroom4));
		classrooms.add((TextView) views.get(arg0).findViewById(R.id.classroom5));
		DatabaseHelper databaseHelper = new DatabaseHelper(Schedule.this);
		String sql = "select name,teacher,classroom from schedule where weekDay='" + arg0 + "'";

		Cursor cursor = databaseHelper.dql(sql, null);
		for (int i = 0; i < 5; i++) {
			switch (i) {
			case 0:
				cursor.moveToPosition(0);
				break;
			case 1:
				cursor.moveToPosition(2);
				break;
			case 2:
				cursor.moveToPosition(5);
				break;
			case 3:
				cursor.moveToPosition(7);
				break;
			case 4:
				cursor.moveToPosition(10);
				break;
			default:
				break;
			}

			// String aString=cursor.getString(0);
			if (!((cursor.getString(0).equals("null")) || (cursor.getString(0).equals("")))) {

				String a = cursor.getString(0);
				String bString = cursor.getString(1);
				textViews.get(i).setText(cursor.getString(0));
				teachers.get(i).setText(cursor.getString(1));
				classrooms.get(i).setText(cursor.getString(2));
				switch (i) {
				case 1:
					cursor.moveToPosition(4);
					if (!((cursor.getString(0).equals("null")) || (cursor.getString(0).equals("")))) {
						threeClassFlags.get(i).setText("三节连");
					}
					break;
				case 3:
					cursor.moveToPosition(9);
					if (!((cursor.getString(0).equals("null")) || (cursor.getString(0).equals("")))) {
						threeClassFlags.get(i).setText("三节连");
					}
					break;
				default:
					break;
				}
			} else {
				textViews.get(i).setText("点击添加课程");
				textViews.get(i).setTextColor(Color.parseColor("#b5b5b5"));
				teachers.get(i).setText(" ");
				classrooms.get(i).setText(" ");
			}
		}

	}

	
	public void switchUser(View view) {
		LinearLayout confirmLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.my_message_box,
				null);
		final Dialog alertDialog = new Dialog(Schedule.this, R.style.dialog);
		alertDialog.setContentView(confirmLayout);

		TextView messageBoxContent = ((TextView) confirmLayout.findViewById(R.id.messageBoxContent));
		messageBoxContent.setText("需要切换课程表账户？");
		final RelativeLayout positiveButton = (RelativeLayout) confirmLayout
				.findViewById(R.id.positiveButton);
		RelativeLayout negativeButton = (RelativeLayout) confirmLayout.findViewById(R.id.negativeButton);
		positiveButton.setOnTouchListener(new CommonOnTouchListener());
		negativeButton.setOnTouchListener(new CommonOnTouchListener());
		positiveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				DatabaseHelper databaseHelper=new DatabaseHelper(getApplicationContext());
				String sql="delete from SCHEDULE";
				databaseHelper.dml(sql, new Object[]{});
				SharedPreferences mysSharedPreferences = getSharedPreferences("handsSwjtu", Activity.MODE_PRIVATE);
				Editor editor=mysSharedPreferences.edit();
				editor.putBoolean("isScheduleLoaded", false);
				editor.commit();
				Intent it=new Intent(Schedule.this,DeanOfficeLoginActivity.class);
				startActivity(it);
				finish();
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
	
		
		

	public void finishActivity(View view){
		finish();
	}

	
	protected void onRestart() {
		mPagerAdapter.notifyDataSetChanged();
		super.onRestart();
	}



	public void onResume() {
		mPagerAdapter.notifyDataSetChanged();
		super.onResume();
		StatService.onResume(this);
	}

	public void onPause() {
		
		super.onPause();
		StatService.onPause(this);
	}

}
