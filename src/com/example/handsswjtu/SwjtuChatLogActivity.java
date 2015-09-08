package com.example.handsswjtu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.baidu.mobstat.StatService;
import com.handsSwjtu.common.DataProvider;
import com.handsSwjtu.common.DatabaseHelper;

import android.os.Bundle;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SwjtuChatLogActivity extends Activity {

	private String stuHome;
	private String stuChatNow;
	private EditText goPageEditText;
	private TextView totalPageTextView;
	private List<Map<String, Object>> chatLogMaps;
	private int pageNo = -1;
	private int totalPage;
	private SimpleAdapter adapter;
	private ListView msgLogLv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_swjtu_chat_log);
		msgLogLv = (ListView) findViewById(R.id.msgLogListView);
		registerForContextMenu(msgLogLv);
		stuHome = getIntent().getStringExtra("stuHome");
		stuChatNow = getIntent().getStringExtra("stuCodeChatNow");
		goPageEditText = (EditText) findViewById(R.id.goPageEd);
		totalPageTextView = (TextView) findViewById(R.id.totalPageTv);
		chatLogMaps = new ArrayList<Map<String, Object>>();
		totalPage = DataProvider.chatLogProvider(chatLogMaps, stuChatNow, stuHome, -1);
		pageNo = totalPage;
		totalPageTextView.setText(String.valueOf(totalPage));
		goPageEditText.setText(String.valueOf(pageNo));
		goPageEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				try {

					int inputPageNo = Integer.parseInt(goPageEditText.getText().toString());
					if (inputPageNo > 0 && inputPageNo <= totalPage) {
						pageNo = inputPageNo;
						chatLogMaps.clear();
						DataProvider.chatLogProvider(chatLogMaps, stuChatNow, stuHome, pageNo);
						adapter.notifyDataSetChanged();

					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		}); 
		initValues();

	}

	private void initValues() {
		// adapter=new SimpleAdapter(SwjtuChatLogActivity.this,);
		adapter = new SimpleAdapter(SwjtuChatLogActivity.this, chatLogMaps, R.layout.listview_swjtu_chat_log,
				new String[] { "msgFrom", "msgContent", "msgTime" }, new int[] { R.id.msgFrom, R.id.msgContent,
						R.id.msgTime });
		msgLogLv.setAdapter(adapter);
	}

	public void previousPage(View view) {
		if (pageNo > 1) {
			pageNo = pageNo - 1;
			chatLogMaps.clear();
			DataProvider.chatLogProvider(chatLogMaps, stuChatNow, stuHome, pageNo);
			adapter.notifyDataSetChanged();
			goPageEditText.setText(String.valueOf(pageNo));
		} else {
			Toast.makeText(SwjtuChatLogActivity.this, "已经是第一页了", Toast.LENGTH_SHORT).show();
		}

	}

	public void nextPage(View view) {
		if (pageNo < totalPage) {
			pageNo = pageNo + 1;
			chatLogMaps.clear();
			DataProvider.chatLogProvider(chatLogMaps, stuChatNow, stuHome, pageNo);
			adapter.notifyDataSetChanged();
			goPageEditText.setText(String.valueOf(pageNo));
		} else {
			Toast.makeText(SwjtuChatLogActivity.this, "已经是最后一页了", Toast.LENGTH_SHORT).show();
		}

	}

	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		menu.setHeaderTitle("请选择");
		int menuId = 0;
		menu.add(0, menuId++, Menu.NONE, "复制文字");
		menu.add(0, menuId++, Menu.NONE, "删除");
	}

	
	@SuppressLint("NewApi")
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int position = menuInfo.position;
		switch (item.getItemId()) {
		case 0:
			String msgContent=(String) (chatLogMaps.get(position).get("msgContent"));
			int currentapiVersion=android.os.Build.VERSION.SDK_INT;
			if (currentapiVersion<11) {
				android.text.ClipboardManager clipboardManager=(android.text.ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
				clipboardManager.setText(msgContent);
			}else {
				android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				clipboardManager.setText(msgContent);
			}


			
			Toast.makeText(SwjtuChatLogActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
			break;
		case 1:
			DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
			String sql = "delete from swjtu_chat_messages where ID=" + chatLogMaps.get(position).get("ID");
			databaseHelper.dml(sql, new Object[] {});
			chatLogMaps.clear();
			DataProvider.chatLogProvider(chatLogMaps, stuChatNow, stuHome, pageNo);
			adapter.notifyDataSetChanged();
			goPageEditText.setText(String.valueOf(pageNo));
			setResult(RESULT_OK);
			break;
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
