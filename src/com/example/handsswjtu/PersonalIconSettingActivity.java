package com.example.handsswjtu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.baidu.mobstat.StatService;
import com.handsSwjtu.common.FileUtils;
import com.handsSwjtu.common.SharePreferenceHelper;
import com.handsSwjtu.common.Tools;
import com.handsSwjtu.httpConnect.HttpConnect;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class PersonalIconSettingActivity extends Activity {

	private final int FILE_SELECT_CODE = 1;
	private ImageView personalIconIv;
	private LinearLayout commitBtn;
	private ProgressBar loadingProgressBar;
	private String errorString;
	private List<NameValuePair> params;
	private Bitmap bitmap;
	private SharePreferenceHelper sharePreferenceHelper;
	private boolean HAVA_SET_ICON=false;
	private String usrNow;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_personal_icon_setting);
		personalIconIv=(ImageView)findViewById(R.id.personIconIv);
		commitBtn=(LinearLayout)findViewById(R.id.commitBtn);
		loadingProgressBar=(ProgressBar)findViewById(R.id.loading);
		personalIconIv.setOnClickListener(onClickListener);
		sharePreferenceHelper=new SharePreferenceHelper();
		commitBtn.setOnClickListener(onClickListener);
		params=new ArrayList<NameValuePair>();
		usrNow=sharePreferenceHelper.getStuCode();
	}

	
	OnClickListener onClickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.personIconIv:
				Intent it = new Intent(Intent.ACTION_GET_CONTENT);
				it.setType("*/*");
				it.addCategory(Intent.CATEGORY_OPENABLE);
				startActivityForResult(Intent.createChooser(it, "选择所需要的图片"), FILE_SELECT_CODE);
				break;
			case R.id.commitBtn:
				if (!HAVA_SET_ICON) {
					Toast.makeText(PersonalIconSettingActivity.this, "未做更改", Toast.LENGTH_SHORT).show();
				}else{
					
				
				params.add(new BasicNameValuePair("icon", Tools.iconBitmap2String(bitmap)));
				params.add(new BasicNameValuePair("userNow", sharePreferenceHelper.getStuCode()));
				loadingProgressBar.setVisibility(View.VISIBLE);
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String resultString = HttpConnect.setIcon(params);
						if (resultString != null) {
							try {
								JSONObject jsonObject = new JSONObject(resultString);
								errorString = jsonObject.getString("errorMsg");
								if (jsonObject.getInt("state") == 0) {
									handler.sendEmptyMessage(0);
								} else {
									handler.sendEmptyMessage(1);
								}
							} catch (Exception e) {
								// TODO: handle exception
								handler.sendEmptyMessage(2);
							}
						} else {
							handler.sendEmptyMessage(3);
						}
					}
				}).start();;
				}
				break;

			default:
				break;
			}
		}
	};
	
	
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case 0:
				Toast.makeText(PersonalIconSettingActivity.this, errorString, Toast.LENGTH_SHORT).show();
				Tools.writeImg2SdCard(bitmap, "/hansSwjtu/swjtu_chat/personalIcons",usrNow+".png" );
				finish();
				break;
			case 1:
				Toast.makeText(PersonalIconSettingActivity.this, errorString, Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Toast.makeText(PersonalIconSettingActivity.this, "设置失败，请重试", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(PersonalIconSettingActivity.this, "联网失败，请重试", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			loadingProgressBar.setVisibility(View.INVISIBLE);
		}
	};

	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case FILE_SELECT_CODE:
			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();
				String path = FileUtils.getPath(PersonalIconSettingActivity.this, uri);
				String end = path.substring(path.lastIndexOf(".") + 1, path.length()).toLowerCase();
				if ((end.equals("jpg") || end.equals("jpeg") || end.equals("gif") || end.equals("png"))) {

					BitmapFactory.Options options = new BitmapFactory.Options();
					// options.inJustDecodeBounds=false;
					options.inJustDecodeBounds=true;
					bitmap = BitmapFactory.decodeFile(path, options);
					int be=(int)(options.outHeight/ (float)90);
					if (be <= 0)be = 1;
					options.inSampleSize = be;
					 options.inJustDecodeBounds =false;
					 bitmap = BitmapFactory.decodeFile(path, options);
					 //Bitmap bitmap=bitmapTemp.createBitmap(bitmapTemp, 0, 0, 90, 90);
					personalIconIv.setImageBitmap(bitmap);
					HAVA_SET_ICON=true;
				} else {
					Toast.makeText(PersonalIconSettingActivity.this, "您选择地文件格式有误，只支持jpg、gif、png格式图片上传", Toast.LENGTH_SHORT)
							.show();

				}
				//contentEditText.setText(end);
				//titleEditText.setText(path);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
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
