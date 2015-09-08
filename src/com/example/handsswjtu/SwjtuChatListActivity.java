package com.example.handsswjtu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.mobstat.StatService;
import com.handsSwjtu.common.CommonOnTouchListener;
import com.handsSwjtu.common.DataProvider;
import com.handsSwjtu.common.DatabaseHelper;
import com.handsSwjtu.common.DifferentColorListViewAdapter;
import com.handsSwjtu.common.SharePreferenceHelper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.R.integer;
import android.app.Activity;
import android.app.DownloadManager.Request;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class SwjtuChatListActivity extends Activity {

	private RelativeLayout addFriend;
	private RelativeLayout blockSettingBtn;
	private ListView friendListView;
	private String errorMsg;
	private List<Map<String, Object>> friendListMaps;
	private ProgressBar loadingProgressBar;
	private SimpleAdapter adapter;
	private SharePreferenceHelper sharePreferenceHelper;
	DatabaseHelper databaseHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_swjtu_chat_list);
		addFriend = (RelativeLayout) findViewById(R.id.addFriend);
		blockSettingBtn = (RelativeLayout) findViewById(R.id.blockSettingBtn);
		loadingProgressBar = (ProgressBar) findViewById(R.id.loading);
		loadingProgressBar.setVisibility(View.VISIBLE);
		friendListView = (ListView) findViewById(R.id.friendList);
		friendListView.setOnItemClickListener(onItemClickListener);
		friendListMaps = new ArrayList<Map<String, Object>>();
		sharePreferenceHelper = new SharePreferenceHelper();
		databaseHelper = new DatabaseHelper(getApplicationContext());
		errorMsg = new String();
		adapter = new SimpleAdapter(SwjtuChatListActivity.this, friendListMaps, R.layout.listview_swjtu_chat_friend,
				new String[] { "ID", "stuName", "signature", "stuIcon", "msgTime" }, new int[] { R.id.ID, R.id.stuName,
						R.id.signature, R.id.stuIconIv, R.id.msgTime });
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(DataProvider.FriendListProvider(friendListMaps, errorMsg));
			}
		}).start();
		addFriend.setOnTouchListener(new CommonOnTouchListener());
		addFriend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(SwjtuChatListActivity.this, SearchUserActivity.class);
				startActivity(it);
			}
		});

		blockSettingBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(SwjtuChatListActivity.this, BlockSettingActivity.class);
				startActivity(it);
			}
		});
	}

	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			Map<String, Object> map = friendListMaps.get(arg2);
			Intent it = new Intent(SwjtuChatListActivity.this, SwjtuChatWithFriendActivity.class);
			it.putExtra("stuName", map.get("stuName").toString());
			it.putExtra("stuCode", map.get("stuCode").toString());
			startActivityForResult(it, 0);
		}
	};

	public void initValues() {

		adapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Object data, String textRepresentation) {
				// TODO Auto-generated method stub
				if (view instanceof ImageView && data instanceof Bitmap) {
					ImageView i = (ImageView) view;
					i.setImageBitmap((Bitmap) data);
					return true;
				}
				return false;
			}
		});
		friendListView.setAdapter(adapter);
		registerForContextMenu(friendListView);
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case 0:
				initValues();
				//Toast.makeText(SwjtuChatListActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
				break;
			case 1:
				//Toast.makeText(SwjtuChatListActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
			case 2:
				Toast.makeText(SwjtuChatListActivity.this, "数据传输异常", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(SwjtuChatListActivity.this, "联网失败，请重试", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			loadingProgressBar.setVisibility(View.INVISIBLE);
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (resultCode == RESULT_OK) {
			friendListMaps.clear();
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(DataProvider.FriendListProvider(friendListMaps, errorMsg));
			adapter.notifyDataSetChanged();
			
		}
	}

	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		menu.setHeaderTitle("请选择");
		int menuId = 0;
		menu.add(0, menuId++, Menu.NONE, "删除会话");
		menu.add(0, menuId++, Menu.NONE, "加入黑名单");
		menu.add(0, menuId++, Menu.NONE, "查看聊天记录");
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int position = menuInfo.position;
		String stuCode = friendListMaps.get(position).get("stuCode").toString();
		String stuName = friendListMaps.get(position).get("stuName").toString();
		String userNow = sharePreferenceHelper.getStuCode();
		switch (item.getItemId()) {
		case 0:

			// Toast.makeText(this,
			// friendListMaps.get(position).get("stuCode").toString(),
			// Toast.LENGTH_SHORT).show();
			String sql = "delete from swjtu_chat_messages where stuCode='" + stuCode + "'";

			databaseHelper.dml(sql, new Object[] {});
			friendListMaps.clear();
			DataProvider.FriendListProvider(friendListMaps, errorMsg);
			adapter.notifyDataSetChanged();
			break;
		case 1: {
			sql = "select * from block_setting where belongTo='" + userNow + "' and blockWord ='" + stuCode
					+ "' or blockWord='" + stuName + "'";
			Cursor cursor = databaseHelper.dql(sql, new String[] {});
			if (cursor.moveToFirst()) {
				Toast.makeText(SwjtuChatListActivity.this, "该用户已经被屏蔽了", Toast.LENGTH_SHORT).show();
			} else {
				sql = "insert into block_setting(blockWord,belongTo) values(?,?)";

				databaseHelper.dml(sql, new Object[] { stuCode, userNow });
				Toast.makeText(SwjtuChatListActivity.this, "您把（" + stuCode + "）" + stuName + "加入了黑名单",
						Toast.LENGTH_SHORT).show();
			}

		}

			break;
		case 2: {
			Intent it = new Intent(SwjtuChatListActivity.this, SwjtuChatLogActivity.class);
			it.putExtra("stuCodeChatNow", stuCode);
			it.putExtra("stuHome", userNow);
			startActivityForResult(it,0);
		}
		default:
			break;
		}
		return true;

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
