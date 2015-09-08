package com.example.handsswjtu;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mobstat.StatService;
import com.handsSwjtu.MyView.MyMessageBox;
import com.handsSwjtu.Objects.UserInfoEntity;
import com.handsSwjtu.common.CommonOnTouchListener;
import com.handsSwjtu.common.DataProvider;
import com.handsSwjtu.common.SharePreferenceHelper;
import com.handsSwjtu.httpConnect.HttpConnect;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SearchUserActivity extends Activity {

	private EditText searchEditText;
	private RelativeLayout searchBtn;
	private String searchString;
	private String errorString;
	private ProgressBar loadingProgressBar;
	private List<UserInfoEntity> searchUserResultEntities;
	private List<NameValuePair> params;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_user);
		searchEditText = (EditText) findViewById(R.id.searchEdit);
		searchBtn = (RelativeLayout) findViewById(R.id.searchUserBtn);
		searchBtn.setOnClickListener(onClickListener);
		loadingProgressBar = (ProgressBar) findViewById(R.id.loading);
		loadingProgressBar.setVisibility(View.INVISIBLE);
		setResult(RESULT_OK);
		
		errorString = new String();

	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			loadingProgressBar.setVisibility(View.VISIBLE);
			if (searchEditText.getText().toString().length()<2) {
				Toast.makeText(SearchUserActivity.this, "请至少输入两个字符", Toast.LENGTH_SHORT).show();
				loadingProgressBar.setVisibility(View.INVISIBLE);
			}else {
				params=new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("searchString", Base64.encodeToString(searchEditText.getText().toString().getBytes(),Base64.DEFAULT)));
					//searchString = (new JSONObject()).put("searchString", searchEditText.getText().toString()).toString();
				
				loadData();
			}

		}
	};

	public void loadData() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				searchUserResultEntities = new ArrayList<UserInfoEntity>();
				handler.sendEmptyMessage((DataProvider.searchUserEntityProvider(searchUserResultEntities, params,
						errorString)));

			}
		}).start();
	}

	public void initvalues() {

		
		LinearLayout searchUserResultTotal = (LinearLayout) findViewById(R.id.searchRestult);
		searchUserResultTotal.removeAllViews();
		for (int i = 0; i < searchUserResultEntities.size(); i++) {
			SharePreferenceHelper sharePreferenceHelper = new SharePreferenceHelper();
			String stuCodeNow = sharePreferenceHelper.getStuCode();
			if (!stuCodeNow.equals(searchUserResultEntities.get(i).getStuCode())) {

				final View searchUserResultView = View
						.inflate(SearchUserActivity.this, R.layout.list_search_user, null);
				((TextView) searchUserResultView.findViewById(R.id.stuName)).setText(searchUserResultEntities.get(i)
						.getStuName());
				((TextView) searchUserResultView.findViewById(R.id.stuCode)).setText(searchUserResultEntities.get(i)
						.getStuCode());
				((TextView) searchUserResultView.findViewById(R.id.stuCollege)).setText(searchUserResultEntities.get(i)
						.getStuCollege());
				final String stuName = searchUserResultEntities.get(i).getStuName();
				final String stuCode= searchUserResultEntities.get(i).getStuCode();
				searchUserResultView.setOnTouchListener(new CommonOnTouchListener());
				searchUserResultView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// MyMessageBox myMessageBox=new
						// MyMessageBox(SearchUserActivity.this, "是否请求发起会话",
						// MainActivity.class);
						// myMessageBox.show();
						LinearLayout confirmLayout = (LinearLayout) getLayoutInflater().inflate(
								R.layout.my_message_box, null);
						final Dialog alertDialog = new Dialog(SearchUserActivity.this, R.style.dialog);
						alertDialog.setContentView(confirmLayout);

						TextView messageBoxContent = ((TextView) confirmLayout.findViewById(R.id.messageBoxContent));
						messageBoxContent.setText("开启会话？");
						final RelativeLayout positiveButton = (RelativeLayout) confirmLayout
								.findViewById(R.id.positiveButton);
						RelativeLayout negativeButton = (RelativeLayout) confirmLayout
								.findViewById(R.id.negativeButton);
						positiveButton.setOnTouchListener(new CommonOnTouchListener());
						negativeButton.setOnTouchListener(new CommonOnTouchListener());
						positiveButton.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								// Intent it = new
								// Intent(SearchUserActivity.this,
								// DeanOfficeLoginActivity.class);
								// startActivity(it);
								String stuCode = ((TextView) searchUserResultView.findViewById(R.id.stuCode)).getText()
										.toString();
								Intent it = new Intent(SearchUserActivity.this, SwjtuChatWithFriendActivity.class);
								it.putExtra("stuName", stuName);
								it.putExtra("stuCode", stuCode);
								startActivity(it);
//								alertDialog.dismiss();
//								loadingProgressBar.setVisibility(View.VISIBLE);
								finish();
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
				});
				searchUserResultTotal.addView(searchUserResultView);
			}
		}
	}

	// public void addNewFriend(final String stuCode) {
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// String resultString=HttpConnect.addNewFriend(stuCode);
	// if (resultString!=null) {
	// try {
	// JSONObject jsonObject=new JSONObject(resultString);
	// errorString=jsonObject.getString("errorMsg");
	// if (jsonObject.getInt("state")==0) {
	// addFriendHandler.sendEmptyMessage(0);
	// }else{
	// addFriendHandler.sendEmptyMessage(1);
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// addFriendHandler.sendEmptyMessage(2);
	// }
	// }else{
	// addFriendHandler.sendEmptyMessage(3);
	// }
	// }
	// }).start();
	// }
	//
	//
	//
	// Handler addFriendHandler=new Handler(){
	//
	// public void handleMessage(Message msg) {
	// switch (msg.what) {
	//
	// case 0:
	// Toast.makeText(SearchUserActivity.this, errorString,
	// Toast.LENGTH_SHORT).show();
	//
	// break;
	// case 1:
	// Toast.makeText(SearchUserActivity.this, errorString,
	// Toast.LENGTH_SHORT).show();
	// case 2:
	// Toast.makeText(SearchUserActivity.this, "数据传输失败",
	// Toast.LENGTH_SHORT).show();
	// break;
	// case 3:
	// Toast.makeText(SearchUserActivity.this, "联网失败，请重试",
	// Toast.LENGTH_SHORT).show();
	// break;
	// default:
	// break;
	// }
	// loadingProgressBar.setVisibility(View.INVISIBLE);
	// }
	//
	// };

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case 0:
				//Toast.makeText(SearchUserActivity.this, errorString, Toast.LENGTH_SHORT).show();
				initvalues();
				break;
			case 1:
				Toast.makeText(SearchUserActivity.this, "获取失败,需要对方用户曾在客户端上登录才可以搜索到哦，返回主界面右下角给ta分享吧！", Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Toast.makeText(SearchUserActivity.this, "数据传输失败", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(SearchUserActivity.this, "联网失败，请重试", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			loadingProgressBar.setVisibility(View.INVISIBLE);
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
