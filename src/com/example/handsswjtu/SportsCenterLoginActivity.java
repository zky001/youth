package com.example.handsswjtu;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.handsSwjtu.common.CommonOnTouchListener;
import com.handsSwjtu.common.SharePreferenceHelper;
import com.handsSwjtu.common.Tools;
import com.handsSwjtu.httpConnect.HttpConnect;

public class SportsCenterLoginActivity extends Activity {

	private ProgressBar loadingProgressBar;
	private ImageView ImageViewValidateCode;
	private RelativeLayout ImageButtonLoginSubmit;
	private EditText EditTextUsername;
	private EditText EditTextPassword;
	private EditText EditTextValidateCode;
	private String userName;
	private String passWord;
	private CheckBox checkBoxRememberPwd;
	private String validateCode;
	private SharePreferenceHelper sharePreferenceHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sports_center_login);
		sharePreferenceHelper=new SharePreferenceHelper();
		loadingProgressBar = (ProgressBar) findViewById(R.id.scoreExcuteWait);
		EditTextUsername = (EditText) findViewById(R.id.stuCode);
		EditTextPassword = (EditText) findViewById(R.id.password);
		EditTextUsername.setText(sharePreferenceHelper.getSportLoginUsername());
		EditTextPassword.setText(sharePreferenceHelper.getSportLoginPassword());
		EditTextValidateCode = (EditText) findViewById(R.id.editValidateCode);
		ImageViewValidateCode = (ImageView) findViewById(R.id.imageViewValidateCode);
		ImageButtonLoginSubmit = (RelativeLayout) findViewById(R.id.loginButton);
		ImageViewValidateCode.setOnClickListener(sportsCenterLoginListener);
		ImageButtonLoginSubmit.setOnClickListener(sportsCenterLoginListener);
		checkBoxRememberPwd=(CheckBox)findViewById(R.id.rememberPwd);
		checkBoxRememberPwd.setChecked((sharePreferenceHelper.getSportLoginPassword()).equals("")?false:true);
		//HttpConnect.isWap(this);
		new Thread(loadVerlidateCode).start();
	}

	View.OnClickListener sportsCenterLoginListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.imageViewValidateCode: {
				new Thread(loadVerlidateCode).start();

			}
				break;
			case R.id.loginButton: {
				loadingProgressBar.setVisibility(View.VISIBLE); 
				Intent it = new Intent(SportsCenterLoginActivity.this, SportTimeResultActivity.class);
				Bundle bundle = new Bundle();
				userName = EditTextUsername.getText().toString();
				passWord = EditTextPassword.getText().toString();
				validateCode = EditTextValidateCode.getText().toString();
				new Thread(login2SportsCenter).start();
			}
				break;

			}
		}
	};

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				String result = msg.getData().getString("result");
				ImageViewValidateCode.setImageBitmap(Tools.String2Bitmap(result));
				loadingProgressBar.setVisibility(View.INVISIBLE);
				break;
			case 2:
				LinearLayout confirmLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.my_message_box, null);
				final Dialog alertDialog = new Dialog(SportsCenterLoginActivity.this, R.style.dialog);
				alertDialog.setContentView(confirmLayout);
				alertDialog.show();
				TextView messageBoxContent = ((TextView) confirmLayout.findViewById(R.id.messageBoxContent));
				messageBoxContent.setText("获取验证码失败，是否重试？");
				final RelativeLayout positiveButton = (RelativeLayout) confirmLayout.findViewById(R.id.positiveButton);
				RelativeLayout negativeButton = (RelativeLayout) confirmLayout.findViewById(R.id.negativeButton);
				positiveButton.setOnTouchListener(new CommonOnTouchListener());
				negativeButton.setOnTouchListener(new CommonOnTouchListener());
				positiveButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						new Thread(loadVerlidateCode).start();
						alertDialog.dismiss();
					}
				});
				negativeButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						loadingProgressBar.setVisibility(View.INVISIBLE);
						alertDialog.dismiss();
					}
				});
				break;
			case 3:
				Toast.makeText(SportsCenterLoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
				Intent it = new Intent(SportsCenterLoginActivity.this, SportTimeResultActivity.class);
				if (checkBoxRememberPwd.isChecked()) {
					sharePreferenceHelper.setSportLoginUsername(EditTextUsername.getText().toString());
					sharePreferenceHelper.setSportLoginPassword(EditTextPassword.getText().toString());
				}
				startActivity(it);
				finish();
				loadingProgressBar.setVisibility(View.INVISIBLE);
				break;
				
			case 4:
				Toast.makeText(SportsCenterLoginActivity.this, "登录失败，请检查网络连接及用户名密码是否正确", Toast.LENGTH_SHORT).show();
				loadingProgressBar.setVisibility(View.INVISIBLE);
				new Thread(loadVerlidateCode).start();
				break;
			default:
				break;
			}
			
		}
	};

	Runnable loadVerlidateCode = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String result = HttpConnect.getValidateCode();
			// TextViewResult.setText(result);

			Message msg = new Message();
			Bundle bundle = new Bundle();
			if (result != null) {
				bundle.putString("result", result);
				msg.setData(bundle);
				msg.what = 1;
				handler.sendMessage(msg);
			} else {
				handler.sendEmptyMessage(2);
			}
		}

	};

	Runnable login2SportsCenter = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String result = HttpConnect.Login2SportsCenter(userName, passWord, validateCode);
			Message msg = new Message();
			if (result.equals("succeed")) {
				msg.what = 3;
				handler.sendMessage(msg);
			} else {
				msg.what = 4;
				handler.sendMessage(msg);
			}
		}
	};
	
	
	public void changeLoginSetting(View view) {
		if (!checkBoxRememberPwd.isChecked()) {
			sharePreferenceHelper.setSportLoginPassword("");
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
