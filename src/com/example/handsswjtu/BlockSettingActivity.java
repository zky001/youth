package com.example.handsswjtu;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mobstat.StatService;
import com.handsSwjtu.MyView.MyMessageBox;
import com.handsSwjtu.common.CommonOnTouchListener;
import com.handsSwjtu.common.DatabaseHelper;
import com.handsSwjtu.common.SharePreferenceHelper;

import android.R.anim;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BlockSettingActivity extends Activity {

	private DatabaseHelper databaseHelper;
	private String sql;
	private Cursor cursor;
	private List<String> blockStrings;
	private List<String> IDs;
	private ListView blockListView;
	private EditText editText;
	private RelativeLayout addBlockBtn;
	private ArrayAdapter<String> adapter;
	private Dialog alertDialog;
	private SharePreferenceHelper sharePreferenceHelper; 
	private String stuCode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_block_setting);
		editText = (EditText) findViewById(R.id.inputEdit);
		blockListView = (ListView) findViewById(R.id.BlockedListView);
		addBlockBtn = (RelativeLayout) findViewById(R.id.addBlockBtn);
		addBlockBtn.setOnClickListener(onClickListener);
		blockStrings = new ArrayList<String>();
		IDs = new ArrayList<String>();
		sharePreferenceHelper=new SharePreferenceHelper();
		stuCode=sharePreferenceHelper.getStuCode();
		databaseHelper = new DatabaseHelper(getApplicationContext());
		initValues();
		blockListView.setOnItemClickListener(onItemClickListener);

	}

	public void initValues() {
		sql = "select ID,blockWord from block_setting where belongTo="+stuCode;
		cursor = databaseHelper.dql(sql, new String[] {});
		if (cursor.moveToFirst()) {
			do {
				blockStrings.add(cursor.getString(1));
				IDs.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, blockStrings);
		blockListView.setAdapter(adapter);
	}

	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
			// TODO Auto-generated method stub
			LinearLayout confirmLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.my_message_box, null);
			alertDialog = new Dialog(BlockSettingActivity.this, R.style.dialog);
			alertDialog.setContentView(confirmLayout);

			TextView messageBoxContent = ((TextView) confirmLayout.findViewById(R.id.messageBoxContent));
			messageBoxContent.setText("从黑名单删除这条记录？");
			final RelativeLayout positiveButton = (RelativeLayout) confirmLayout.findViewById(R.id.positiveButton);
			RelativeLayout negativeButton = (RelativeLayout) confirmLayout.findViewById(R.id.negativeButton);
			positiveButton.setOnTouchListener(new CommonOnTouchListener());
			negativeButton.setOnTouchListener(new CommonOnTouchListener());
			positiveButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					sql = "delete from block_setting where ID=" + IDs.get(arg2);
					Toast.makeText(BlockSettingActivity.this, blockStrings.get(arg2), Toast.LENGTH_SHORT).show();
					databaseHelper.dml(sql, new Object[] {});
					blockStrings.clear();
					IDs.clear();
					alertDialog.dismiss();
					initValues();

				}
			});

			negativeButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					alertDialog.dismiss();
				}
			});
			alertDialog.show();
		}

	};



	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String inputString = editText.getText().toString();
			if (inputString.length() != 0) {
				sql = "insert into block_setting(blockWord,belongTo) values(?,?)";

				databaseHelper.dml(sql, new Object[] { inputString ,stuCode});
				editText.setText("");
				blockStrings.clear();
				initValues();
			} else {
				Toast.makeText(BlockSettingActivity.this, "不能为空哦", Toast.LENGTH_SHORT).show();
			}
		}
	};

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
