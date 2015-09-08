package com.example.handsswjtu;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.baidu.mobstat.StatService;
import com.handsSwjtu.common.ContextUtil;
import com.handsSwjtu.common.SharePreferenceHelper;
import com.handsSwjtu.httpConnect.HttpConnect;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class PPPoeActivity extends Activity {

	private RelativeLayout loginBtn;
	private ProgressBar loadProgressBar;
	private EditText userNameEditText;
	private EditText passWordEditText;
	private String newWorkTest;
	private int retryTimeNow = 0;
	private String aString;
	private boolean haveConnected = false;
	private String systemDataPath;
	private List<String> wlanName;
	private CheckBox checkBoxRememberPwd;
	private CheckBox checkBoxAutoLogin;
	private SharePreferenceHelper sharePreferenceHelper;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_pppoe);
		sharePreferenceHelper=new SharePreferenceHelper();
		systemDataPath=this.getFilesDir().getPath();
		if (!(new File(systemDataPath)).exists()) {
			(new File(systemDataPath)).mkdirs();
		}
		checkBoxAutoLogin=(CheckBox)findViewById(R.id.autoLogin);
		checkBoxRememberPwd=(CheckBox)findViewById(R.id.rememberPwd);
		checkBoxAutoLogin.setChecked(sharePreferenceHelper.getIsPPPoeAutoLogin());
		checkBoxRememberPwd.setChecked((sharePreferenceHelper.getPPPoeLoginPassword()).equals("")?false:true);
		loginBtn = (RelativeLayout) findViewById(R.id.loginButton);
		loadProgressBar = (ProgressBar) this.findViewById(R.id.loading);
		userNameEditText = (EditText) findViewById(R.id.username);
		passWordEditText = (EditText) findViewById(R.id.password);
		userNameEditText.setText(sharePreferenceHelper.getPPPoeLoginUsername());
		passWordEditText.setText(sharePreferenceHelper.getPPPoeLoginPassword());
		loadProgressBar.setVisibility(View.INVISIBLE);
		loginBtn.setOnClickListener(loginOnClickListener);
		moveFileToSystem();
		// DataOutputStream localoDataOutputStream=new
		// DataOutputStream(localProcess.getOutputStream());
	}

	public void moveFileToSystem() {
		try {

			Process suProcess = Runtime.getRuntime().exec("su");
			DataOutputStream suDataOutputStream = new DataOutputStream(suProcess.getOutputStream());
			suDataOutputStream.writeBytes("mount -o remount rw system\n");
			suDataOutputStream.flush();
			File filePppoe = new File(systemDataPath,"pppoe");
			if (!filePppoe.exists()) {
				InputStream inputStream = this.getResources().openRawResource(R.raw.pppoe);

				byte[] bytes = new byte[inputStream.available()];
				new DataInputStream(inputStream).readFully(bytes);
				FileOutputStream fileOutputStream = new FileOutputStream(systemDataPath+"/pppoe");
				fileOutputStream.write(bytes);
				fileOutputStream.close();

			}
			suDataOutputStream.writeBytes("chmod 755 "+systemDataPath+"/pppoe\n");
			suDataOutputStream.flush();
			File fileWifiPppoe = new File(systemDataPath,"wifipppoe");
			if (!fileWifiPppoe.exists()) {
				InputStream inputStream = this.getResources().openRawResource(R.raw.wifipppoe);

				byte[] bytes = new byte[inputStream.available()];
				new DataInputStream(inputStream).readFully(bytes);
				FileOutputStream fileOutputStream = new FileOutputStream(systemDataPath+"/wifipppoe");
				fileOutputStream.write(bytes);
				fileOutputStream.close();

			}

			suDataOutputStream.writeBytes("chmod 755 "+systemDataPath+"/wifipppoe\n");
			suDataOutputStream.flush();
			suDataOutputStream.writeBytes("dd if="+systemDataPath+"/pppoe of=/system/bin/pppoe\n");
			suDataOutputStream.flush();
			suDataOutputStream.writeBytes("chmod 755 /system/bin/pppoe\n");
			suDataOutputStream.flush();
			suDataOutputStream
					.writeBytes("dd if="+systemDataPath+"/wifipppoe of=/system/bin/wifipppoe\n");
			suDataOutputStream.flush();
			suDataOutputStream.writeBytes("chmod 755 /system/bin/wifipppoe\n");
			suDataOutputStream.flush();
			suDataOutputStream.writeBytes("exit\n");
			suDataOutputStream.flush();
			InputStream inputStream = suProcess.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line = "";
			StringBuilder stringBuilder = new StringBuilder(line);
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append("\n");

			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.i("wrong", e.toString());
		}

	}

	OnClickListener loginOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			loadProgressBar.setVisibility(View.VISIBLE);
			wlanName=getWlanName();
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						for (int i = wlanName.size()-1; i < wlanName.size(); i++) {
							
						
						for (retryTimeNow = 0; retryTimeNow < 3; retryTimeNow++) {

							String userName = userNameEditText.getText().toString();
							String passWord = passWordEditText.getText().toString();
							String strcommand = "/system/bin/wifipppoe " +wlanName.get(i)+" "+userName + " " + passWord + "\n";
							// String
							// strcommand="su -c \"wifipppoe "+userName+" "+passWord+"\"\n";
							// Process
							// localpProcess=Runtime.getRuntime().exec("su -c wifipppoe bk20112370@cer 057987816609");
							Process localpProcess = Runtime.getRuntime().exec("su");
							DataOutputStream dataOutputStream = new DataOutputStream(localpProcess.getOutputStream());
							dataOutputStream.writeBytes(strcommand);
							dataOutputStream.flush();
							dataOutputStream.writeBytes("exit\n");
							dataOutputStream.flush();
							// localpProcess.waitFor();
							// Process
							// localpProcess=Runtime.getRuntime().exec("su -c wifipppoe");
							// DataOutputStream localDataOutputStream=new
							// DataOutputStream(localProcess.getOutputStream());
							InputStream inputStream = localpProcess.getInputStream();
							InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
							BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
							String line = "";
							StringBuilder stringBuilder = new StringBuilder(line);
							while ((line = bufferedReader.readLine()) != null) {
								stringBuilder.append(line);
								stringBuilder.append("\n");

							}
							aString = stringBuilder.toString();
							// userNameEditText.setText(stringBuilder.toString());
						}
						}
						newWorkTest = HttpConnect.netWorkTest();
						if (newWorkTest != null) {
							handler.sendEmptyMessage(1);
							haveConnected = true;
							Thread.currentThread().interrupt();

						} else {
							handler.sendEmptyMessage(2);
						}

						// localDataOutputStream.writeBytes("cd system\n");
						// localDataOutputStream.writeBytes("	");
						// localDataOutputStream.writeBytes("pppd pty \"pppoe -I wlan0 -T 80 -U -m 1412\" user \"bk20112370@cer\" password \"057987816609\" defaultroute mtu 1492 mru 1492");
						// localDataOutputStream.writeBytes("npppd pty \"pppoe -I wlan0 -T 80 -U -m 1412\" user \""+userName+"\" "+passWord+" \"PASSWORD\" defaultroute mtu 1492 mru 1492\n");
						// localDataOutputStream.writeBytes("setprop net.rmnet0.dns1 8.8.8.8\n");
						// localDataOutputStream.writeBytes("setprop net.rmnet0.dns2 208.67.220.220\n");
						// localDataOutputStream.writeBytes("setprop net.dns1 8.8.8.8\n");
						// localDataOutputStream.writeBytes("setprop net.dns2 208.67.220.220\n");
						// localDataOutputStream.writeBytes("ip route del default\n");
						// localDataOutputStream.writeBytes("ip route add default dev ppp0\n");
						// localDataOutputStream.flush();
						// localProcess.waitFor();
						// Runtime.getRuntime().exec("ip route del default\n");
						// Runtime.getRuntime().exec("ip route del default\n");
						// Runtime.getRuntime().exec("ip route add default dev ppp0\n");

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// TODO Auto-generated method stub
					if (!haveConnected) {
						handler.sendEmptyMessage(3);
					}

				}
			}).start();

		}
	};

	
	public List<String> getWlanName() {
		Process suProcess;
		List<String> wlanNameTemp=new ArrayList<String>();
		try {
			suProcess = Runtime.getRuntime().exec("su");
			DataOutputStream suDataOutputStream = new DataOutputStream(suProcess.getOutputStream());
			suDataOutputStream.writeBytes("netcfg\n");
			suDataOutputStream.flush();
			suDataOutputStream.writeBytes("exit\n");
			suDataOutputStream.flush();
			InputStream inputStream = suProcess.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line = "";
			StringBuilder stringBuilder = new StringBuilder(line);
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(" ");

			}
			String[] possibleWlanName=stringBuilder.toString().split("\\s+");
			
			for (int i = 0; i < possibleWlanName.length/5; i++) {
				if (possibleWlanName[5*i+1].equals("UP")||possibleWlanName[5*i+1].equals("up")||possibleWlanName[5*i+1].equals("Up")) {
					wlanNameTemp.add(possibleWlanName[5*i]);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return wlanNameTemp;

		
	}
	
	
	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(PPPoeActivity.this, "联网成功，欢迎使用", Toast.LENGTH_SHORT).show();
				loadProgressBar.setVisibility(View.INVISIBLE);
				sharePreferenceHelper.setPPPoeLoginUsername(userNameEditText.getText().toString());
				if (checkBoxAutoLogin.isChecked()) {
					sharePreferenceHelper.setIsPPPoeAutoLogin(true);
					sharePreferenceHelper.setPPPoeLoginPassword(passWordEditText.getText().toString());
				}
				if (checkBoxRememberPwd.isChecked()) {
					sharePreferenceHelper.setPPPoeLoginPassword(passWordEditText.getText().toString());
				}
				break;
			case 2:
				//Toast.makeText(PPPoeActivity.this, "联网失败，正在重试第" + retryTimeNow + "次", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(PPPoeActivity.this, "联网失败，请检查用户名密码是否正确，是否已经获取root权限，WiFi连接是否正确", Toast.LENGTH_SHORT)
						.show();
				loadProgressBar.setVisibility(View.INVISIBLE);
				break;
			default:
				break;
			}
			// userNameEditText.setText(aString);
		}
	};

	public void dialer() {

	}

	
	public void changeLoginSetting(View view) {
		if (!checkBoxRememberPwd.isChecked()) {
			sharePreferenceHelper.setPPPoeLoginPassword("");
		}
		if (!checkBoxAutoLogin.isChecked()) {
			sharePreferenceHelper.setIsPPPoeAutoLogin(false);
		}
		if (checkBoxAutoLogin.isChecked()) {
			checkBoxRememberPwd.setChecked(true);
			sharePreferenceHelper.setIsPPPoeAutoLogin(false);
		}
	}
	
	public void finishActivity(View view) {
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
