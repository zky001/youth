package com.example.handsswjtu;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mobstat.StatService;
import com.handsSwjtu.Objects.SecondClassDetailEntity;
import com.handsSwjtu.common.BtnOnTouchListener;
import com.handsSwjtu.common.CommonOnTouchListener;
import com.handsSwjtu.common.DataProvider;
import com.handsSwjtu.httpConnect.HttpConnect;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SecondClassMyCourseDetailActivity extends Activity {

	private String courseId;
	private SecondClassDetailEntity secondClassDetailEntity;
	private ProgressBar loadingProgressBar;
	private RelativeLayout chooseClassBtn;
	private static int CHOOSE_COURSE_MESSAGE = 1;
	private static int LOAD_DATA_MESSAGE = 0;
	private StringBuffer message;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_second_class_my_course_detail);
		courseId = getIntent().getStringExtra("ID");
		secondClassDetailEntity = new SecondClassDetailEntity();
		loadingProgressBar = (ProgressBar) findViewById(R.id.loading);
		chooseClassBtn = (RelativeLayout) findViewById(R.id.chooseClassBtn);
		chooseClassBtn.setOnTouchListener(new BtnOnTouchListener());
		//chooseClassBtn.setOnClickListener(onClickListener);
		message=new StringBuffer();
		loadData();
	}

	public void loadData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putInt("messageType", LOAD_DATA_MESSAGE);
				msg.setData(bundle);
				msg.what = (DataProvider.secondClassDetailProvider(secondClassDetailEntity, message, courseId));
				handler.sendMessage(msg);
			}
		}).start();
	}

	public void initvalues() {
		((TextView) findViewById(R.id.CourseNameEx)).setText(secondClassDetailEntity.getCourseNameEx());
		((TextView) findViewById(R.id.BelongQiseCode)).setText(secondClassDetailEntity.getBelongQiseCode());
		((TextView) findViewById(R.id.TeachAddress)).setText(secondClassDetailEntity.getTeachAddress());
		((TextView) findViewById(R.id.MaxCapacity)).setText(secondClassDetailEntity.getMaxCapacity());
		((TextView) findViewById(R.id.TeachTime)).setText(secondClassDetailEntity.getTeachTime());
		((TextView) findViewById(R.id.SelectedCounts)).setText(secondClassDetailEntity.getSelectedCounts());
		((TextView) findViewById(R.id.XHours)).setText(secondClassDetailEntity.getXHours());
		((TextView) findViewById(R.id.DepID)).setText(secondClassDetailEntity.getDepID());
		((TextView) findViewById(R.id.Presenter)).setText(secondClassDetailEntity.getPresenter());
		((TextView) findViewById(R.id.CourseDetail)).setText(secondClassDetailEntity.getCourseDetail());
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case 0:
				Toast.makeText(SecondClassMyCourseDetailActivity.this, message, Toast.LENGTH_SHORT).show();
				if (msg.getData().getInt("messageType") == LOAD_DATA_MESSAGE) {
					initvalues();
				}else {
					setResult(RESULT_OK);
					finish();
				}
				break;
			case 1:
				Toast.makeText(SecondClassMyCourseDetailActivity.this, message, Toast.LENGTH_SHORT).show();
			case 2:
				Toast.makeText(SecondClassMyCourseDetailActivity.this, "数据传输失败", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(SecondClassMyCourseDetailActivity.this, "联网失败，请重试", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			loadingProgressBar.setVisibility(View.INVISIBLE);
		}
	};
	
	
	public void deleteCourse(final String courseId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putInt("messageType", CHOOSE_COURSE_MESSAGE);
				msg.setData(bundle);
				String resultString = HttpConnect.deleteCourse(courseId);
				if (resultString == null) {
					msg.what = 3;
				} else {
					try {
						JSONObject jsonObject = new JSONObject(resultString);
						message.delete(0, message.length());
						message = message.append(jsonObject.getString("Message"));
						if (jsonObject.getInt("State") != 0) {
							msg.what = 1;
						} else {
							msg.what = 0;
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg.what = 2;
					}
				}
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	
	public void deleteCourse(View view) {

		LinearLayout confirmLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.my_message_box, null);
		final Dialog alertDialog = new Dialog(SecondClassMyCourseDetailActivity.this, R.style.dialog);
		alertDialog.setContentView(confirmLayout);
		alertDialog.show();
		TextView messageBoxContent = ((TextView) confirmLayout.findViewById(R.id.messageBoxContent));
		messageBoxContent.setText("确定删除该门课程?");
		final RelativeLayout positiveButton = (RelativeLayout) confirmLayout.findViewById(R.id.positiveButton);
		RelativeLayout negativeButton = (RelativeLayout) confirmLayout.findViewById(R.id.negativeButton);
		positiveButton.setOnTouchListener(new CommonOnTouchListener());
		negativeButton.setOnTouchListener(new CommonOnTouchListener());
		positiveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				deleteCourse(courseId);
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
