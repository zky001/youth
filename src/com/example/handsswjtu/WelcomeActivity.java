package com.example.handsswjtu;

import java.util.Calendar;

import org.json.JSONObject;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.mobstat.StatService;
import com.baidu.push.example.Utils;
import com.example.handsswjtu.util.SystemUiHider;
import com.handsSwjtu.common.CommonOnTouchListener;
import com.handsSwjtu.common.ContextUtil;
import com.handsSwjtu.common.DatabaseHelper;
import com.handsSwjtu.common.SharePreferenceHelper;
import com.handsSwjtu.common.Tools;
import com.handsSwjtu.httpConnect.HttpConnect;
import com.handsSwjtu.serviceAndBroadcast.SetAlarmService;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class WelcomeActivity extends Activity {

	SharePreferenceHelper sharePreferenceHelper;
	AudioManager audioManager ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		



		
		
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_welcome);
		PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
				Utils.getMetaValue(WelcomeActivity.this, "api_key"));
		String pkgName = this.getPackageName();
		Resources resource = this.getResources();
		CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(resource.getIdentifier(
				"notification_custom_builder", "layout", pkgName), resource.getIdentifier("notification_icon", "id",
				pkgName), resource.getIdentifier("notification_title", "id", pkgName), resource.getIdentifier(
				"notification_text", "id", pkgName));
		cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
		cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
		cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
		cBuilder.setLayoutDrawable(resource.getIdentifier("simple_notification_icon", "drawable", pkgName));
		sharePreferenceHelper = new SharePreferenceHelper();
		sharePreferenceHelper.setHaveLogged(false);
		// sharePreferenceHelper.setStuCode(null);
		PushManager.setNotificationBuilder(this, 1, cBuilder);
		PushManager.enableLbs(this);

		try {
			if (!(sharePreferenceHelper.getUpdateInfo().equals("null"))) {
				JSONObject jsonObject = new JSONObject(sharePreferenceHelper.getUpdateInfo());
				String newestVersion = jsonObject.getString("version");
				String updateLog = jsonObject.getString("changeLog");
				boolean isMustUpdate = jsonObject.getBoolean("isMustUpdate");
				
				final String updateUrl = jsonObject.getString("updateUrl");
				if (newestVersion.equals(ContextUtil.VERSION_NOW)) {
					sharePreferenceHelper.setUpdateInfo("null");
					justWaitASecond();
				} else {
					if (!isMustUpdate) {
						Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
						NotificationManager nm = (NotificationManager) ContextUtil.getInstance().getSystemService(
								Context.NOTIFICATION_SERVICE);
						Notification n = new Notification(R.drawable.simple_notification_icon, "有新版本可用",
								System.currentTimeMillis());
						// Intent it=new Intent()
						n.flags = Notification.FLAG_AUTO_CANCEL;
						// n.defaults = Notification.DEFAULT_ALL;
						PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, it, 0);
						n.setLatestEventInfo(getApplicationContext(), "有新版本可用", "最新版本:" + newestVersion + "当前版本:"
								+ ContextUtil.VERSION_NOW, pendingIntent);
						nm.notify(R.drawable.simple_notification_icon, n);
						justWaitASecond();
					} else {
						LinearLayout confirmLayout = (LinearLayout) getLayoutInflater().inflate(
								R.layout.my_message_box, null);
						
						Dialog alertDialog = new Dialog(WelcomeActivity.this, R.style.dialog);
						alertDialog.setContentView(confirmLayout);

						TextView messageBoxContent = ((TextView) confirmLayout.findViewById(R.id.messageBoxContent));
						messageBoxContent.setText(updateLog);
						final RelativeLayout positiveButton = (RelativeLayout) confirmLayout
								.findViewById(R.id.positiveButton);
						RelativeLayout negativeButton = (RelativeLayout) confirmLayout
								.findViewById(R.id.negativeButton);
						positiveButton.setOnTouchListener(new CommonOnTouchListener());
						negativeButton.setOnTouchListener(new CommonOnTouchListener());
						positiveButton.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
								startActivity(it);
								finish();
							}
						});
						negativeButton.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								finish();
							}
						});
						alertDialog.show();
					}
				}
			} else {
				justWaitASecond();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				checkUpdate();
			}
		}).start();
	}




	public void justWaitASecond() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(2000);
					handler.sendEmptyMessage(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();

	}

	public void checkUpdate() {
		String resultString = HttpConnect.checkUpdate();
		if (resultString != null) {
			try {
				JSONObject jsonObject = new JSONObject(resultString);
				if (jsonObject.getInt("state") == 0) {
					sharePreferenceHelper.setUpdateInfo(resultString);
				} else {
					sharePreferenceHelper.setUpdateInfo("null");
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			Intent it = new Intent(WelcomeActivity.this, MainActivity.class);
			startActivity(it);
			finish();
		}
	};

	protected void onRestart() {
		finish();
		super.onRestart();
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
