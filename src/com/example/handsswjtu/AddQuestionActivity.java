package com.example.handsswjtu;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.baidu.mobstat.StatService;

import com.handsSwjtu.MyView.MyMessageBox;
import com.handsSwjtu.common.CommonOnTouchListener;
import com.handsSwjtu.common.FileUtils;
import com.handsSwjtu.common.SharePreferenceHelper;
import com.handsSwjtu.common.Tools;
import com.handsSwjtu.httpConnect.HttpConnect;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class AddQuestionActivity extends Activity {

	private final int FILE_SELECT_CODE = 1;
	private LinearLayout addImgBtn;
	private List<String> imageStrings;
	private EditText titleEditText;
	private EditText contentEditText;
	private LinearLayout lyImgPost;
	private int imgSize = 0;
	private TextView fileSizetTextView;
	private RelativeLayout submitBtn;
	private DecimalFormat df = new DecimalFormat("#.00");
	private String titleString;
	private String contentString;
	private List<NameValuePair> params;
	private int imgNum = 0;
	private String errorString;
	private ProgressBar loadingProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_question);
		addImgBtn = (LinearLayout) findViewById(R.id.addImgBtn);
		titleEditText = (EditText) findViewById(R.id.secondClassTitle);
		contentEditText = (EditText) findViewById(R.id.questionContent);
		lyImgPost = (LinearLayout) findViewById(R.id.imgPost);
		fileSizetTextView = (TextView) findViewById(R.id.fileSize);
		loadingProgressBar = (ProgressBar) findViewById(R.id.loading);
		fileSizetTextView.setVisibility(View.INVISIBLE);
		submitBtn = (RelativeLayout) findViewById(R.id.submit);
		submitBtn.setOnTouchListener(new CommonOnTouchListener());
		submitBtn.setOnClickListener(onClickListener);
		imageStrings = new ArrayList<String>();
		params = new ArrayList<NameValuePair>();
		addImgBtn.setOnClickListener(onClickListener);
		addImgBtn.setOnTouchListener(new CommonOnTouchListener());

	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.addImgBtn:
				Intent it = new Intent(Intent.ACTION_GET_CONTENT);
				it.setType("*/*");
				it.addCategory(Intent.CATEGORY_OPENABLE);
				startActivityForResult(Intent.createChooser(it, "选择所需要的图片"), FILE_SELECT_CODE);
				break;
			case R.id.submit:
				SharePreferenceHelper sharePreferenceHelper = new SharePreferenceHelper();

				titleString = titleEditText.getText().toString();
				contentString = contentEditText.getText().toString();
				if (!(titleString.length() != 0 && contentString.length() != 0)) {
					Toast.makeText(AddQuestionActivity.this, "标题和内容不能为空", Toast.LENGTH_SHORT).show();
				} else {

					if (sharePreferenceHelper.isHaveLogged()) {

						loadingProgressBar.setVisibility(View.VISIBLE);
						params.add(new BasicNameValuePair("title", Base64.encodeToString(titleString.getBytes(),
								Base64.DEFAULT)));
						params.add(new BasicNameValuePair("content", Base64.encodeToString(contentString.getBytes(),
								Base64.DEFAULT)));
						params.add(new BasicNameValuePair("totalImgNum", String.valueOf(imgNum)));
						params.add(new BasicNameValuePair("userNow", (new SharePreferenceHelper()).getStuCode()));
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								String resultString = HttpConnect.addSwjtuKnowQuestion(params);
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
						}).start();

					} else {
						MyMessageBox myMessageBox = new MyMessageBox(AddQuestionActivity.this, "该操作需要登录哦！",
								IndividualCenterLoginActivity.class);
						myMessageBox.show();
					}
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
				Toast.makeText(AddQuestionActivity.this, errorString, Toast.LENGTH_SHORT).show();
				setResult(RESULT_OK);
				finish();
				break;
			case 1:
				Toast.makeText(AddQuestionActivity.this, errorString, Toast.LENGTH_SHORT).show();
			case 2:
				Toast.makeText(AddQuestionActivity.this, "数据传输异常", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(AddQuestionActivity.this, "联网失败，请重试", Toast.LENGTH_SHORT).show();
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
				String path = FileUtils.getPath(AddQuestionActivity.this, uri);
				String end = path.substring(path.lastIndexOf(".") + 1, path.length()).toLowerCase();
				if ((end.equals("jpg") || end.equals("jpeg") || end.equals("gif") || end.equals("png"))) {
					ImageView imageView = new ImageView(this);
					LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					lp.height = 40;
					lp.width = 40;
					lp.rightMargin = 5;
					imageView.setLayoutParams(lp);
					BitmapFactory.Options options = new BitmapFactory.Options();
					// options.inJustDecodeBounds=false;
					if ((new File(path)).length() > 300000) {
						options.inSampleSize = (int) ((new File(path)).length() / 1000 / 300);
					}

					Bitmap bitmap = BitmapFactory.decodeFile(path, options);
					imageView.setImageBitmap(bitmap);
					lyImgPost.addView(imageView);
					try {
						String imgString = Tools.Bitmap2String(bitmap);
						params.add(new BasicNameValuePair("img" + String.valueOf(imgNum), imgString));
						imgNum += 1;
						imgSize += (int) (imgString).length();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					fileSizetTextView.setText("文件共" + df.format(((double) imgSize / 1000)) + "kb");
					fileSizetTextView.setVisibility(View.VISIBLE);
				} else {
					Toast.makeText(AddQuestionActivity.this, "您选择地文件格式有误，只支持jpg、gif、png格式图片上传", Toast.LENGTH_SHORT)
							.show();

				}
				// contentEditText.setText(end);
				// titleEditText.setText(path);
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
