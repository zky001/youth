package com.handsSwjtu.serviceAndBroadcast;

import com.handsSwjtu.common.ContextUtil;
import com.handsSwjtu.common.DatabaseHelper;
import com.handsSwjtu.common.SharePreferenceHelper;
import com.handsSwjtu.common.Tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.widget.Toast;

public class SlientReceiver extends BroadcastReceiver {

	SharePreferenceHelper sharePreferenceHelper;
	AudioManager audioManager;
	DatabaseHelper databaseHelper;
	Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		int a = 1;
		sharePreferenceHelper = new SharePreferenceHelper();
		if (sharePreferenceHelper.getIsMuteWhenClass()) {
			sharePreferenceHelper = new SharePreferenceHelper();
			databaseHelper = new DatabaseHelper(context);
			audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
			int classNo = intent.getIntExtra("classNo", 0);
			int type = intent.getIntExtra("type", 1);

			String sql = "select name from SCHEDULE where dayTime=" + classNo + " and weekDay="
					+ (Tools.WeekDayNow(0) - 1);
			Cursor cursor = databaseHelper.dql(sql, new String[] {});
			if (cursor.moveToFirst()) {
				if (!cursor.getString(0).equals("null") && !cursor.getString(0).equals("")) {
					if (type == 2) {
						String sqlTmp = "select name from SCHEDULE where dayTime=" + (classNo + 1) + " and weekDay="
								+ (Tools.WeekDayNow(0) - 1);
						Cursor cursorTmp = databaseHelper.dql(sqlTmp, new String[] {});
						cursorTmp.moveToFirst();
						if (!cursorTmp.getString(0).equals("null") && !cursorTmp.getString(0).equals("")) {
							type = 2;
						} else {
							type = 1;
						}
					}

					switch (type) {
					case 0:
						setVibrate(context,audioManager);
						break;
					case 1:
						recovery(context,audioManager);
						break;
					case 2:
						break;
					default:
						break;
					}
				}
			}
		}

	}

	public void setVibrate(Context context,AudioManager audioManager) {
		sharePreferenceHelper.setRingerMode(audioManager.getRingerMode());
		sharePreferenceHelper.setVibrateTypeRinger(audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER));
		sharePreferenceHelper.setVibrateTypeNotification(audioManager
				.getVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION));
		audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
		audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);
		Toast.makeText(context, "课程表：已设为振动", Toast.LENGTH_SHORT).show();
	}

	public void recovery(Context context,AudioManager audioManager) {
		audioManager.setRingerMode(sharePreferenceHelper.getRingerMode());
		audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, sharePreferenceHelper.getVibrateTypeRinger());
		audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,
				sharePreferenceHelper.getVibrateTypeNotification());
		Toast.makeText(context, "课程表：已取消振动，回复原来的情景模式", Toast.LENGTH_SHORT).show();
	}

}
