package com.handsSwjtu.serviceAndBroadcast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.handsswjtu.R.color;
import com.handsSwjtu.common.ContextUtil;
import com.handsSwjtu.common.SharePreferenceHelper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartupReceiver extends BroadcastReceiver {

	SharePreferenceHelper sharePreferenceHelper;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		sharePreferenceHelper = new SharePreferenceHelper();
		AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
		if (sharePreferenceHelper.getIsMuteWhenClass()) {
			// List<Intent> its = new ArrayList<Intent>();
			// for (int i = 1; i <= 13; i++) {
			// Intent it = new Intent("com.handsSwjtu.CHANGE_RING_SETTING");
			// switch (i) {
			// case 1:
			// case 3:
			// case 6:
			// case 8:
			// case 10:
			// it.putExtra("type", 0);
			// break;
			//
			// default:
			// it.putExtra("type", 1);
			// break;
			// }
			//
			// switch (i) {
			// case 1:
			// case 2:
			// it.putExtra("classNo", 0);
			// break;
			// case 3:
			// case 4:
			// case 5:
			// it.putExtra("classNo", 1);
			// break;
			//
			// case 6:
			// case 7:
			// it.putExtra("classNo", 2);
			// break;
			//
			// case 8:
			// case 9:
			// case 10:
			// it.putExtra("classNo", 3);
			// break;
			// case 11:
			// case 12:
			// case 13:
			// it.putExtra("classNo", 4);
			// break;
			//
			// default:
			// break;
			// }
			// its.add(it);
			// }
			//
			// List<PendingIntent> pis=new
			// for (int i = 0; i < 13; i++) {
			//
			// }

			Intent it1 = new Intent("com.handsSwjtu.CHANGE_RING_SETTING");
			Intent it2 = new Intent("com.handsSwjtu.CHANGE_RING_SETTING");
			Intent it3 = new Intent("com.handsSwjtu.CHANGE_RING_SETTING");
			Intent it4 = new Intent("com.handsSwjtu.CHANGE_RING_SETTING");
			Intent it5 = new Intent("com.handsSwjtu.CHANGE_RING_SETTING");
			Intent it6 = new Intent("com.handsSwjtu.CHANGE_RING_SETTING");
			Intent it7 = new Intent("com.handsSwjtu.CHANGE_RING_SETTING");
			Intent it8 = new Intent("com.handsSwjtu.CHANGE_RING_SETTING");
			Intent it9 = new Intent("com.handsSwjtu.CHANGE_RING_SETTING");
			Intent it10 = new Intent("com.handsSwjtu.CHANGE_RING_SETTING");
			Intent it11 = new Intent("com.handsSwjtu.CHANGE_RING_SETTING");
			Intent it12 = new Intent("com.handsSwjtu.CHANGE_RING_SETTING");
			Intent it13 = new Intent("com.handsSwjtu.CHANGE_RING_SETTING");
			
			
			it1.putExtra("type", 0);
			it2.putExtra("type", 1);
			it3.putExtra("type", 0);
			it4.putExtra("type", 2);
			it5.putExtra("type", 1);
			it6.putExtra("type", 0);
			it7.putExtra("type", 1);
			it8.putExtra("type", 0);
			it9.putExtra("type", 2);
			it10.putExtra("type", 1);
			it11.putExtra("type", 0);
			it12.putExtra("type", 2);
			it13.putExtra("type", 1);

			it1.putExtra("classNo", 0);
			it2.putExtra("classNo", 1);
			it3.putExtra("classNo", 2);
			it4.putExtra("classNo", 3);
			it5.putExtra("classNo", 4);
			it6.putExtra("classNo", 5);
			it7.putExtra("classNo", 6);
			it8.putExtra("classNo", 7);
			it9.putExtra("classNo", 8);
			it10.putExtra("classNo",9);
			it11.putExtra("classNo", 10);
			it12.putExtra("classNo", 11);
			it13.putExtra("classNo", 12);

			PendingIntent pi1 = PendingIntent.getBroadcast(context, 0, it1, PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent pi2 = PendingIntent.getBroadcast(context, 1, it2, PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent pi3 = PendingIntent.getBroadcast(context, 2, it3, PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent pi4 = PendingIntent.getBroadcast(context, 3, it4, PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent pi5 = PendingIntent.getBroadcast(context, 4, it5, PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent pi6 = PendingIntent.getBroadcast(context, 5, it6, PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent pi7 = PendingIntent.getBroadcast(context, 6, it7, PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent pi8 = PendingIntent.getBroadcast(context, 7, it8, PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent pi9 = PendingIntent.getBroadcast(context, 8, it9, PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent pi10 = PendingIntent.getBroadcast(context, 9, it10, PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent pi11 = PendingIntent.getBroadcast(context, 10, it11, PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent pi12 = PendingIntent.getBroadcast(context, 11, it12, PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent pi13 = PendingIntent.getBroadcast(context, 12, it13, PendingIntent.FLAG_UPDATE_CURRENT);

			// PendingIntent pi1=PendingIntent.getService(context, 0, it1,
			// PendingIntent.FLAG_UPDATE_CURRENT);
			// PendingIntent pi2=PendingIntent.getService(context, 0, it2,
			// PendingIntent.FLAG_UPDATE_CURRENT);
			// PendingIntent pi3=PendingIntent.getService(context, 0, it3,
			// PendingIntent.FLAG_UPDATE_CURRENT);
			// PendingIntent pi4=PendingIntent.getService(context, 0, it4,
			// PendingIntent.FLAG_UPDATE_CURRENT);
			// PendingIntent pi5=PendingIntent.getService(context, 0, it5,
			// PendingIntent.FLAG_UPDATE_CURRENT);
			// PendingIntent pi6=PendingIntent.getService(context, 0, it6,
			// PendingIntent.FLAG_UPDATE_CURRENT);
			// PendingIntent pi7=PendingIntent.getService(context, 0, it7,
			// PendingIntent.FLAG_UPDATE_CURRENT);
			// PendingIntent pi8=PendingIntent.getService(context, 0, it8,
			// PendingIntent.FLAG_UPDATE_CURRENT);
			// PendingIntent pi9=PendingIntent.getService(context, 0, it9,
			// PendingIntent.FLAG_UPDATE_CURRENT);
			// PendingIntent pi10=PendingIntent.getService(context, 0, it10,
			// PendingIntent.FLAG_UPDATE_CURRENT);
			// PendingIntent pi11=PendingIntent.getService(context, 0, it11,
			// PendingIntent.FLAG_UPDATE_CURRENT);
			// PendingIntent pi12=PendingIntent.getService(context, 0, it12,
			// PendingIntent.FLAG_UPDATE_CURRENT);
			// PendingIntent pi13=PendingIntent.getService(context, 0, it13,
			// PendingIntent.FLAG_UPDATE_CURRENT);

			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();
			Calendar c3 = Calendar.getInstance();
			Calendar c4 = Calendar.getInstance();
			Calendar c5 = Calendar.getInstance();
			Calendar c6 = Calendar.getInstance();
			Calendar c7 = Calendar.getInstance();
			Calendar c8 = Calendar.getInstance();
			Calendar c9 = Calendar.getInstance();
			Calendar c10 = Calendar.getInstance();
			Calendar c11 = Calendar.getInstance();
			Calendar c12 = Calendar.getInstance();
			Calendar c13 = Calendar.getInstance();
			
			
			c1.set(Calendar.HOUR_OF_DAY, 8);
			c1.set(Calendar.MINUTE, 0);
			c2.set(Calendar.HOUR_OF_DAY, 9);
			c2.set(Calendar.MINUTE, 35);
			c3.set(Calendar.HOUR_OF_DAY, 9);
			c3.set(Calendar.MINUTE, 50);
			c4.set(Calendar.HOUR_OF_DAY, 11);
			c4.set(Calendar.MINUTE, 25);
			c5.set(Calendar.HOUR_OF_DAY, 12);
			c5.set(Calendar.MINUTE, 15);
			c6.set(Calendar.HOUR_OF_DAY, 14);
			c6.set(Calendar.MINUTE, 0);
			c7.set(Calendar.HOUR_OF_DAY, 15);
			c7.set(Calendar.MINUTE, 35);
			c8.set(Calendar.HOUR_OF_DAY, 15);
			c8.set(Calendar.MINUTE, 50);
			c9.set(Calendar.HOUR_OF_DAY, 17);
			c9.set(Calendar.MINUTE, 25);
			c10.set(Calendar.HOUR_OF_DAY, 18);
			c10.set(Calendar.MINUTE, 15);
			c11.set(Calendar.HOUR_OF_DAY, 19);
			c11.set(Calendar.MINUTE, 30);
			c12.set(Calendar.HOUR_OF_DAY, 21);
			c12.set(Calendar.MINUTE, 05);
			c13.set(Calendar.HOUR_OF_DAY, 21);
			c13.set(Calendar.MINUTE, 55);
			
			
			c1.set(Calendar.SECOND, 0);
			c2.set(Calendar.SECOND, 0);
			c3.set(Calendar.SECOND, 0);
			c4.set(Calendar.SECOND, 0);
			c5.set(Calendar.SECOND, 0);
			c6.set(Calendar.SECOND, 0);
			c7.set(Calendar.SECOND, 0);
			c8.set(Calendar.SECOND, 0);
			c9.set(Calendar.SECOND, 0);
			c10.set(Calendar.SECOND, 0);
			c11.set(Calendar.SECOND, 0);
			c12.set(Calendar.SECOND, 0);
			c13.set(Calendar.SECOND, 0);
			
			
			
			
			am.setRepeating(AlarmManager.RTC_WAKEUP, c1.getTimeInMillis(), 24 * 3600 * 1000, pi1);
			am.setRepeating(AlarmManager.RTC_WAKEUP, c2.getTimeInMillis(), 24 * 3600 * 1000, pi2);
			am.setRepeating(AlarmManager.RTC_WAKEUP, c3.getTimeInMillis(), 24 * 3600 * 1000, pi3);
			am.setRepeating(AlarmManager.RTC_WAKEUP, c4.getTimeInMillis(), 24 * 3600 * 1000, pi4);
			am.setRepeating(AlarmManager.RTC_WAKEUP, c5.getTimeInMillis(), 24 * 3600 * 1000, pi5);
			am.setRepeating(AlarmManager.RTC_WAKEUP, c6.getTimeInMillis(), 24 * 3600 * 1000, pi6);
			am.setRepeating(AlarmManager.RTC_WAKEUP, c7.getTimeInMillis(), 24 * 3600 * 1000, pi7);
			am.setRepeating(AlarmManager.RTC_WAKEUP, c8.getTimeInMillis(), 24 * 3600 * 1000, pi8);
			am.setRepeating(AlarmManager.RTC_WAKEUP, c9.getTimeInMillis(), 24 * 3600 * 1000, pi9);
			am.setRepeating(AlarmManager.RTC_WAKEUP, c10.getTimeInMillis(), 24 * 3600 * 1000, pi10);
			am.setRepeating(AlarmManager.RTC_WAKEUP, c11.getTimeInMillis(), 24 * 3600 * 1000, pi11);
			am.setRepeating(AlarmManager.RTC_WAKEUP, c12.getTimeInMillis(), 24 * 3600 * 1000, pi12);
			am.setRepeating(AlarmManager.RTC_WAKEUP, c13.getTimeInMillis(), 24 * 3600 * 1000, pi13);

		}
	}

}
