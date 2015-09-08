package com.example.handsswjtu;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.mobstat.StatService;
import com.handsSwjtu.common.CommonOnTouchListener;
import com.handsSwjtu.common.DifferentColorListViewAdapter;
import com.handsSwjtu.common.Tools;

import android.R.integer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TakeOutActivity extends Activity {

	private String ServerFilePath="TakeOutDataBase.db";
	private String localFilePath="/handsSwjtu";
	private String fileName="TakeOutDataBaseV2.db";
	private ProgressBar loadingProgressBar;
	private ListView takeOutLv;
	private DifferentColorListViewAdapter adapter;
	private List<Map<String, Object>> takeOutContents;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_take_out);
		loadingProgressBar=(ProgressBar)findViewById(R.id.loading);
		takeOutLv=(ListView)findViewById(R.id.takeOutList);
		takeOutContents=new ArrayList<Map<String,Object>>();
		File file=new File(Environment.getExternalStorageDirectory()+localFilePath,fileName);
		if ((!file.exists())||((System.currentTimeMillis() - file.lastModified()) > 7*24 * 3600 * 1000)) {
			loadDataBase();
		}else {
			initValues();
		}
	}
	
	
	public void initValues() {
		String file=Environment.getExternalStorageDirectory()+localFilePath+"/"+fileName;
		File file2=new File(file);
		boolean a=file2.exists();
		if (a) {
			
		}
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory()+localFilePath+"/"+fileName, null);
		String sql="select ID,name,phoneNum,location from Take_Out";
		Cursor cursor=database.rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			do {
				Map<String, Object> temp=new HashMap<String, Object>();
				temp.put("ID", cursor.getString(0));
				temp.put("name",cursor.getString(1));
				temp.put("phoneNum", cursor.getString(2));
				temp.put("location", cursor.getString(3));
				temp.put("icon", R.drawable.icon_phone_bg);
				takeOutContents.add(temp);
			} while (cursor.moveToNext());
		}
		
		adapter=new DifferentColorListViewAdapter(TakeOutActivity.this, takeOutContents, R.layout.listview_take_out, new String[]{"ID","name","phoneNum","icon"}, new int[]{R.id.ID,R.id.name,R.id.phoneNum,R.id.iconPhone});
		SimpleAdapter.ViewBinder binder=new SimpleAdapter.ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, Object data, String textRepresentation) {
				// TODO Auto-generated method stub
				if (view instanceof ImageButton) {
					final View button=view;
					view.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							LinearLayout listItem=(LinearLayout)button.getParent();
							String name=((TextView)listItem.findViewById(R.id.name)).getText().toString();
							final String phoneNum=((TextView)listItem.findViewById(R.id.phoneNum)).getText().toString();
							LinearLayout confirmLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.my_message_box, null);
							final Dialog alertDialog = new Dialog(TakeOutActivity.this, R.style.dialog);
							alertDialog.setContentView(confirmLayout);
							alertDialog.show();
							TextView messageBoxContent = ((TextView) confirmLayout.findViewById(R.id.messageBoxContent));
							messageBoxContent.setText("拨打电话给"+name+"("+phoneNum+")?");
							final RelativeLayout positiveButton = (RelativeLayout) confirmLayout.findViewById(R.id.positiveButton);
							RelativeLayout negativeButton = (RelativeLayout) confirmLayout.findViewById(R.id.negativeButton);
							positiveButton.setOnTouchListener(new CommonOnTouchListener());
							negativeButton.setOnTouchListener(new CommonOnTouchListener());
							positiveButton.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									Intent phoneIntent = new Intent("android.intent.action.CALL",Uri.parse("tel:" + phoneNum));
									startActivity(phoneIntent);
									alertDialog.dismiss();
								}
							});
							negativeButton.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub

									alertDialog.dismiss();
								}
							});
							
							
						}
					});
				}
				return false;
			}
		};
		adapter.setViewBinder(binder);
		takeOutLv.setAdapter(adapter);
		takeOutLv.setOnItemClickListener(onItemClickListener);
	}
	
	
	
	public void loadDataBase(){
		loadingProgressBar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (Tools.writeHttpFile2SdCard(ServerFilePath, localFilePath, fileName)) {
					handler.sendEmptyMessage(0);
				}else {
					handler.sendEmptyMessage(1);
				}
			}
		}).start();
	}
	

	Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				initValues();
				break;
			case 1:
				//Toast.makeText(TakeOutActivity.this, "数据载入失败", Toast.LENGTH_SHORT).show();
				LinearLayout confirmLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.my_message_box, null);
				final Dialog alertDialog = new Dialog(TakeOutActivity.this, R.style.dialog);
				alertDialog.setContentView(confirmLayout);
				alertDialog.show();
				TextView messageBoxContent = ((TextView) confirmLayout.findViewById(R.id.messageBoxContent));
				messageBoxContent.setText("数据获取失败，是否重试？");
				final RelativeLayout positiveButton = (RelativeLayout) confirmLayout.findViewById(R.id.positiveButton);
				RelativeLayout negativeButton = (RelativeLayout) confirmLayout.findViewById(R.id.negativeButton);
				positiveButton.setOnTouchListener(new CommonOnTouchListener());
				negativeButton.setOnTouchListener(new CommonOnTouchListener());
				positiveButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						loadDataBase();
						alertDialog.dismiss();
					}
				});
				negativeButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						alertDialog.dismiss();
					}
				});
				break;
			default:
				break;
			}
			loadingProgressBar.setVisibility(View.INVISIBLE);
		}
	};
	
	
	OnItemClickListener onItemClickListener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			Intent it=new Intent(TakeOutActivity.this,TakeOutDetailActivity.class);
			Map<String, Object> child=takeOutContents.get(arg2);
			it.putExtra("categoryID", (String)child.get("ID"));
			it.putExtra("name", (String)child.get("name"));
			it.putExtra("phoneNum", (String)child.get("phoneNum"));
			it.putExtra("location", (String)child.get("location"));
			startActivity(it);
			
			
		}
	};
	

	public  void refresh(View view ) {
		takeOutContents.clear();
		loadDataBase();
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
