package com.example.handsswjtu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.baidu.mobstat.StatService;
import com.handsSwjtu.common.DataProvider;
import com.handsSwjtu.common.DifferentColorListViewAdapter;
import com.handsSwjtu.httpConnect.HttpConnect;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.R.integer;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;

public class SecondClassMainActivity extends Activity {
	private ListView secondClassListView;
	private TabHost tabHost;
	private ListView myCourseResultListView;
	private boolean secondClassListHaveLoad = false;
	private boolean myCourseResultHaveLoad = false;
	private List<Map<String, Object>> secondClassMapsList;
	private StringBuffer message;
	private int index1=1;
	private int index2=1;
	private int category=1;
	private ProgressBar loadingProgressBar;
	private DifferentColorListViewAdapter secondClassListAdapter;
	private DifferentColorListViewAdapter myCourseResultAdapter;
	private static int SECOND_CLASS_LIST_MESSAGE = 2;
	private static int MY_COURSE_RESULT_MESSAGE = 1;
	private List<Map<String, Object>> myCourseResultMapsList;
	private boolean NO_MORE_DATA=false;
	private boolean loadingFlag=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_second_class_main);
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		TabWidget tabWidget = tabHost.getTabWidget();
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("课程列表").setContent(R.id.secondClassTab));
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("我的课程").setContent(R.id.myClassTab));
		secondClassListView = (ListView) findViewById(R.id.secondClassList);
		secondClassListView.setOnItemClickListener(secondClassListOnItemClickListener);
		myCourseResultListView = (ListView) findViewById(R.id.myClassList);
		myCourseResultListView.setOnItemClickListener(myCourseResultOnItemClickListener);
		loadingProgressBar = (ProgressBar) findViewById(R.id.loading);
		message=new StringBuffer();
		loadSeconClassList(1, 1);
		secondClassMapsList = new ArrayList<Map<String, Object>>();
		myCourseResultMapsList = new ArrayList<Map<String, Object>>();
		secondClassListAdapter = new DifferentColorListViewAdapter(SecondClassMainActivity.this, secondClassMapsList,
				R.layout.listview_second_class_list,
				new String[] { "Presenter", "CourseNameEx", "TeachTime", "showId" }, new int[] { R.id.Presenter,
						R.id.CourseNameEx, R.id.TeachTime, R.id.showId });
		secondClassListView.setAdapter(secondClassListAdapter);
		myCourseResultAdapter = new DifferentColorListViewAdapter(SecondClassMainActivity.this, myCourseResultMapsList,
				R.layout.listview_my_second_class_result_list, new String[] { "Presenter", "CourseNameEx", "TeachTime",
						"showId", "CourseScore" }, new int[] { R.id.Presenter, R.id.CourseNameEx, R.id.TeachTime,
						R.id.showId, R.id.courseScore });
		myCourseResultListView.setAdapter(myCourseResultAdapter);
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				if (tabId.equals("tab2") && !myCourseResultHaveLoad) {
					// Toast.makeText(SecondClassMainActivity.this,
					// "当前标签页为"+tabId,Toast.LENGTH_SHORT).show();
					loadMyCourseResult(1);
					
					myCourseResultHaveLoad = true;
				}
			}
		});
		secondClassListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						& view.getLastVisiblePosition() == (view.getCount() - 1) && !loadingFlag) {
					if (!NO_MORE_DATA) {
						index1++;
						loadingFlag = true;
						loadingProgressBar.setVisibility(View.VISIBLE);
						loadSeconClassList(index1, category);
					}else {
						Toast.makeText(SecondClassMainActivity.this, "没有更多数据了哦", Toast.LENGTH_SHORT).show();
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
		
		myCourseResultListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						& view.getLastVisiblePosition() == (view.getCount() - 1) && !loadingFlag) {
					if (!NO_MORE_DATA) {
						index2++;
						loadingFlag = true;
						loadingProgressBar.setVisibility(View.VISIBLE);
						loadMyCourseResult(index2);
					}else {
						Toast.makeText(SecondClassMainActivity.this, "没有更多数据了哦", Toast.LENGTH_SHORT).show();
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
	}
	
	
	public void loadMoreSecondClassData(final int index) {
		loadingProgressBar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putInt("messageType", MY_COURSE_RESULT_MESSAGE);
				msg.setData(bundle);
				msg.what = DataProvider.MySecondClassMapsProvider(myCourseResultMapsList, message, index);
				if (myCourseResultMapsList.size()%10!=0) {
					NO_MORE_DATA=true;
				}
			}
		}).start();
	}
	
	

	OnItemClickListener secondClassListOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			Intent it = new Intent(SecondClassMainActivity.this, SecondClassDetailActivity.class);
			// it.putStringExtra("ID", secondClassMapsList.get(arg2).get("ID"));
			it.putExtra("ID", secondClassMapsList.get(arg2).get("ID").toString());
			startActivityForResult(it,0);
		}
	};

	OnItemClickListener myCourseResultOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			Intent it = new Intent(SecondClassMainActivity.this, SecondClassMyCourseDetailActivity.class);
			// it.putStringExtra("ID", secondClassMapsList.get(arg2).get("ID"));
			it.putExtra("ID", myCourseResultMapsList.get(arg2).get("ID").toString());
			startActivityForResult(it,0);
		}

	};

	public void loadMyCourseResult(final int index) {
		loadingFlag=true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putInt("messageType", MY_COURSE_RESULT_MESSAGE);
				msg.setData(bundle);
				msg.what = DataProvider.MySecondClassMapsProvider(myCourseResultMapsList, message, index);
				if (myCourseResultMapsList.size()%15!=0) {
					NO_MORE_DATA=true;
				}
				handler.sendMessage(msg);
			}
		}).start();
	}

	public void loadSeconClassList(final int index, final int category) {
		loadingProgressBar.setVisibility(View.VISIBLE);
		loadingFlag=true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// String resultString=HttpConnect.getSecondClassList(1, null);
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putInt("messageType", SECOND_CLASS_LIST_MESSAGE);
				msg.setData(bundle);
				msg.what = DataProvider.secondClassListMapsProvider(secondClassMapsList, message, index, category);
				if (secondClassMapsList.size()%15!=0) {
					NO_MORE_DATA=true;
				}
				handler.sendMessage(msg);
			}
		}).start();
		;
	}

	public void initSecondClassList() {
		secondClassListAdapter.notifyDataSetChanged();

	}

	public void initMySecondClassResult() {
		myCourseResultAdapter.notifyDataSetChanged();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case 0:
				Toast.makeText(SecondClassMainActivity.this, "数据获取成功", Toast.LENGTH_SHORT).show();
				int a = msg.getData().getInt("messageType");
				if (msg.getData().getInt("messageType") == SECOND_CLASS_LIST_MESSAGE) {
					initSecondClassList();
				} else {
					initMySecondClassResult();
				}
				break;
			case 1:
				Toast.makeText(SecondClassMainActivity.this, message, Toast.LENGTH_SHORT).show();
			case 2:
				Toast.makeText(SecondClassMainActivity.this, "当前没有数据", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(SecondClassMainActivity.this, "联网失败，请重试", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			loadingProgressBar.setVisibility(View.INVISIBLE);
			loadingFlag=false;
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		int a=1;
		int b=a;
		if (resultCode == RESULT_OK) {
			finish();
			Intent it = new Intent(this, SecondClassMainActivity.class);
			startActivity(it);

		}
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
