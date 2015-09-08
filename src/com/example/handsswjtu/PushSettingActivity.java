package com.example.handsswjtu;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.baidu.mobstat.StatService;
import com.handsSwjtu.common.BtnOnTouchListener;
import com.handsSwjtu.common.SharePreferenceHelper;
import com.handsSwjtu.httpConnect.HttpConnect;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class PushSettingActivity extends Activity {

	private LinearLayout commitBtn;
	private JSONArray jsonArray;
	private String pushSetting;
	private SharePreferenceHelper sharePreferenceHelper;
	private String errorString;
	private ProgressBar loadingProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_push_setting);
		loadingProgressBar=(ProgressBar)findViewById(R.id.loading);
		sharePreferenceHelper = new SharePreferenceHelper();
		try {
			jsonArray = new JSONArray(sharePreferenceHelper.getPushSetting());
			for (int i = 0; i < jsonArray.length(); i++) {
				switch (jsonArray.optInt(i)) {
				case 1:
					((CheckBox) findViewById(R.id.checkBoxDean)).setChecked(true);
					;
					break;
				case 2:
					((CheckBox) findViewById(R.id.checkBoxYangHua)).setChecked(true);
					;
					break;
				case 3:
					((CheckBox) findViewById(R.id.checkBoxSwjtuNews)).setChecked(true);
					;
					break;
				case 4:
					((CheckBox) findViewById(R.id.checkBoxYouth)).setChecked(true);
					;
					break;
				case 21:
					((CheckBox) findViewById(R.id.checkBoxYangHuaCollege)).setChecked(true);
					;
				case 5:((CheckBox) findViewById(R.id.checkBoxEmploy)).setChecked(true);
					break;
				default:
					
					break;
					
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			((CheckBox) findViewById(R.id.checkBoxDean)).setChecked(true);
			((CheckBox) findViewById(R.id.checkBoxYangHua)).setChecked(true);
			((CheckBox) findViewById(R.id.checkBoxSwjtuNews)).setChecked(true);
			((CheckBox) findViewById(R.id.checkBoxYouth)).setChecked(true);
		}

		commitBtn = (LinearLayout) findViewById(R.id.commitBtn);
		commitBtn.setOnTouchListener(new BtnOnTouchListener());
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				jsonArray = new JSONArray();
				if (((CheckBox) findViewById(R.id.checkBoxSwjtuNews)).isChecked()) {
					jsonArray.put(3);

				}
				if (((CheckBox) findViewById(R.id.checkBoxYouth)).isChecked()) {
					jsonArray.put(4);

				}
				if (((CheckBox) findViewById(R.id.checkBoxYangHua)).isChecked()) {
					jsonArray.put(2);

				}
				if (((CheckBox) findViewById(R.id.checkBoxYangHuaCollege)).isChecked()) {
					jsonArray.put(21);

				}
				if (((CheckBox) findViewById(R.id.checkBoxDean)).isChecked()) {
					jsonArray.put(1);

				}
				if (((CheckBox) findViewById(R.id.checkBoxEmploy)).isChecked()) {
					jsonArray.put(5);

				}
				pushSetting = jsonArray.toString();

				setBaiduPushSetting(pushSetting);
			}
		});

	}

	public void setBaiduPushSetting(final String pushSetting) {
		loadingProgressBar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String userNow=sharePreferenceHelper.getStuCode();
				String pushChannelId=sharePreferenceHelper.getChannelId();
				String pushUserId=sharePreferenceHelper.getUserId();
				String resultString = HttpConnect.setBaiduPush(pushSetting, userNow,pushUserId, pushChannelId);
				if (resultString != null) {
					try {
						JSONObject jsonObject = new JSONObject(resultString);
						errorString = jsonObject.getString("errorMsg");
						if (jsonObject.getInt("state") == 0) {
							handler.sendEmptyMessage(0);
						} else {
							handler.sendEmptyMessage(1);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						handler.sendEmptyMessage(2);
					}

				} else {
					handler.sendEmptyMessage(4);
				}
			}
			
		}).start();
	}

Handler handler = new Handler() {
	public void handleMessage(Message msg) {

		switch (msg.what) {

		case 0:
			Toast.makeText(PushSettingActivity.this, errorString, Toast.LENGTH_SHORT).show();
			sharePreferenceHelper.setPushSetting(pushSetting);
			break;
		case 1:
			Toast.makeText(PushSettingActivity.this, errorString, Toast.LENGTH_SHORT).show();
			break;
		case 2:
			Toast.makeText(PushSettingActivity.this, "数据传输异常", Toast.LENGTH_SHORT).show();
			break;
		case 3:
			Toast.makeText(PushSettingActivity.this, "联网失败，请重试", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
		loadingProgressBar.setVisibility(View.INVISIBLE);
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
