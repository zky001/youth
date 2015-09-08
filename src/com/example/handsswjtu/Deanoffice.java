package com.example.handsswjtu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mobstat.StatService;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Deanoffice extends Activity {

	private ProgressBar scoreExcuteWait;
	private ArrayList<View> pageViews;
	private ArrayList<String> titles;
	private ImageView imageView;
	private ViewPager mViewPager;
	private int offset = 0;
	private int currIndex = 0;
	private int bmpw;
	private View view1, view2, view3;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_deanoffice);
		LayoutInflater mLi = LayoutInflater.from(this);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setOnPageChangeListener(new myOnPageChangeListener());
		view2 = mLi.inflate(R.layout.activity_score_inquiry, null);
		view1 = mLi.inflate(R.layout.activity_search_empty_classrooms, null);

		final ArrayList<View> views = new ArrayList<View>();
		views.add(view1);
		views.add(view2);

		titles = new ArrayList<String>();
		titles.add("空闲教室查询");
		titles.add("教务处登录");

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
				
				return views.get(position);
			}
		};

		mViewPager.setAdapter(mPagerAdapter);
		// scoreExcuteWait = (ProgressBar) findViewById(R.id.scoreExcuteWait);
		// scoreExcuteWait.setVisibility(View.INVISIBLE);
		// ImageButton scoreInquirySubmit = (ImageButton)
		// findViewById(R.id.scoreInquirySubmit);
		// scoreInquirySubmit.setOnClickListener(scoreInquiryOnClick);

	}

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
			switch (arg0) {
			case 0:

				if (currIndex == arg0 + 1) {
					animation = new TranslateAnimation(50 * (arg0 + 1),
							50 * arg0, 0, 0);
				}
				break;
			case 1:
				onCreatePage1();
				if (currIndex == arg0 - 1) {
					animation = new TranslateAnimation(50 * (arg0 - 1),
							50 * arg0, 0, 0);
				}
				break;
			default:
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
		}

	}

	public void onCreatePage1() {
		scoreExcuteWait = (ProgressBar) findViewById(R.id.scoreExcuteWait);
		scoreExcuteWait.setVisibility(View.INVISIBLE);
		ImageView scoreInquirySubmit = (ImageView) findViewById(R.id.scoreInquirySubmit);
		scoreInquirySubmit.setOnClickListener(scoreInquiryOnClick);
	}

	ImageView.OnClickListener scoreInquiryOnClick = new ImageView.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			scoreExcuteWait.setVisibility(View.VISIBLE);
			new Thread(update).start();

		}
	};
	public Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			scoreExcuteWait.setVisibility(View.INVISIBLE);
		}

	};

	Runnable update = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			EditText EditTextStudentNumber = (EditText) findViewById(R.id.stuCode);
			EditText EditTextPassword = (EditText) findViewById(R.id.password);
			String studentNum = EditTextStudentNumber.getText().toString();
			String password = EditTextPassword.getText().toString();
			String url = "http://182.18.22.178:80/handsSwjtu.php?version=0.1&studentNum="
					+ studentNum + "&password=" + password;
			getServerJsonDataWithNoType(url);

		}

	};

	public void getServerJsonDataWithNoType(String url) {
		int res = 0;
		HttpClient client = new DefaultHttpClient();
		StringBuilder str = new StringBuilder();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Host", "ftp109631.host252.web522.com");
		httpGet.addHeader("Content-Type", "application/json");
		httpGet.addHeader("charset", HTTP.UTF_8);
		//
		try {
			HttpResponse httpRes = client.execute(httpGet);
			httpRes = client.execute(httpGet);
			res = httpRes.getStatusLine().getStatusCode();
			if (res == 200) {
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(httpRes.getEntity().getContent()));
				for (String s = buffer.readLine(); s != null; s = buffer
						.readLine()) {
					str.append(s);
				}
				try {

					// TextView TextViewInquiryResult = (TextView)
					// findViewById(R.id.result);

					// JSONObject json new
					// JSONObject(str.toString()).getJSONObject("content");
					JSONObject json = new JSONObject(str.toString());
					int a = 0;
					String inquiryResult = json.getString("1");
					Intent it = new Intent(Deanoffice.this,
							InquiryResultActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("inquiryResult", inquiryResult);
					it.putExtra("result", bundle);
					handler.sendEmptyMessage(0);
					startActivity(it);
				} catch (Exception e) {
					Log.i("sg", e.toString());
				}
			} else {
			}
		} catch (Exception e) {
			Log.i("Tag", e.toString());
		}

	}
	public void onResume() {
		
		super.onResume();
		StatService.onResume(this);
	}

	public void onPause() {
		
		super.onPause();
		StatService.onPause(this);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.score_inquiry, menu);
		return true;
	}

}
