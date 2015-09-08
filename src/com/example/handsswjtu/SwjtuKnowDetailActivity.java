package com.example.handsswjtu;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mobstat.StatService;
import com.handsSwjtu.MyView.MyMessageBox;
import com.handsSwjtu.Objects.SwjtuKnowEntity;
import com.handsSwjtu.Objects.SwjtuKnowAnswerEntity;
import com.handsSwjtu.Objects.SwjtuKnowAnswerReplyEntity;
import com.handsSwjtu.common.BtnOnTouchListener;
import com.handsSwjtu.common.CommonOnTouchListener;
import com.handsSwjtu.common.DataProvider;
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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SwjtuKnowDetailActivity extends Activity {

	private ProgressBar loadingProgressBar;
	private ScrollView bodyScrollView;
	private SwjtuKnowEntity swjtuKnowEntity;
	private List<SwjtuKnowAnswerEntity> swjtuKnowAnswerList;
	private List<List<SwjtuKnowAnswerReplyEntity>> swjtuKnowAnswerReplyLists;
	private int questionId;
	private TextView questionTitleTextView;
	private TextView questionContentTextView;
	private TextView questionFromTextView;
	private TextView questionCtimeTextView;
	private LinearLayout answersLayout;
	private LayoutInflater inflater;
	private EditText myAnswerEditText;
	private RelativeLayout myAnswerBtn;
	private String answerContent;
	private String errorString;
	private List<NameValuePair> params;
	private String replyContent;
	private Map<String, List<SwjtuKnowAnswerReplyEntity>> swjtuKnowAnswerReplyMap;
	private LinearLayout questionBodyLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_swjtu_know_detail);
		questionBodyLayout = (LinearLayout) findViewById(R.id.questionBody);
		loadingProgressBar = (ProgressBar) findViewById(R.id.loading);
		bodyScrollView=(ScrollView)findViewById(R.id.bodyScrollView);
		questionTitleTextView = (TextView) findViewById(R.id.secondClassTitle);
		questionContentTextView = (TextView) findViewById(R.id.questionContent);
		questionFromTextView = (TextView) findViewById(R.id.Presenter);
		questionCtimeTextView = (TextView) findViewById(R.id.questionCtime);
		myAnswerEditText = (EditText) findViewById(R.id.searchEdit);
		myAnswerBtn = (RelativeLayout) findViewById(R.id.searchBtn);
		myAnswerBtn.setOnTouchListener(new BtnOnTouchListener());
		myAnswerBtn.setOnClickListener(onClickListener);
		answersLayout = (LinearLayout) findViewById(R.id.answers);
		// loadingProgressBar.setVisibility(View.INVISIBLE);
		swjtuKnowEntity = new SwjtuKnowEntity();
		questionId = getIntent().getExtras().getInt("id");
		//Toast.makeText(SwjtuKnowDetailActivity.this, String.valueOf(questionId), Toast.LENGTH_SHORT).show();
		inflater = LayoutInflater.from(SwjtuKnowDetailActivity.this);

		loadData();

	}

	public void loadData() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				swjtuKnowAnswerList = new ArrayList<SwjtuKnowAnswerEntity>();
				swjtuKnowAnswerReplyLists = new ArrayList<List<SwjtuKnowAnswerReplyEntity>>();
				swjtuKnowAnswerReplyMap = new HashMap<String, List<SwjtuKnowAnswerReplyEntity>>();
				if (DataProvider.swjtuKnowDetailProvider(swjtuKnowEntity, swjtuKnowAnswerList, swjtuKnowAnswerReplyMap,
						questionId)) {
					handler.sendEmptyMessage(1);
				} else {

				}
				;
			}
		}).start();
	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SharePreferenceHelper sharePreferenceHelper=new SharePreferenceHelper();
			if (!sharePreferenceHelper.isHaveLogged()) {
				MyMessageBox myMessageBox = new MyMessageBox(SwjtuKnowDetailActivity.this, "该操作需要登录哦！",
						IndividualCenterLoginActivity.class);
				myMessageBox.show();
			}else {
				loadingProgressBar.setVisibility(View.VISIBLE);
				answerContent = myAnswerEditText.getText().toString();
				params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("content",
						Base64.encodeToString(answerContent.getBytes(), Base64.DEFAULT)));
				params.add(new BasicNameValuePair("questionId", String.valueOf(questionId)));
				params.add(new BasicNameValuePair("userNow",(new SharePreferenceHelper()).getStuCode()));
				addAnswer();
			}

		}
	};

	public void addAnswer() {
		setResult(RESULT_OK);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String resultString = HttpConnect.addSwjtuKnowAnswer(params);
				if (resultString != null) {
					try {
						JSONObject jsonObject = new JSONObject(resultString);
						errorString = jsonObject.getString("errorMsg");
						if (jsonObject.getInt("state") == 0) {
							addQuestionHandler.sendEmptyMessage(0);
						} else {
							addQuestionHandler.sendEmptyMessage(1);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						addQuestionHandler.sendEmptyMessage(2);
					}

				} else {
					addQuestionHandler.sendEmptyMessage(4);
				}
			}
		}).start();
	}

	public void initValue() {

		questionTitleTextView.setText(swjtuKnowEntity.getTitle());
		questionContentTextView.setText(swjtuKnowEntity.getContent());
		questionFromTextView.setText(swjtuKnowEntity.getQuestionFrom());
		questionCtimeTextView.setText(swjtuKnowEntity.getCtime());
		// JSONObject jsonObject=new JSONObject(swjtuKnowEntity.getImagesUrl());
		for (int i = 0; i < swjtuKnowEntity.getQuestionImgFiles().size(); i++) {
			ImageView imageView = new ImageView(SwjtuKnowDetailActivity.this);
			Options options = new Options();
			final String filePath = Environment.getExternalStorageDirectory() + "/handsSwjtu/swjtu_know/thumbs/"
					+ swjtuKnowEntity.getQuestionImgFiles().get(i);
			Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
			if (bitmap==null) {
				bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.img_load_error);
			}
			imageView.setImageBitmap(bitmap);
			questionBodyLayout.addView(imageView);
			/*
			 * ((LinearLayout)
			 * NewsDetailActivity.this.findViewById(R.id.newsContent
			 * )).addView(imageView, new LinearLayout.LayoutParams(50, 150));
			 */
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
			WindowManager wm = (WindowManager) (SwjtuKnowDetailActivity.this.getSystemService(Context.WINDOW_SERVICE));
			int width = wm.getDefaultDisplay().getWidth();
			layoutParams.width = width - 150;
			layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
			int a = bitmap.getHeight();
			layoutParams.topMargin = 20;
			layoutParams.height = (int) ((float) ((float) (layoutParams.width / (float) bitmap.getWidth())) * (float) (bitmap
					.getHeight()));
			imageView.setLayoutParams(layoutParams);
			final int j=i;
			imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent it=new Intent(Intent.ACTION_VIEW);
					Uri uri=Uri.parse(HttpConnect.SWJTU_KNOW_IMAGE_BASE_PATH+swjtuKnowEntity.getQuestionImgFiles().get(j));
					it.setDataAndType(uri, "image/*");
					startActivity(it);
				}
			});

		}
		// URL uri=new
		// URL(HttpConnect.SWJTU_KNOW_IMAGE_BASE_PATH+jsonArray.optString(0));
		// ImageView imageView=new ImageView(SwjtuKnowDetailActivity.this);
		// imageView.setImageURI(uri);
		for (int i = 0; i < swjtuKnowAnswerList.size(); i++) {
			View answerView = inflater.inflate(R.layout.list_swjtu_know_answer, null);
			// TextView
			// answerFromtTextView=(TextView)answerView.findViewById(R.id.answerFrom);
			((TextView) answerView.findViewById(R.id.answerFrom)).setText(swjtuKnowAnswerList.get(i).getAnswerFrom());
			((TextView) answerView.findViewById(R.id.ctime)).setText(swjtuKnowAnswerList.get(i).getCtime());
			((TextView) answerView.findViewById(R.id.answerContent)).setText(swjtuKnowAnswerList.get(i)
					.getAnswerContent());
			((TextView) answerView.findViewById(R.id.answerFrom)).setText(swjtuKnowAnswerList.get(i).getAnswerFrom());
			((RelativeLayout) answerView.findViewById(R.id.myReplyBtn)).setOnTouchListener(new BtnOnTouchListener());
			final String answerId = String.valueOf(swjtuKnowAnswerList.get(i).getAnswerId());
			final TextView myReplyEditText = (EditText) answerView.findViewById(R.id.myReplyEdit);
			((RelativeLayout) answerView.findViewById(R.id.myReplyBtn)).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SharePreferenceHelper sharePreferenceHelper=new SharePreferenceHelper();
					if (!sharePreferenceHelper.isHaveLogged()) {
						MyMessageBox myMessageBox = new MyMessageBox(SwjtuKnowDetailActivity.this, "该操作需要登录哦！",
								IndividualCenterLoginActivity.class);
						myMessageBox.show();
					}else {
						loadingProgressBar.setVisibility(View.VISIBLE);
						String replyContent = Base64.encodeToString(myReplyEditText.getText().toString().getBytes(),
								Base64.DEFAULT);
						params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("content", replyContent));
						params.add(new BasicNameValuePair("answerId", answerId));
						params.add(new BasicNameValuePair("userNow",(new SharePreferenceHelper()).getStuCode()));
						addAnswerReply();
					}

				}
			});
			// answerView.setLayoutParams(new
			// LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT))));
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			lp.topMargin = 15;
			lp.leftMargin = 10;
			lp.rightMargin = 10;
			LinearLayout answerReplysLayout = (LinearLayout) answerView.findViewById(R.id.answerReplys);
			if (swjtuKnowAnswerReplyMap.containsKey("reply" + i)) {

				List<SwjtuKnowAnswerReplyEntity> swjtuKnowAnswerReplyEntities = swjtuKnowAnswerReplyMap
						.get("reply" + i);

				for (int j = 0; j < swjtuKnowAnswerReplyEntities.size(); j++) {
					View answerReplyView = inflater.inflate(R.layout.list_swjtu_know_answer_reply, null);
					((TextView) answerReplyView.findViewById(R.id.replyFrom)).setText(swjtuKnowAnswerReplyEntities.get(
							j).getReplyFrom());
					((TextView) answerReplyView.findViewById(R.id.replyContent)).setText(swjtuKnowAnswerReplyEntities
							.get(j).getReplyContent());
					((TextView) answerReplyView.findViewById(R.id.replyCtime)).setText(swjtuKnowAnswerReplyEntities
							.get(j).getCtime());
					answerReplysLayout.addView(answerReplyView);
				}
			}
			answerView.setLayoutParams(lp);
			answersLayout.addView(answerView);
			bodyScrollView.fullScroll(ScrollView.FOCUS_UP);
		}
	}

	public void addAnswerReply() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				String resultString = HttpConnect.addSwjtuKnowAnswerReply(params);
				// String resultString = HttpConnect.addSwjtuKnowAnswer(params);
				if (resultString != null) {
					try {
						JSONObject jsonObject = new JSONObject(resultString);
						errorString = jsonObject.getString("errorMsg");
						if (jsonObject.getInt("state") == 0) {
							addQuestionHandler.sendEmptyMessage(0);
						} else {
							addQuestionHandler.sendEmptyMessage(1);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						addQuestionHandler.sendEmptyMessage(2);
					}

				} else {
					addQuestionHandler.sendEmptyMessage(4);
				}
			}
		}).start();
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				initValue();
				break;
			case 2:
				Toast.makeText(SwjtuKnowDetailActivity.this, "联网失败，请重试", Toast.LENGTH_SHORT).show();
			default:
				break;
			}
			loadingProgressBar.setVisibility(View.INVISIBLE);
		}

	};

	Handler addQuestionHandler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case 0:
				Toast.makeText(SwjtuKnowDetailActivity.this, errorString, Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(SwjtuKnowDetailActivity.this, errorString, Toast.LENGTH_SHORT).show();
			case 2:
				Toast.makeText(SwjtuKnowDetailActivity.this, "数据传输异常", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(SwjtuKnowDetailActivity.this, "联网失败，请重试", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			loadingProgressBar.setVisibility(View.INVISIBLE);
			Intent it = new Intent(SwjtuKnowDetailActivity.this, SwjtuKnowDetailActivity.class);
			it.putExtra("id", questionId);
			startActivity(it);
			finish();
		}
	};

	Handler addAnswerReplyHandler = new Handler() {

		public void handleMessage(Message msg) {

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
