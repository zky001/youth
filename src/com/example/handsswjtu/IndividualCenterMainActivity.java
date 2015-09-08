package com.example.handsswjtu;

import org.json.JSONObject;

import com.baidu.android.pushservice.richmedia.MediaListActivity;
import com.baidu.mobstat.StatService;
import com.baidu.push.example.PushDemoActivity;
import com.handsSwjtu.common.BtnOnTouchListener;
import com.handsSwjtu.common.CommonOnTouchListener;
import com.handsSwjtu.common.SharePreferenceHelper;
import com.handsSwjtu.httpConnect.HttpConnect;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class IndividualCenterMainActivity extends Activity {

	
	private ListView msgListView; 
	private LinearLayout lyMsgBox;
	private LinearLayout lyMySwjtuKnow;
	private LinearLayout lyPushSetting;
	private LinearLayout lySystemMsg;
	private LinearLayout lypersonalIconSettiing;
	private LinearLayout logoutBtn;
	private ProgressBar loadingProgressBar;
	private String errorString;
	private SharePreferenceHelper sharePreferenceHelper;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_individual_center_main);
		loadingProgressBar=(ProgressBar)findViewById(R.id.loading);
		lyMySwjtuKnow=(LinearLayout)findViewById(R.id.mySwjtuKnowLy);
		lyPushSetting=(LinearLayout)findViewById(R.id.pushSettiingLy);
		lypersonalIconSettiing=(LinearLayout)findViewById(R.id.personalIconSettiingLy);
		logoutBtn=(LinearLayout)findViewById(R.id.logoutBtn);
		lypersonalIconSettiing.setOnTouchListener(new CommonOnTouchListener());
		lyMySwjtuKnow.setOnTouchListener(new CommonOnTouchListener());
		lyPushSetting.setOnTouchListener(new CommonOnTouchListener());
		lyMySwjtuKnow.setOnClickListener(onClickListener);
		lyPushSetting.setOnClickListener(onClickListener);
		lypersonalIconSettiing.setOnClickListener(onClickListener);
		logoutBtn.setOnClickListener(onClickListener);
		lySystemMsg=(LinearLayout)findViewById(R.id.systemMsgLy);
		lySystemMsg.setOnTouchListener(new CommonOnTouchListener());
		lySystemMsg.setOnClickListener(onClickListener);
		sharePreferenceHelper=new SharePreferenceHelper();
		logoutBtn.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					v.setBackgroundColor(Color.parseColor("#03b4fe"));
				}
				if(event.getAction() == MotionEvent.ACTION_UP){
					v.setBackgroundResource(R.drawable.logoutbtnbkg);
				}
				
				return false;
			}
		});
		
	}

	OnClickListener onClickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent it;
			switch (v.getId()) {
			case R.id.mySwjtuKnowLy:
				it=new Intent(IndividualCenterMainActivity.this,MySwjtuKnowActivity.class);
				startActivity(it);
				break;
			case R.id.pushSettiingLy:
				it=new Intent(IndividualCenterMainActivity.this,PushSettingActivity.class);
				startActivity(it);
				break;
			case R.id.personalIconSettiingLy:
				it=new Intent(IndividualCenterMainActivity.this,PersonalIconSettingActivity.class);
				startActivity(it);
				break;
			case R.id.logoutBtn:
				logout();
			case R.id.systemMsgLy:
			Intent sendIntent = new Intent();
			sendIntent.setClass(IndividualCenterMainActivity.this,
					MediaListActivity.class);
			sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			IndividualCenterMainActivity.this.startActivity(sendIntent);
			break;
			default:
				break;
			}

		}
	};
	
	
	public void logout() {
		loadingProgressBar.setVisibility(View.VISIBLE);
		sharePreferenceHelper.setHaveLogged(false);
		sharePreferenceHelper.setStuCode(null);
		sharePreferenceHelper.setPushSetting("[1,2,3,4]");
		sharePreferenceHelper.setIsAutoLogin(false);
		sharePreferenceHelper.setLoginPassword("");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String resultString = HttpConnect.logout();
				if (resultString != null) {
					try {
						JSONObject jsonObject = new JSONObject(resultString);
						errorString = jsonObject.getString("errorMsg");
						if (jsonObject.getInt("state") == 0) {
							handler.sendEmptyMessage(0);
						} else {
							handler.sendEmptyMessage(1);
						}
					} catch (Exception e) {
						// TODO: handle exception
						handler.sendEmptyMessage(2);
					}
				} else {
					handler.sendEmptyMessage(3);
				}
			}
		}).start();;
		
	}

	
	
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case 0:
				//Toast.makeText(IndividualCenterMainActivity.this, errorString, Toast.LENGTH_SHORT).show();
				
				break;
			case 1:
				//Toast.makeText(IndividualCenterMainActivity.this, errorString, Toast.LENGTH_SHORT).show();
			case 2:
				//Toast.makeText(IndividualCenterMainActivity.this, "数据传输异常", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				//Toast.makeText(IndividualCenterMainActivity.this, "联网失败，请重试", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			
			loadingProgressBar.setVisibility(View.INVISIBLE);
			finish();
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
