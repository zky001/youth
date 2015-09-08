package com.example.handsswjtu;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.mobstat.StatService;
import com.baidu.push.example.Utils;
import com.handsSwjtu.common.BtnOnTouchListener;
import com.handsSwjtu.common.CommonOnTouchListener;
import com.handsSwjtu.common.SharePreferenceHelper;
import com.handsSwjtu.httpConnect.HttpConnect;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class IndividualCenterLoginActivity extends Activity {

	private EditText stuCodeEditText;
	private EditText passWordEditText;
	private RelativeLayout loginBtn;
	private ProgressBar loadingProgressBar;
	private SharePreferenceHelper sharePreferenceHelper;
	private String stuCode;
	private String passWord;
	private String message;
	private CheckBox checkBoxRememberPwd;
	private CheckBox checkBoxAutoLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_individual_center_login);
		sharePreferenceHelper = new SharePreferenceHelper();
		if (sharePreferenceHelper.isHaveLogged()) {
			Intent it=new Intent(IndividualCenterLoginActivity.this,IndividualCenterMainActivity.class);
			startActivity(it);
			finish();
		}

		stuCodeEditText = (EditText) findViewById(R.id.stuCode);
		stuCodeEditText.setText(sharePreferenceHelper.getLoginUsername());
		checkBoxAutoLogin=(CheckBox)findViewById(R.id.autoLogin);
		checkBoxRememberPwd=(CheckBox)findViewById(R.id.rememberPwd);
		checkBoxAutoLogin.setChecked(sharePreferenceHelper.getIsAutoLogin());
		String aString=sharePreferenceHelper.getLoginPassword();
		checkBoxRememberPwd.setChecked((sharePreferenceHelper.getLoginPassword()).equals("")?false:true);
		passWordEditText = (EditText) findViewById(R.id.password);
		passWordEditText.setText(sharePreferenceHelper.getLoginPassword());

		loginBtn = (RelativeLayout) findViewById(R.id.loginButton);
		loadingProgressBar = (ProgressBar) findViewById(R.id.loading);
		loadingProgressBar.setVisibility(View.INVISIBLE);
		if (sharePreferenceHelper.getIsAutoLogin()) {
			stuCode=sharePreferenceHelper.getLoginUsername();
			passWord=sharePreferenceHelper.getLoginPassword();
			login();
		}
		loginBtn.setOnTouchListener(new BtnOnTouchListener());
		loginBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stuCode = stuCodeEditText.getText().toString();
				passWord = passWordEditText.getText().toString();	
				login();

			}
		});
	}

	
	public void login() {
		loadingProgressBar.setVisibility(View.VISIBLE);

		
		login2SecondClass(stuCode, passWord, null);
		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//
//				String userId=sharePreferenceHelper.getUserId();
//				String channelId=sharePreferenceHelper.getChannelId();
//				HttpConnect.registerPush(userId, channelId,stuCode);
//				HttpConnect.setBaiduPush(sharePreferenceHelper.getPushSetting(), stuCode, userId,channelId);
//			}
//		}).start();
	}
	
	
	public void login2IndividualCenter(final String stuCode, final String passWord, final String validateCode) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// String resultString =
				// HttpConnect.login2IndividualCenter(stuCode, passWord,
				// validateCode);
				//String resultString = HttpConnect.login2SecondClassCenter(stuCode, passWord, validateCode);
				String resultString = HttpConnect.login2IndividualCenter(stuCode, passWord, validateCode);
				if (resultString != null) {
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(resultString);

						if (jsonObject.getInt("State") == 0) {
							handler2.sendEmptyMessage(1);
						} else {
							message = jsonObject.getString("Message");
							handler2.sendEmptyMessage(2);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						handler2.sendEmptyMessage(3);
					}
				} else {
					handler2.sendEmptyMessage(4);
				}
			}
		}).start();
	}

	public void login2SecondClass(final String stuCode, final String passWord, final String validateCode) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// String resultString =
				// HttpConnect.login2IndividualCenter(stuCode, passWord,
				// validateCode);
				String resultString = HttpConnect.login2SecondClassCenter(stuCode, passWord, validateCode);
				//String resultString = HttpConnect.login2IndividualCenter(stuCode, passWord, validateCode);
				if (resultString != null) {
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(resultString);

						if (jsonObject.getInt("State") == 0) {
							handler.sendEmptyMessage(1);
						} else {
							message = jsonObject.getString("Message");
							handler.sendEmptyMessage(2);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						handler.sendEmptyMessage(3);
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
			case 1:
//				Toast.makeText(IndividualCenterLoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
//				//Intent it = new Intent(IndividualCenterLoginActivity.this, IndividualCenterMainActivity.class);
//				Intent it = new Intent(IndividualCenterLoginActivity.this, SecondClassMainActivity.class);
				login2IndividualCenter(stuCode, passWord, null);
				
				break;
			case 2:
				Toast.makeText(IndividualCenterLoginActivity.this, message, Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(IndividualCenterLoginActivity.this, "服务器发生异常错误", Toast.LENGTH_SHORT).show();
				break;
			case 4:
				Toast.makeText(IndividualCenterLoginActivity.this, "网络连接失败，请重试", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			loadingProgressBar.setVisibility(View.INVISIBLE);
		}
	};

	
	Handler handler2 = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(IndividualCenterLoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
				//Intent it = new Intent(IndividualCenterLoginActivity.this, IndividualCenterMainActivity.class);
				Intent it = new Intent(IndividualCenterLoginActivity.this, SecondClassMainActivity.class);
				//startActivity(it);
				finish();
				sharePreferenceHelper.setHaveLogged(true);
				sharePreferenceHelper.setStuCode(stuCode);
				sharePreferenceHelper.setLoginUsername(stuCode);
				if (checkBoxAutoLogin.isChecked()) {
					sharePreferenceHelper.setIsAutoLogin(true);
					sharePreferenceHelper.setLoginPassword(passWord);
				}
				if (checkBoxRememberPwd.isChecked()) {
					sharePreferenceHelper.setLoginPassword(passWord);
				}
				PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
						Utils.getMetaValue(IndividualCenterLoginActivity.this, "api_key"));
				
				break;
			case 2:
				Toast.makeText(IndividualCenterLoginActivity.this, message, Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(IndividualCenterLoginActivity.this, "服务器发生异常错误", Toast.LENGTH_SHORT).show();
				break;
			case 4:
				Toast.makeText(IndividualCenterLoginActivity.this, "网络连接失败，请重试", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			loadingProgressBar.setVisibility(View.INVISIBLE);
		}
	};
	
	
	
	
	
	public void changeLoginSetting(View view) {
		if (!checkBoxRememberPwd.isChecked()) {
			sharePreferenceHelper.setLoginPassword("");
		}
		if (!checkBoxAutoLogin.isChecked()) {
			sharePreferenceHelper.setIsAutoLogin(false);
		}
		if (checkBoxAutoLogin.isChecked()) {
			checkBoxRememberPwd.setChecked(true);
			sharePreferenceHelper.setIsAutoLogin(false);
		}
	}
	
	
	public void finish(View v){
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
