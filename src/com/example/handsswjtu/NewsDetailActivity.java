package com.example.handsswjtu;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mobstat.StatService;
import com.handsSwjtu.Objects.NewsEntity;
import com.handsSwjtu.common.CommonOnTouchListener;
import com.handsSwjtu.common.DataProvider;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewsDetailActivity extends Activity {

	private WebView newsDetailWebView;
	private TextView newsTitleTextView;
	private ProgressBar loadingProgressBar;
	private ProgressBar loadingContentProgressBar;
	private PopupWindow menuWindow;
	private String baseUrl = "http://dean.swjtu.edu.cn/servlet/NewsView?NewsID=";
	private String baseYangHuaUrl = "http://www.yanghua.net/WebSite/StudentOffice/ShowNews.aspx?Id=";
	private RelativeLayout menuButton;
	private boolean isMenuVisible = false;
	private TextView simpleNewsContentTextView;
	private String simpleNewsContentString;
	private NewsEntity newsEntity;
	private int newsTypeNow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_news_detail);
		newsDetailWebView = (WebView) findViewById(R.id.newsDetailBrower);
		newsTitleTextView = (TextView) findViewById(R.id.chatWithWho);
		loadingProgressBar = (ProgressBar) findViewById(R.id.loading);
		loadingContentProgressBar = (ProgressBar) findViewById(R.id.loadingContent);
		newsTitleTextView.setText(getIntent().getExtras().getString("newsTitle"));
		newsTypeNow = getIntent().getExtras().getInt("newsTypeNow");
		if (newsTypeNow<100) {
			newsTypeNow=newsTypeNow;
		}else if(newsTypeNow<1000){
			newsTypeNow=newsTypeNow/10;
		}else {
			newsTypeNow=newsTypeNow/100;
		}
		LayoutInflater layoutInflater = (LayoutInflater) NewsDetailActivity.this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View popView = layoutInflater.inflate(R.layout.news_detail_popwindow, null);
		menuWindow = new PopupWindow(popView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		simpleNewsContentTextView = (TextView) findViewById(R.id.simpleNewsContent);
		menuWindow.setFocusable(true);
		menuWindow.setTouchable(true);
		menuWindow.setOutsideTouchable(true);
		menuWindow.update();
		menuWindow.setBackgroundDrawable(new BitmapDrawable());
		menuButton = (RelativeLayout) findViewById(R.id.more);
		switch (newsTypeNow) {
		case 1:
			baseUrl="http://dean.swjtu.edu.cn/servlet/NewsView?NewsID=";
			break;
		case 2:baseUrl="http://www.yanghua.net/WebSite/StudentOffice/ShowNews.aspx?Id=";
			break;
		default:
			break;
		}
		menuButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!menuWindow.isShowing()) {
					// menuWindow.showAtLocation(NewsDetailActivity.this.findViewById(R.id.content),
					// Gravity.TOP|Gravity.RIGHT, 0, 0);
					menuWindow.showAsDropDown(NewsDetailActivity.this.findViewById(R.id.more));
				} else {
					menuWindow.dismiss();
				}
			}
		});
		newsDetailWebView.getSettings().setBlockNetworkImage(true);
		newsDetailWebView.getSettings().setSupportZoom(true);
		// newsDetailWebView.getSettings().setBuiltInZoomControls(true);
		newsDetailWebView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				loadingProgressBar.setProgress(progress);
				if (progress == 100) {
					loadingProgressBar.setVisibility(View.INVISIBLE);
				}
			}
		});
		// newsDetailWebView.loadUrl(baseUrl+getIntent().getExtras().getString("newsCode"));
		newsDetailWebView.setVisibility(View.INVISIBLE);
		loadData();

	}

	public void loadData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					newsEntity = DataProvider.newsDetailProvider(getIntent().getExtras().getString("newsCode"),newsTypeNow);
					// simpleNewsContentString=new
					// JSONObject(DataHandler.getSimpleNewsContent(getIntent().getExtras().getString("newsCode"))).getString("1");
					handle.sendEmptyMessage(1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void showDialog() {
		LinearLayout confirmLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.my_message_box, null);
		final Dialog alertDialog = new Dialog(NewsDetailActivity.this, R.style.dialog);
		alertDialog.setContentView(confirmLayout);
		alertDialog.show();
		TextView messageBoxContent = ((TextView) confirmLayout.findViewById(R.id.messageBoxContent));
		messageBoxContent.setText("联网出错了额，要重试吗入？");
		final RelativeLayout positiveButton = (RelativeLayout) confirmLayout.findViewById(R.id.positiveButton);
		RelativeLayout negativeButton = (RelativeLayout) confirmLayout.findViewById(R.id.negativeButton);
		positiveButton.setOnTouchListener(new CommonOnTouchListener());
		negativeButton.setOnTouchListener(new CommonOnTouchListener());
		positiveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loadData();
				loadingContentProgressBar.setVisibility(View.VISIBLE);
				alertDialog.dismiss();
				// positiveButtonsetBackgroundColor(0);
			}
		});

		negativeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				alertDialog.dismiss();
				finish();
			}
		});
	}

	Handler handle = new Handler() {

		public void handleMessage(Message msg) {
			// simpleNewsContentTextView.setText(simpleNewsContentString);
			simpleNewsContentTextView.setVisibility(View.VISIBLE);
			loadingContentProgressBar.setVisibility(View.INVISIBLE);
			if (newsEntity == null) {
				Toast.makeText(NewsDetailActivity.this, "联网失败，点击重试", Toast.LENGTH_LONG).show();
				showDialog();
				loadingContentProgressBar.setVisibility(View.INVISIBLE);
			} else {

				if (newsEntity.isWebPage()) {
					simpleNewsContentTextView.setText(newsEntity.getNewsContent());
					if (newsEntity.isHaveImg()) {
						WindowManager wm = (WindowManager) (NewsDetailActivity.this
								.getSystemService(Context.WINDOW_SERVICE));
						int width = wm.getDefaultDisplay().getWidth();
						for (int i = 0; i < newsEntity.getNewsImg().size(); i++) {
							ImageView imageView = new ImageView(NewsDetailActivity.this);
							imageView.setImageBitmap(newsEntity.getNewsImg().get(i));
							((LinearLayout) NewsDetailActivity.this.findViewById(R.id.newsContent)).addView(imageView,
									new LinearLayout.LayoutParams(50, 150));
							LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView
									.getLayoutParams();
							layoutParams.width = width-50;
							int a=newsEntity.getNewsImg().get(i).getHeight();
							layoutParams.topMargin=50;
							layoutParams.height = (int) ((float)((float)(layoutParams.width/ (float)newsEntity.getNewsImg().get(i).getWidth())) * (float)(newsEntity.getNewsImg().get(i).getHeight()));
							imageView.setLayoutParams(layoutParams);
						}
					}
				} else {
					newsDetailWebView.loadUrl(baseUrl + getIntent().getExtras().getString("newsCode"));
				}
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
