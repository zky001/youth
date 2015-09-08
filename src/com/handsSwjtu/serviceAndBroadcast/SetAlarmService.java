package com.handsSwjtu.serviceAndBroadcast;

import com.handsSwjtu.common.ContextUtil;
import com.handsSwjtu.common.DatabaseHelper;
import com.handsSwjtu.common.SharePreferenceHelper;
import com.handsSwjtu.common.Tools;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.IBinder;

public class SetAlarmService extends Service {

	private SharePreferenceHelper sharePreferenceHelper;
	private DatabaseHelper databaseHelper;
	private AudioManager audioManager;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		int a=1;
		if (sharePreferenceHelper.getIsMuteWhenClass()) {
			sharePreferenceHelper = new SharePreferenceHelper();
			databaseHelper = new DatabaseHelper(getApplicationContext());
			audioManager = (AudioManager) getApplication().getSystemService(AUDIO_SERVICE);
			int classNo = intent.getIntExtra("classNo", 0);
			int type = intent.getIntExtra("type", 1);
			String sql = "select name from SCHEDULE where dayTime=" + classNo + " and weekDay=" + Tools.WeekDayNow(0);
			Cursor cursor = databaseHelper.dql(sql, new String[] {});
			if (cursor.moveToFirst()) {
				if (!cursor.getString(0).equals("null") && !cursor.getString(0).equals("")) {

					switch (type) {
					case 0:
						setVibrate();
						break;
					case 1:
						recovery();
						break;
					default:
						break;
					}
				}
			}
		}
		stopSelf();
		return null;
	}

	public void setVibrate() {
		audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
		audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);
		sharePreferenceHelper.setRingerMode(audioManager.getRingerMode());
		sharePreferenceHelper.setVibrateTypeRinger(audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER));
		sharePreferenceHelper.setVibrateTypeNotification(audioManager
				.getVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION));
	}

	public void recovery() {
		audioManager.setRingerMode(sharePreferenceHelper.getRingerMode());
		audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, sharePreferenceHelper.getVibrateTypeRinger());
		audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,
				sharePreferenceHelper.getVibrateTypeNotification());
	}

}
