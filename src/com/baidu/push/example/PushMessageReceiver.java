package com.baidu.push.example;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.handsswjtu.R;

import android.R.integer;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.example.handsswjtu.NewsDetailActivity;
import com.example.handsswjtu.SwjtuChatWithFriendActivity;
import com.handsSwjtu.Objects.SwjtuChatMsgEntity;
import com.handsSwjtu.common.ContextUtil;
import com.handsSwjtu.common.DataProvider;
import com.handsSwjtu.common.DatabaseHelper;
import com.handsSwjtu.common.SharePreferenceHelper;
import com.handsSwjtu.httpConnect.HttpConnect;

/**
 * Push消息处理receiver
 */
public class PushMessageReceiver extends BroadcastReceiver {
	/** TAG to Log */
	public static final String TAG = PushMessageReceiver.class.getSimpleName();
	private SharePreferenceHelper sharePreferenceHelper;
	private DatabaseHelper databaseHelper;
	private String sql;
	AlertDialog.Builder builder;

	/**
	 * @param context
	 *            Context
	 * @param intent
	 *            接收的intent
	 */
	@Override
	public void onReceive(final Context context, Intent intent) {

		Log.d(TAG, ">>> Receive intent: \r\n" + intent);

		sharePreferenceHelper = new SharePreferenceHelper();
		if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
			// 获取消息内容
			String message = intent.getExtras().getString(PushConstants.EXTRA_PUSH_MESSAGE_STRING);

			// 消息的用户自定义内容读取方式
			Log.i(TAG, "onMessage: " + message);

			// 自定义内容的json串
			Log.d(TAG, "EXTRA_EXTRA = " + intent.getStringExtra(PushConstants.EXTRA_EXTRA));

			// 用户在此自定义处理消息,以下代码为demo界面展示用
			// Intent responseIntent = null;
			// responseIntent = new Intent(Utils.ACTION_MESSAGE);
			// responseIntent.putExtra(Utils.EXTRA_MESSAGE, message);
			// responseIntent.setClass(context, PushDemoActivity.class);
			// responseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// context.startActivity(responseIntent);

			//Toast.makeText(context, intent.getStringExtra(PushConstants.EXTRA_EXTRA), Toast.LENGTH_SHORT).show();
			String aString = intent.getStringExtra(PushConstants.EXTRA_EXTRA);
			try {

				JSONObject jsonObject = (new JSONObject(message)).getJSONObject("custom_content");
				int msgType = jsonObject.getInt("msgType");
				if (msgType == 0) {
					String msgFrom = jsonObject.getString("msgFrom");
					String msgTo = jsonObject.getString("msgTo");
					String stuName = jsonObject.getString("stuName");
					SwjtuChatMsgEntity swjtuChatMsgEntity = new SwjtuChatMsgEntity(jsonObject.getString("msgFrom"),
							jsonObject.getString("msgTo"), new String(Base64.decode(jsonObject.getString("msgContent"),
									Base64.DEFAULT)), jsonObject.getString("msgFrom"), jsonObject.getString("stuName"),
							jsonObject.getString("msgCtime"), jsonObject.getString("msgTo"), 0,true);
					
					databaseHelper = new DatabaseHelper(context);
					sql = "select * from block_setting where belongTo='" + msgTo + "' and blockWord ='" + msgFrom
							+ "' or blockWord='" + stuName+"'";
					Cursor cursor = databaseHelper.dql(sql, new String[] {});
					if (!cursor.moveToFirst()) {
						DataProvider.swjtuChatMsgToDatabase(swjtuChatMsgEntity);
						Intent msgIntent = new Intent();
						msgIntent.putExtra("msgFrom", jsonObject.getString("msgFrom"));
						msgIntent.putExtra("msgTo", jsonObject.getString("msgTo"));
						msgIntent.putExtra("stuName", jsonObject.getString("stuName"));
						msgIntent.putExtra("stuCode", jsonObject.getString("msgFrom"));
						msgIntent.putExtra("msgCtime", jsonObject.getString("msgCtime"));
						msgIntent.putExtra("msgContent",
								new String(Base64.decode(jsonObject.getString("msgContent"), Base64.DEFAULT)));

						if (sharePreferenceHelper.getStuCode().equals(jsonObject.getString("msgTo"))) {

							if (SwjtuChatWithFriendActivity.isForeground
									&& SwjtuChatWithFriendActivity.stuCodeChatNow.equals(jsonObject
											.getString("msgFrom"))) {
								msgIntent.setAction(SwjtuChatWithFriendActivity.MESSAGE_RECEIVED_ACTION);
								context.sendBroadcast(msgIntent);
							} else {

								// msgIntent.setClass(packageContext, cls)
								NotificationManager nm = (NotificationManager) ContextUtil.getInstance()
										.getSystemService(Context.NOTIFICATION_SERVICE);
								Notification n = new Notification(R.drawable.simple_notification_icon, "您有新消息了",
										System.currentTimeMillis());
								// Intent it=new Intent()
								n.flags = Notification.FLAG_AUTO_CANCEL;
								n.defaults = Notification.DEFAULT_ALL;
								msgIntent.setClass(context, SwjtuChatWithFriendActivity.class);

								PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, msgIntent, 0);
								n.setLatestEventInfo(context, jsonObject.getString("stuName"),
										new String(Base64.decode(jsonObject.getString("msgContent"), Base64.DEFAULT)),
										pendingIntent);
								nm.notify(R.drawable.simple_notification_icon, n);

							}
						}
					}

					// String message =
					// bundle.getString(JPushInterface.EXTRA_MESSAGE);
					// String extras =
					// bundle.getString(JPushInterface.EXTRA_EXTRA);
					// Intent msgIntent = new
					// Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
					// msgIntent.putExtra(SwjtuChatWithFriendActivity.KEY_MESSAGE,
					// message);
					// if (!ExampleUtil.isEmpty(extras)) {
					// try {
					// JSONObject extraJson = new JSONObject(extras);
					// if (null != extraJson && extraJson.length() > 0) {
					// msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
					// }
					// } catch (JSONException e) {
					//
					// }
					//
					// }

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (intent.getAction().equals(PushConstants.ACTION_RECEIVE)) {
			// 处理绑定等方法的返回数据
			// PushManager.startWork()的返回值通过PushConstants.METHOD_BIND得到
			// 获取方法
			final String method = intent.getStringExtra(PushConstants.EXTRA_METHOD);
			// 方法返回错误码。若绑定返回错误（非0），则应用将不能正常接收消息。
			// 绑定失败的原因有多种，如网络原因，或access token过期。
			// 请不要在出错时进行简单的startWork调用，这有可能导致死循环。
			// 可以通过限制重试次数，或者在其他时机重新调用来解决。
			int errorCode = intent.getIntExtra(PushConstants.EXTRA_ERROR_CODE, PushConstants.ERROR_SUCCESS);
			String content = "";
			if (intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT) != null) {
				// 返回内容
				content = new String(intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT));
			}
			try {
				JSONObject responseJsonObject = (new JSONObject(content)).getJSONObject("response_params");
				String appId = responseJsonObject.getString("appid");
				final String channelId = responseJsonObject.getString("channel_id");
				final String userId = responseJsonObject.getString("user_id");

				// String requestId=responseJsonObject.getString("request_id");
				final String stuCode = sharePreferenceHelper.getStuCode();
				sharePreferenceHelper.setUserId(userId);
				sharePreferenceHelper.setChannelId(channelId);
				sharePreferenceHelper.setAppId(appId);

				final Handler handler = new Handler() {
					public void handleMessage(Message msg) {
						switch (msg.what) {
						case 0:
							//Toast.makeText(context, "数据更新成功", Toast.LENGTH_SHORT).show();
							break;
						case 1:
							//Toast.makeText(context, "联网失败，请重试", Toast.LENGTH_SHORT).show();
							break;
						default:
							break;
						}
					}
				};
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						String resultString = HttpConnect.registerPush(userId, channelId, stuCode);
						if (sharePreferenceHelper.getStuCode().equals("null")) {
							HttpConnect.setBaiduPush(null, null, userId, channelId);
						}else {
							HttpConnect.setBaiduPush(sharePreferenceHelper.getPushSetting(), stuCode, userId,channelId);
						}
						
						if (resultString != null) {
							handler.sendEmptyMessage(0);
						} else {
							handler.sendEmptyMessage(1);
						}

					}
				}).start();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 用户在此自定义处理消息,以下代码为demo界面展示用
			Log.d(TAG, "onMessage: method : " + method);
			Log.d(TAG, "onMessage: result : " + errorCode);
			Log.d(TAG, "onMessage: content : " + content);
			//Toast.makeText(context, "method : " + method + "\n result: " + errorCode + "\n content = " + content,Toast.LENGTH_SHORT).show();
			/*
			 * Intent responseIntent = null; responseIntent = new
			 * Intent(Utils.ACTION_RESPONSE);
			 * responseIntent.putExtra(Utils.RESPONSE_METHOD, method);
			 * responseIntent.putExtra(Utils.RESPONSE_ERRCODE, errorCode);
			 * responseIntent.putExtra(Utils.RESPONSE_CONTENT, content);
			 * responseIntent.setClass(context, PushDemoActivity.class);
			 * responseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 * context.startActivity(responseIntent);
			 */
			// 可选。通知用户点击事件处理
		} else if (intent.getAction().equals(PushConstants.ACTION_RECEIVER_NOTIFICATION_CLICK)) {
			Log.d(TAG, "intent=" + intent.toUri(0));
			// 自定义内容的json串
			Log.d(TAG, "EXTRA_EXTRA = " + intent.getStringExtra(PushConstants.EXTRA_EXTRA));
			Intent aIntent = new Intent();
			String notifiTitle = intent.getStringExtra(PushConstants.EXTRA_NOTIFICATION_TITLE);
			aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Bundle bundle = new Bundle();
			String aString=intent.getStringExtra("newsTypeNow");
			String bString=intent.getStringExtra("newsCode");
			int a=aString.length();
			int b=bString.length();
			bundle.putInt("newsTypeNow", Integer.parseInt(intent.getStringExtra("newsTypeNow")));
			bundle.putString("newsCode", intent.getStringExtra("newsCode"));
			bundle.putString("newsTitle", intent.getStringExtra("newsTitle"));
			aIntent.putExtras(bundle);
			aIntent.setClass(context, NewsDetailActivity.class);

			context.startActivity(aIntent);
		}
	}

}
