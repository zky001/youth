package com.example.handsswjtu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.mobstat.StatService;
import com.example.handsswjtu.R.id;
import com.handsSwjtu.Objects.NewsListData;
import com.handsSwjtu.common.DataProvider;
import com.handsSwjtu.common.DatabaseHelper;
import com.handsSwjtu.common.SharePreferenceHelper;
import com.handsSwjtu.httpConnect.HttpConnect;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewsListActivity extends Activity {

	private View menuView;
	private View contentView;
	private View totalView;
	private float xDown;
	private float xMove;
	private float xUp;
	private float yUp;
	private int menuWidth;
	private FrameLayout.LayoutParams contentParams;
	private FrameLayout.LayoutParams menuParams;
	private boolean isMenuVisible = false;
	private LinearLayout deanOfficeButton;
	private LinearLayout yangHuaButton;
	private LinearLayout swjtuNewsButton;
	private LinearLayout youthButton;
	private LinearLayout employButton;
	private ProgressBar loadingProgressBar;
	private ExpandableListView newsListView;
	private List<Map<String, Object>> groupLists;
	private SimpleExpandableListAdapter simpleExpandableListAdapter;
	private RelativeLayout showMenuButton;
	private NewsListData newsListData;
	private List<List<Map<String, Object>>> childListsTotal;
	private DatabaseHelper databaseHelper;
	private String sqlString;
	private static final int DEANOFFICE = 1;
	private static final int YANGHUA = 2;
	private static final int YANGHUACOLLEGE = 21;
	private static final int SWJTUNEWS = 3;
	private static final int YOUTH = 4;
	private static final int EMPLOYTOTAL = 5;
	private static final int EMPLOYSWJTU = 52;
	private boolean isLoading = false;
	private int pageNo = 1;
	private int newsTypeNow = 3;
	private TextView newsListTitleTextView;
	private Spinner yangHuaCollegeNoSpinner;
	private Spinner employSwitchSpinner;
	private String[] employStrings = { "招聘信息", "校园招聘会" };
	private String[] yangHuaCollegeNoStrings = { "学生处", "土木", "机械", "信息", "艺传", "电气", "经管", "外语", "交运", "材料", "物理",
			"建筑", "生命", "地学", "心理", "数学", "力学", "公管", "牵引", "政治", "体育", "茅院", "詹院" };
	// private String[] yangHuaCollegeNoStrings = { "土木", "机械", "信息", "艺传",
	// "电气", "", "经管", "外语", "交运", "材料", "物理", "建筑",
	// "生命", "地学", "心理", "数学", "", "力学", "", "公管", "", "牵引", "政治", "", "", "体育",
	// "茅院", "詹院" };
	private int yangHuaCollegeNow;
	private boolean isSpinnerFirstLoad = true;
	private SharePreferenceHelper sharePreferenceHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_news_list);
		sharePreferenceHelper = new SharePreferenceHelper();
		newsTypeNow = sharePreferenceHelper.getNewsTypeLastView();
		menuView = (View) findViewById(R.id.menuLayout);
		contentView = (View) findViewById(R.id.contentLayout);
		contentView.setOnTouchListener(contentViewOnTouchListener);
		totalView = (View) findViewById(R.id.totalLayout);

		loadingProgressBar = (ProgressBar) findViewById(R.id.deanOfficeNewsListloding);
		newsListView = (ExpandableListView) findViewById(R.id.newsList);
		// newsListView.setOnTouchListener(contentViewOnTouchListener);
		yangHuaCollegeNoSpinner = (Spinner) findViewById(R.id.collegeNo);
		deanOfficeButton = (LinearLayout) findViewById(R.id.deanOfficeButton);
		yangHuaButton = (LinearLayout) findViewById(R.id.yangHuaButton);
		swjtuNewsButton = (LinearLayout) findViewById(R.id.swjtuNewsButton);
		youthButton = (LinearLayout) findViewById(R.id.youthButton);
		employButton = (LinearLayout) findViewById(R.id.employTotalButton);
		employButton.setOnTouchListener(meunViewOnTouchListener);
		employButton.setOnClickListener(menuOnClickListener);
		newsListTitleTextView = (TextView) findViewById(R.id.newsListTiltle);
		yangHuaCollegeNoSpinner.setOnItemSelectedListener(onItemSelectedListener);
		yangHuaCollegeNoSpinner.setVisibility(View.INVISIBLE);
		ArrayAdapter<String> weekNoAdapter = new ArrayAdapter<String>(this, R.layout.my_spinner,
				yangHuaCollegeNoStrings);
		yangHuaCollegeNoSpinner.setAdapter(weekNoAdapter);
		// employSwitchSpinner=(Spinner)findViewById(R.id.employSwitch);
		// ArrayAdapter<String> employSwitchAdapter = new
		// ArrayAdapter<String>(this, R.layout.my_spinner,
		// employStrings);
		// employSwitchSpinner.setAdapter(employSwitchAdapter);

		swjtuNewsButton.setOnClickListener(menuOnClickListener);
		youthButton.setOnClickListener(menuOnClickListener);
		swjtuNewsButton.setOnTouchListener(meunViewOnTouchListener);
		youthButton.setOnTouchListener(meunViewOnTouchListener);
		deanOfficeButton.setOnClickListener(menuOnClickListener);
		yangHuaButton.setOnClickListener(menuOnClickListener);
		deanOfficeButton.setOnTouchListener(meunViewOnTouchListener);
		yangHuaButton.setOnTouchListener(meunViewOnTouchListener);
		contentView.setOnTouchListener(contentViewOnTouchListener);

		showMenuButton = (RelativeLayout) findViewById(R.id.showMenu);
		showMenuButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isMenuVisible) {
					scollToBegin(2);
				} else {
					scollToEnd(2);
				}
			}
		});
		contentParams = (FrameLayout.LayoutParams) contentView.getLayoutParams();
		contentParams.gravity = Gravity.TOP | Gravity.LEFT;
		menuParams = (FrameLayout.LayoutParams) menuView.getLayoutParams();
		newsListView = (ExpandableListView) findViewById(R.id.newsList);
		newsListView.setOnChildClickListener(onChildClickListener);
		newsListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				contentView.onTouchEvent(event);
				return false;
			}
		});
		// loadingProgressBar.setVisibility(View.INVISIBLE);
		HttpConnect.isWap(this);
		WindowManager wm = (WindowManager) (this.getSystemService(Context.WINDOW_SERVICE));
		int width = wm.getDefaultDisplay().getWidth();
		contentParams.width = width;
		menuWidth = (int) 7 * width / 14;
		contentParams.leftMargin = 0;
		contentView.setLayoutParams(contentParams);
		menuParams.width = menuWidth;
		menuView.setLayoutParams(menuParams);
		initValues();
		setNewsTitle(newsTypeNow);
		if (newsTypeNow==YANGHUA) {
			yangHuaCollegeNoSpinner.setVisibility(View.VISIBLE);
		}
		newsListView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
						loadingProgressBar.setVisibility(View.VISIBLE);
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (!isLoading) {
									isLoading = true;
									NewsListData newsListDataTemp = newsListData;
									newsListData = DataProvider.loadMoreNewsList(newsListData, ++pageNo, newsTypeNow);
									if (newsListData == null) {
										handler.sendEmptyMessage(4);
										newsListData = newsListDataTemp;
									} else {
										handler.sendEmptyMessage(2);
									}

									isLoading = false;
								}
							}
						}).start();
					}
					break;
				default:
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// DataHandler.deanOfficeNewsListHandler(NewsListActivity.this,
				// String.valueOf(1));
				DataProvider.newsListToDatabase(NewsListActivity.this, newsTypeNow);
				handler.sendEmptyMessage(3);
			}
		}).start();
		
		
	}

	public void initValues() {

		newsListData = DataProvider.newsListFromDataBase(newsTypeNow);
		groupLists = newsListData.getGroupLists();
		childListsTotal = newsListData.getChildListsTotal();
		// simpleExpandableListAdapter=new SimpleExpandableListAdapter(this,
		// groupLists, , collapsedGroupLayout, groupFrom, groupTo, childData,
		// childLayout, childFrom, childTo)
		simpleExpandableListAdapter = new SimpleExpandableListAdapter(this, newsListData.getGroupLists(),
				R.layout.expandable_newslist_group, new String[] { "newsTime", "newsCode" }, new int[] { R.id.newsTime,
						R.id.newsCode }, newsListData.getChildListsTotal(), R.layout.expandable_newslist_child,
				new String[] { "newsTitle" }, new int[] { R.id.chatWithWho });
		newsListView.setAdapter(simpleExpandableListAdapter);
		for (int i = 0; i < groupLists.size(); i++) {
			newsListView.expandGroup(i);

		}
	}

	OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			if (!isSpinnerFirstLoad) {
				// if (arg2 < 5) {
				// yangHuaCollegeStringNow = "00" + String.valueOf(arg2 + 1);
				// } else if (arg2 < 10) {
				// yangHuaCollegeStringNow = "00" + String.valueOf(arg2 + 2);
				// } else if (arg2 < 15) {
				// yangHuaCollegeStringNow = "0" + String.valueOf(arg2 + 2);
				// } else if (arg2 < 16) {
				// yangHuaCollegeStringNow = "0" + String.valueOf(arg2 + 3);
				// } else if (arg2 < 17) {
				// yangHuaCollegeStringNow = "0" + String.valueOf(arg2 + 4);
				// } else if (arg2 < 19) {
				// yangHuaCollegeStringNow = "0" + String.valueOf(arg2 + 5);
				// } else if (arg2 < 22) {
				// yangHuaCollegeStringNow = "0" + String.valueOf(arg2 + 7);
				// }
				arg2 = arg2 - 1;
				if (arg2 == -1) {
					yangHuaCollegeNow = 2;
				} else if (arg2 < 5) {
					yangHuaCollegeNow = 2100 + arg2 + 1;
				} else if (arg2 < 10) {
					yangHuaCollegeNow = 2100 + arg2 + 2;
				} else if (arg2 < 15) {
					yangHuaCollegeNow = 2100 + arg2 + 2;
				} else if (arg2 < 16) {
					yangHuaCollegeNow = 2100 + arg2 + 3;
				} else if (arg2 < 17) {
					yangHuaCollegeNow = 2100 + arg2 + 4;
				} else if (arg2 < 19) {
					yangHuaCollegeNow = 2100 + arg2 + 5;
				} else if (arg2 < 22) {
					yangHuaCollegeNow = 2100 + arg2 + 7;
				}

				newsTypeNow = yangHuaCollegeNow;
				initValues();
				loadingProgressBar.setVisibility(View.VISIBLE);
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						DataProvider.newsListToDatabase(NewsListActivity.this, newsTypeNow);
						newsListData = DataProvider.newsListFromDataBase(newsTypeNow);
						handler.sendEmptyMessage(3);
					}
				}).start();
			} else {
				isSpinnerFirstLoad = false;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};

	OnClickListener menuOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int a = 1;
			int b = 1;
			b = a;
			switch (v.getId()) {
			case R.id.deanOfficeButton: {
				sharePreferenceHelper.setNewsTypeLastView(DEANOFFICE);
				if (newsTypeNow != DEANOFFICE) {
					newsTypeNow = DEANOFFICE;
					yangHuaCollegeNoSpinner.setVisibility(View.INVISIBLE);
					newsListTitleTextView.setText("教务通知");
					initValues();
					scollToBegin(1);
					pageNo = 1;
					loadingProgressBar.setVisibility(View.VISIBLE);
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							newsTypeNow = DEANOFFICE;

							if (!DataProvider.newsListToDatabase(NewsListActivity.this, newsTypeNow)) {
								handler.sendEmptyMessage(4);
							} else {
								newsListData = DataProvider.newsListFromDataBase(newsTypeNow);
								handler.sendEmptyMessage(3);
							}

						}
					}).start();
					
				}
				
				break;
			}
			case R.id.yangHuaButton: {
				sharePreferenceHelper.setNewsTypeLastView(YANGHUA);
				if (newsTypeNow != YANGHUA) {
					newsTypeNow = YANGHUA;
					yangHuaCollegeNoSpinner.setVisibility(View.VISIBLE);
					newsListTitleTextView.setText("扬华通知");
					initValues();
					scollToBegin(1);
					pageNo = 1;
					loadingProgressBar.setVisibility(View.VISIBLE);
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub

							DataProvider.newsListToDatabase(NewsListActivity.this, newsTypeNow);
							newsListData = DataProvider.newsListFromDataBase(newsTypeNow);
							handler.sendEmptyMessage(3);
						}
					}).start();
				}

			}
				break;
			case R.id.swjtuNewsButton: {
				if (newsTypeNow != SWJTUNEWS)
					changeNewsList(SWJTUNEWS);
			}
				break;
			case R.id.youthButton: {
				if (newsTypeNow != YOUTH)
					changeNewsList(YOUTH);
			}
				break;
			case R.id.employTotalButton: {
				if (newsTypeNow != EMPLOYTOTAL)
					changeNewsList(EMPLOYTOTAL);
			}
				break;
			default:
				break;
			}

		}
	};

	
	public void setNewsTitle(int type){
		sharePreferenceHelper.setNewsTypeLastView(type);
		switch (type) {
		case 1:
			newsListTitleTextView.setText("教务通知");
			break;
		case 2:
			newsListTitleTextView.setText("扬华通知");
			break;
		case 3:
			newsListTitleTextView.setText("交大新闻网");
			break;
		case 4:
			newsListTitleTextView.setText("新青年素质网");
			break;
		case 5:
			newsListTitleTextView.setText("校园招聘会");
			break;
		default:
			break;
		}
	}
	
	
	public void changeNewsList(int type) {
		newsTypeNow = type;
		pageNo = 1;
		yangHuaCollegeNoSpinner.setVisibility(View.INVISIBLE);
		setNewsTitle(type);
		initValues();
		scollToBegin(1);
		loadingProgressBar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				if (!DataProvider.newsListToDatabase(NewsListActivity.this, newsTypeNow)) {
					handler.sendEmptyMessage(4);
				} else {
					newsListData = DataProvider.newsListFromDataBase(newsTypeNow);
					handler.sendEmptyMessage(3);
				}

			}
		}).start();
	}

	ExpandableListView.OnChildClickListener onChildClickListener = new ExpandableListView.OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
			// TODO Auto-generated method stub

			String newsCode = (String) childListsTotal.get(groupPosition).get(childPosition).get("newsCode");
			String newsTitle = (String) childListsTotal.get(groupPosition).get(childPosition).get("newsTitle");
			Bundle bundle = new Bundle();
			bundle.putString("newsCode", newsCode);
			bundle.putString("newsTitle", newsTitle);
			bundle.putInt("newsTypeNow", newsTypeNow);
			Intent it = new Intent(NewsListActivity.this, NewsDetailActivity.class);
			it.putExtras(bundle);
			startActivity(it);
			return false;
		}
	};

	View.OnTouchListener meunViewOnTouchListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.setBackgroundColor(Color.parseColor("#03b4fe"));
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				v.setBackgroundColor(Color.argb(0, 0, 0, 0));
			}
			return false;
		}
	};

	View.OnTouchListener contentViewOnTouchListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			int a = event.getAction();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				xDown = event.getRawX();
				// return true;
				break;
			case MotionEvent.ACTION_MOVE:
				// if
				// (contentParams.leftMargin>=0&&contentParams.leftMargin<=menuWidth)
				// {
				xMove = event.getRawX();
				int distance = (int) (xMove - xDown);

				if (isMenuVisible) {
					contentParams.leftMargin = menuWidth + distance;
				} else {
					contentParams.leftMargin = distance;
				}
				// contentParams.leftMargin = distance;
				if (contentParams.leftMargin < 0) {
					contentParams.leftMargin = 0;
					isMenuVisible = false;
				} else if (contentParams.leftMargin > menuWidth) {
					contentParams.leftMargin = menuWidth;
					isMenuVisible = true;
				}
				contentView.setLayoutParams(contentParams);
				// }

				break;
			case MotionEvent.ACTION_UP:
				// if (isMeanVisible) {
				if (contentParams.leftMargin <= menuWidth / 2) {
					scollToBegin(1);

				} else {
					scollToEnd(1);
				}
				// return false;
				break;
			default:
				break;
			}
			return true;
		}

	};

	public void scollToBegin(final int speed) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (contentParams.leftMargin > 0) {
					contentParams.leftMargin = contentParams.leftMargin - speed;
					handler.sendEmptyMessage(0);
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}).start();
		isMenuVisible = false;
	}

	public void scollToEnd(final int speed) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (contentParams.leftMargin < menuWidth) {
					contentParams.leftMargin = contentParams.leftMargin + speed;
					handler.sendEmptyMessage(0);
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}).start();
		isMenuVisible = true;
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				contentView.setLayoutParams(contentParams);

				break;
			case 2:
				simpleExpandableListAdapter.notifyDataSetChanged();
				loadingProgressBar.setVisibility(View.INVISIBLE);
				for (int i = 0; i < newsListData.getGroupLists().size(); i++) {
					newsListView.expandGroup(i);
				}
				break;
			case 3:
				initValues();
				loadingProgressBar.setVisibility(View.INVISIBLE);
				break;
			case 4:
				Toast.makeText(NewsListActivity.this, "加载失败，请检查网络连接", Toast.LENGTH_LONG).show();
				loadingProgressBar.setVisibility(View.INVISIBLE);
				break;
			default:
				break;
			}

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

}
