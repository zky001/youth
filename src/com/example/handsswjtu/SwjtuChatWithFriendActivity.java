package com.example.handsswjtu;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.mobstat.StatService;
import com.baidu.push.example.Utils;
import com.handsSwjtu.Objects.SwjtuChatMsgEntity;
import com.handsSwjtu.common.BtnOnTouchListener;
import com.handsSwjtu.common.DataProvider;
import com.handsSwjtu.common.DatabaseHelper;
import com.handsSwjtu.common.SharePreferenceHelper;
import com.handsSwjtu.common.Tools;
import com.handsSwjtu.httpConnect.HttpConnect;

import android.R.integer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path.Direction;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SwjtuChatWithFriendActivity extends Activity {

	private String stuHome;
	private String stuName;
	private LinearLayout chatContently;
	private ProgressBar loadingProgressBar;
	private RelativeLayout MsgContentSendBtn;
	private EditText MsgContentEditText;
	private List<NameValuePair> params;
	private TextView chatWithWhoTextView;
	public static boolean isForeground = false;
	public static String stuCodeChatNow = null;
	private ScrollView msgContentScrollView;
	// private String stuCode;
	// for receive customer msg from jpush server
	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.example.handsSwjtu.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";
	private View chatSendView;
	private SharePreferenceHelper sharePreferenceHelper;
	private boolean loadingFlag = true;
	public static String SWJTU_CHAT_ICON_PATH = "/handsSwjtu/swjtu_chat/personalIcons";
	private Bitmap bitmapIconSwjtuCodeChatNow;
	private Bitmap bitmapStuHome;
	private List<View> chatSendMsgViews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setResult(RESULT_OK);
		isForeground = true;
		PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
				Utils.getMetaValue(this, "api_key"));
		setContentView(R.layout.activity_swjtu_chat_with_friend);
		chatSendMsgViews=new ArrayList<View>();
		chatContently = (LinearLayout) findViewById(R.id.chatContent);
		loadingProgressBar = (ProgressBar) findViewById(R.id.loading);
		loadingProgressBar.setVisibility(View.INVISIBLE);
		MsgContentSendBtn = (RelativeLayout) findViewById(R.id.msgContentSendBtn);
		MsgContentSendBtn.setOnTouchListener(new BtnOnTouchListener());
		MsgContentSendBtn.setOnClickListener(onClickListener);
		MsgContentEditText = (EditText) findViewById(R.id.msgContentEdit);
		chatWithWhoTextView = (TextView) findViewById(R.id.chatWithWho);
		chatWithWhoTextView.setText(getIntent().getStringExtra("stuName"));
		stuCodeChatNow = getIntent().getStringExtra("stuCode");
		stuName = getIntent().getStringExtra("stuName");
		msgContentScrollView = (ScrollView) findViewById(R.id.msgContentScrollView);
		params = new ArrayList<NameValuePair>();
		sharePreferenceHelper = new SharePreferenceHelper();
		stuHome = sharePreferenceHelper.getStuCode();
		// View chatSendView= View
		// .inflate(SwjtuChatWithFriendActivity.this, R.layout.chat_send_layout,
		// null);
		// View chatReceiveView= View
		// .inflate(SwjtuChatWithFriendActivity.this,
		// R.layout.chat_receive_layout, null);
		// chatContently.addView(chatSendView);
		// chatContently.addView(chatReceiveView);
		registerMessageReceiver();
		initContent();

	}

	public void initContent() {

		if (isIconNeedUpdate(stuCodeChatNow)) {
			loadPersonalIcon(stuCodeChatNow);
			bitmapIconSwjtuCodeChatNow = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
		} else {

			bitmapIconSwjtuCodeChatNow = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()
					+ SWJTU_CHAT_ICON_PATH + "/" + stuCodeChatNow + ".png");
			if (bitmapIconSwjtuCodeChatNow == null) {
				bitmapIconSwjtuCodeChatNow = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
			}

		}
		if (isIconNeedUpdate(stuHome)) {
			loadPersonalIcon(stuHome);
			bitmapStuHome = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
		} else {
			bitmapStuHome = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + SWJTU_CHAT_ICON_PATH
					+ "/" + stuHome + ".png");
			if (bitmapStuHome == null) {
				bitmapStuHome = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
			}
		}

		List<SwjtuChatMsgEntity> swjtuChatMsgEntities = DataProvider.swjtuChatWithFriendInitProvider(stuHome,
				stuCodeChatNow);
		for (int i = 0; i < swjtuChatMsgEntities.size(); i++) {
			if (swjtuChatMsgEntities.get(i).getMsgType() == 0) {
				addNewReceivedMessageView(swjtuChatMsgEntities.get(i).getMsgContent(), swjtuChatMsgEntities.get(i)
						.getMsgCtime());
			} else {
				addNewSendMessageView(swjtuChatMsgEntities.get(i).getMsgContent(), swjtuChatMsgEntities.get(i)
						.getMsgCtime(),swjtuChatMsgEntities.get(i).isHaveSend());
			}
		}
		loadingFlag = false;
	}

	public void addNewReceivedMessageView(String msgContent, String msgCtime) {
		View chatReceiveView = View.inflate(SwjtuChatWithFriendActivity.this, R.layout.chat_receive_layout, null);
		((TextView) chatReceiveView.findViewById(R.id.msgFrom)).setText(stuName);
		((TextView) chatReceiveView.findViewById(R.id.msgContent)).setText(msgContent);
		((TextView) chatReceiveView.findViewById(R.id.msgCtime)).setText(Tools.getMsgTime(msgCtime));
		((ImageView) chatReceiveView.findViewById(R.id.stuChatNowIcon)).setImageBitmap(bitmapIconSwjtuCodeChatNow);
		SwjtuChatMsgEntity swjtuChatMsgEntity = new SwjtuChatMsgEntity(stuCodeChatNow, stuHome, msgContent,
				stuCodeChatNow, stuName, msgCtime, stuHome, 0,false);

		chatContently.addView(chatReceiveView);
		registerForContextMenu(chatReceiveView);
		// msgContentScrollView.fullScroll(ScrollView.FOCUS_DOWN);
		msgContentScrollView.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				msgContentScrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
	}

	public Map<String, Object> addNewSendMessageView(String msgContent, String msgCtime,boolean isHaveSend) {
		Map<String,Object> temp=new HashMap<String, Object>();
		chatSendView = View.inflate(SwjtuChatWithFriendActivity.this, R.layout.chat_send_layout, null);
		((TextView) chatSendView.findViewById(R.id.msgContent)).setText(msgContent);
		((TextView) chatSendView.findViewById(R.id.msgCtime)).setText(Tools.getMsgTime(msgCtime));
		((ImageView) chatSendView.findViewById(R.id.stuHomeIcon)).setImageBitmap(bitmapStuHome);

		chatSendMsgViews.add(chatSendView);
		chatContently.addView(chatSendView);
		SwjtuChatMsgEntity swjtuChatMsgEntity = new SwjtuChatMsgEntity(stuHome, stuCodeChatNow, msgContent,
				stuCodeChatNow, stuName, msgCtime, stuHome, 1,false);
		if (!loadingFlag) {
			temp.put("lastId", DataProvider.swjtuChatMsgToDatabase(swjtuChatMsgEntity));
			((ProgressBar) chatSendView.findViewById(R.id.sending)).setVisibility(View.VISIBLE);
		}else {
			if (isHaveSend) {
				((ProgressBar) chatSendView.findViewById(R.id.sending)).setVisibility(View.INVISIBLE);
				((ImageView)chatSendView.findViewById(R.id.sendError)).setVisibility(View.INVISIBLE);
			}else {
				((ProgressBar) chatSendView.findViewById(R.id.sending)).setVisibility(View.GONE);
				((ImageView)chatSendView.findViewById(R.id.sendError)).setVisibility(View.VISIBLE);
			}
		}
		
		registerForContextMenu(chatSendView);
		// msgContentScrollView.fullScroll(ScrollView.FOCUS_DOWN);
		msgContentScrollView.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				msgContentScrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
		temp.put("position",chatSendMsgViews.size()-1);
		return temp;
	}

	OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			switch (v.getId()) {
			case R.id.msgContentSendBtn:
				// loadingProgressBar.setVisibility(View.VISIBLE);
				if (MsgContentEditText.getText().toString().length() != 0) {
					MsgContentSend();
					MsgContentEditText.setText("");
				} else {
					Toast.makeText(SwjtuChatWithFriendActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
				}

				break;

			default:
				break;
			}
		}
	};

	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				String msgContent = intent.getStringExtra("msgContent");
				String msgCtime = intent.getStringExtra("msgCtime");
				addNewReceivedMessageView(msgContent, msgCtime);
			}
		}
	}

	public void MsgContentSend() {
		String msgContent = Base64.encodeToString(MsgContentEditText.getText().toString().getBytes(), Base64.DEFAULT);
		Map<String, Object> temp=addNewSendMessageView(MsgContentEditText.getText().toString(), Tools.getTimeNow(),false);
		final int position=(Integer)temp.get("position");
		final long lastId=(Long)temp.get("lastId");
		params.add(new BasicNameValuePair("msgContent", msgContent));
		params.add(new BasicNameValuePair("msgFrom", stuHome));
		params.add(new BasicNameValuePair("msgSendTo", stuCodeChatNow));
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message=new Message();
				Bundle bundle=new Bundle();
				bundle.putInt("position", position);
				bundle.putLong("lastId", lastId);
				message.setData(bundle);
				String resultString = HttpConnect.sendMsg(params);
				if (resultString != null) {
					try {
						JSONObject jsonObject = new JSONObject(resultString);
						if (jsonObject.getInt("state") == 0) {
							message.what=0;
						} else {
							message.what=1;
						}
					} catch (Exception e) {
						// TODO: handle exception
						message.what=2;
					}
				} else {
					message.what=3;
				}
				handler.sendMessage(message);
			}
		}).start();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			Bundle bundle=msg.getData();
			int position=bundle.getInt("position");
			long lastId=bundle.getLong("lastId");
			switch (msg.what) {

			case 0:
				//Toast.makeText(SwjtuChatWithFriendActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
				DatabaseHelper databaseHelper=new DatabaseHelper(getApplicationContext());
				String sql="update swjtu_chat_messages set isHaveSend='0' where ID='"+lastId+"'";
				databaseHelper.dml(sql, new String[]{});
				((ProgressBar) chatSendMsgViews.get(position).findViewById(R.id.sending)).setVisibility(View.INVISIBLE);
				//((ImageView)chatSendMsgViews.get(position).findViewById(R.id.sendError)).setVisibility(View.VISIBLE);
				break;
			case 1:
				((ProgressBar) chatSendMsgViews.get(position).findViewById(R.id.sending)).setVisibility(View.GONE);
				((ImageView)chatSendMsgViews.get(position).findViewById(R.id.sendError)).setVisibility(View.VISIBLE);
				Toast.makeText(SwjtuChatWithFriendActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
			case 2:
				((ProgressBar) chatSendMsgViews.get(position).findViewById(R.id.sending)).setVisibility(View.GONE);
				((ImageView)chatSendMsgViews.get(position).findViewById(R.id.sendError)).setVisibility(View.VISIBLE);
				Toast.makeText(SwjtuChatWithFriendActivity.this, "数据传输异常", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				((ProgressBar) chatSendMsgViews.get(position).findViewById(R.id.sending)).setVisibility(View.GONE);
				((ImageView)chatSendMsgViews.get(position).findViewById(R.id.sendError)).setVisibility(View.VISIBLE);
				Toast.makeText(SwjtuChatWithFriendActivity.this, "联网失败，请重试", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			((ProgressBar) chatSendMsgViews.get(position).findViewById(R.id.sending)).setVisibility(View.INVISIBLE);
			loadingProgressBar.setVisibility(View.INVISIBLE);
		}
	};

	public void loadPersonalIcon(final String stuCode) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Calendar calendar = Calendar.getInstance();
				String filePath = SWJTU_CHAT_ICON_PATH;
				String fileName = stuCode + ".png";
				Tools.writeHttpFile2SdCard("swjtu_chat/personalIcons/" + fileName, filePath, fileName);
			}
		}).start();
	}

	public boolean isIconNeedUpdate(String stuCode) {
		String filePath = Environment.getExternalStorageDirectory() + SWJTU_CHAT_ICON_PATH;
		String fileName = stuCode + ".png";
		File file = new File(filePath, fileName);
		boolean a = file.exists();
		long b = (System.currentTimeMillis() - file.lastModified());
		if (!file.exists() || (System.currentTimeMillis() - file.lastModified()) > 6 * 3600 * 1000) {
			return true;
		} else {
			return false;
		}
	}

	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		menu.setHeaderTitle("请选择");
		int menuId = 0;
		menu.add(0, menuId++, Menu.NONE, "复制");
		menu.add(0, menuId++, Menu.NONE, "删除");
	}

	public boolean onContextItemSelected(MenuItem item) {

		Toast.makeText(SwjtuChatWithFriendActivity.this, "写软件的童鞋说他不会写这个，喊你点击右上角进聊天记录操作", Toast.LENGTH_SHORT).show();
		return true;

	}

	public void startChatLogActivity(View view) {
		Intent it = new Intent(SwjtuChatWithFriendActivity.this, SwjtuChatLogActivity.class);
		it.putExtra("stuCodeChatNow", stuCodeChatNow);
		it.putExtra("stuHome", stuHome);
		startActivityForResult(it,0);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			loadingFlag=true;
			chatContently.removeAllViews();
			// TODO Auto-generated method stub
			initContent();
		}
	}
	
	
	public void finishActivity(View view){
		finish();
	}


	@Override
	protected void onDestroy() {
		unregisterReceiver(mMessageReceiver);

		super.onDestroy();
	}

	public void onResume() {
		isForeground = true;
		super.onResume();
		StatService.onResume(this);
	}

	public void onPause() {
		isForeground = false;
		super.onPause();
		StatService.onPause(this);
	}
	


}
