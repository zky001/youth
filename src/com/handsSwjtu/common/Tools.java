package com.handsSwjtu.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream.GetField;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.client.entity.UrlEncodedFormEntity;

import com.handsSwjtu.httpConnect.HttpConnect;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;

public class Tools {

	public static Bitmap String2Bitmap(String string) {
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(string, Base64.DEFAULT);
			// bitmapArray=
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	public static String Bitmap2String(Bitmap bitmap) {
		String string = null;
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 60, bStream);
		byte[] bytes = bStream.toByteArray();
		string = Base64.encodeToString(bytes, Base64.DEFAULT);
		return string;

	}

	public static String iconBitmap2String(Bitmap bitmap) {
		String string = null;
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 90, bStream);
		byte[] bytes = bStream.toByteArray();
		string = Base64.encodeToString(bytes, Base64.DEFAULT);
		return string;

	}

	/**
	 * 判断当前网络模式是否为CMWAP
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isCmwapNet(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netWrokInfo = manager.getActiveNetworkInfo();
		if (netWrokInfo == null || !netWrokInfo.isAvailable()) {
			return false;
		} else if (netWrokInfo.getTypeName().equals("mobile") && netWrokInfo.getExtraInfo().equals("cmwap")) {
			return true;
		}
		return false;
	}

	public static Bitmap loadBookImg(String bookImg) {
		Bitmap bitmap = null;
		try {
			byte[] base64Img = Base64.decode(bookImg, Base64.DEFAULT);
			String url = new String(base64Img);
			URL bookImgUrl = new URL(url);
			bitmap = BitmapFactory.decodeStream(bookImgUrl.openStream());
		} catch (Exception e) {
			e.printStackTrace();

		}

		return bitmap;
	}

	public static int WeekDayNow(int dayOffset) {

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, dayOffset);
		int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
		switch (weekDay) {
		case 1:
			return 7;
		default:
			return weekDay - 1;

		}

	}

	public static int weekNoNow(int dayOffset) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendarNow = Calendar.getInstance();
		calendarNow.add(Calendar.DATE, dayOffset);
		Calendar calendarStart = Calendar.getInstance();
		try {
			calendarStart.setTime(sdf.parse("2014-2-24"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long timeOne = calendarNow.getTimeInMillis();
		long timeTwo = calendarStart.getTimeInMillis();
		// return Math.ceil((timeOne-timeTwo)/(1000*60*60*24*7));
		// return (int) Math.ceil(6.2);
		return (int) Math.ceil((double) (timeOne - timeTwo) / (double) (1000 * 60 * 60 * 24 * 7));

	}

	public static String getTimeNow() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		// Calendar calendarNow=Calendar.getInstance();
		return sdf.format(now);
	}

	public static int classTimeNow() {

		Calendar calendar = Calendar.getInstance();
		int hours = calendar.get(Calendar.HOUR_OF_DAY);

		if (hours < 8 || hours > 22) {
			return -1;
		} else if (hours < 10) {
			return 0;
		} else if (hours < 12) {
			return 1;
		} else if (hours < 16) {
			return 2;
		} else if (hours < 18) {
			return 3;
		} else if (hours < 22) {
			return 4;
		} else
			return -1;
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static Bitmap getHttpBitmap(String sourceUrl) {

		try {
			URL url = new URL(sourceUrl);
			Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
			return bitmap;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static boolean writeImg2SdCard(Bitmap bitmap, String filePath, String fileNameString) {
		File fileTest = new File(Environment.getExternalStorageDirectory().toString() + filePath);
		if (!fileTest.exists()) {
			fileTest.mkdirs();
		}
		try {
			File file = new File(Environment.getExternalStorageDirectory().toString() + filePath, fileNameString);
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	public static boolean writeHttpFile2SdCard(String url, String filePath, String fileNameString) {
		File fileTest = new File(Environment.getExternalStorageDirectory().toString() + filePath);
		if (!fileTest.exists()) {
			fileTest.mkdirs();
		}
		try {
			InputStream is = HttpConnect.getHttpFile(url);

			if (is != null) {
				File file = new File(Environment.getExternalStorageDirectory().toString() + filePath, fileNameString);
				FileOutputStream out = new FileOutputStream(file);
				byte buffer[] = new byte[8192];
				int count = 0;
				while ((count = is.read(buffer)) >=0) {
					out.write(buffer, 0, count);
				}
				//out.write(buffer);
				out.flush();
				out.close();
				return true;
			}else {
				return false;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	public static String getMsgTime(String timeString) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendarNow = Calendar.getInstance();
		Calendar calendarMsgTime = Calendar.getInstance();
		try {
			calendarMsgTime.setTime(sdf.parse(timeString));
			if ((calendarNow.get(Calendar.YEAR) - calendarMsgTime.get(Calendar.YEAR)) > 0) {

				return String.valueOf(calendarMsgTime.get(Calendar.YEAR)) + "-" + String.valueOf(calendarMsgTime.get(Calendar.MONTH)+1) + "-"
						+ String.valueOf(calendarMsgTime.get(Calendar.DAY_OF_MONTH));

			} else if ((calendarNow.get(Calendar.MONTH)- calendarMsgTime.get(Calendar.MONTH)) > 0) {
				return String.valueOf(calendarMsgTime.get(Calendar.MONTH)+1) + "-" + String.valueOf(calendarMsgTime.get(Calendar.DAY_OF_MONTH));
			}else if ((calendarNow.get(Calendar.DAY_OF_MONTH) - calendarMsgTime.get(Calendar.DAY_OF_MONTH)) > 0) {
				return String.valueOf(calendarMsgTime.get(Calendar.MONTH)+1) + "-" + String.valueOf(calendarMsgTime.get(Calendar.DAY_OF_MONTH));
			}else {
				if (calendarMsgTime.get(Calendar.MINUTE)<10) {
					return String.valueOf(calendarMsgTime.get(Calendar.HOUR_OF_DAY)) + ":0" + String.valueOf(calendarMsgTime.get(Calendar.MINUTE));
				}else {
					return String.valueOf(calendarMsgTime.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(calendarMsgTime.get(Calendar.MINUTE));
				}
				
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date now = new Date();
		// Calendar calendarNow=Calendar.getInstance();
		return null;
	}

	
	public static String convertNumToCn(int num) {
		String cn = null;
		switch (num) {
		case 1:
			cn="一";
			break;
		case 2:
			cn="二";
			break;
		case 3:
			cn="三";
			break;
		case 4:
			cn="四";
			break;
		case 5:
			cn="五";
			break;
		case 6:
			cn="六";
			break;
		case 7:
			cn="日";
			break;

		default:
			break;
		}
		return cn;
	}
	
	
	// public static boolean isFileExist (String filePath) {
	//
	// }
}
