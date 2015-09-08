package com.example.handsswjtu;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.PrivateCredentialPermission;

import org.json.JSONArray;
import org.json.JSONObject;













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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.handsSwjtu.common.Tools;
import com.handsSwjtu.httpConnect.HttpConnect;;;

public class BookDetailActivity extends Activity {

	
	private ListView listViewBookDetail;
	private String bookId;
	private String sysid;
	private String bookName;
	private String author;	
	private String publisher;
	private String refrenceNum;
	private String ISBN;
	private String bookDetailResult;
	private List<Map<String,Object>> bookDetails;
	private ProgressBar processBarLoadingBookDetail;
	private TextView textViewBookName;
	private TextView textViewBookId;
	private TextView textViewAuthor;
	private TextView textViewPublisher;
	private TextView textViewReferenceNum;
	private TextView textViewISBN;
	private ImageView imageViewBookImg;
	private SimpleAdapter adapter;
	private String bookImg;
	private Bitmap bitmap;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_book_detail);
		listViewBookDetail=(ListView)findViewById(R.id.bookDetailResult);
		textViewBookName=(TextView)findViewById(R.id.showBookname);
		textViewAuthor=(TextView)findViewById(R.id.showAuthor);
		textViewPublisher=(TextView)findViewById(R.id.showPublisher);
		textViewBookId=(TextView)findViewById(R.id.showBookId);
		textViewReferenceNum=(TextView)findViewById(R.id.showReferenceNum);
		textViewISBN=(TextView)findViewById(R.id.showISBN);
		processBarLoadingBookDetail=(ProgressBar)findViewById(R.id.loadingBookDetail);
		imageViewBookImg=(ImageView)findViewById(R.id.booksImg);
		Bundle bundle=getIntent().getBundleExtra("bookDetail");
		bookName=bundle.getString("bookName");
		bookId=bundle.getString("bookId");
		author=bundle.getString("author");
		publisher=bundle.getString("publisher");
		sysid=bundle.getString("sysid");
		textViewBookName.setText(bookName);
		textViewAuthor.setText(author);
		textViewPublisher.setText(publisher);
		textViewBookId.setText(bookId);
		//textViewReferenceNum.setText(bookName);
		//textViewBookName.setText(bookName);
		HttpConnect.isWap(this);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				bookDetailResult=HttpConnect.getBookDetailFromServer(sysid);
				handler.sendEmptyMessage(1);
				
			}
		}).start();
	}
	
	Handler handler=new Handler(){
		public void handleMessage(Message msg){
			if(msg.what==1){
				if(!bookDetailResult.equals("null")){
					try {
						bookImg=new JSONObject(bookDetailResult).getString("bookImg");
						refrenceNum=new JSONObject(bookDetailResult).getJSONObject("bookInfo").getString("referenceNum");
						ISBN=new JSONObject(bookDetailResult).getJSONObject("bookInfo").getString("ISBN");
						textViewReferenceNum.setText(refrenceNum);
						textViewISBN.setText(ISBN);
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								bitmap=Tools.loadBookImg(bookImg);
								handler.sendEmptyMessage(2);
							}
						}).start();
						
						JSONArray jsonArray = new JSONObject(bookDetailResult).getJSONArray("result");
						bookDetails=new ArrayList<Map<String,Object>>();
						for(int i=0;i<jsonArray.length();i++){
							Map<String,Object>bookDetail=new HashMap<String,Object>();
							JSONObject jsonObject2=(JSONObject)jsonArray.opt(i);
							bookDetail.put("code",jsonObject2.getString("code") );
							bookDetail.put("locate", jsonObject2.getString("locate"));
							bookDetail.put("type", jsonObject2.getString("type"));
							//bookDetail.put("dateIn", jsonObject2.getString("ty"));
							bookDetail.put("dateIn",jsonObject2.getString("dateIn"));
							bookDetail.put("borrowed",jsonObject2.getString("borrowed"));
							bookDetail.put("status",jsonObject2.getString("status"));
							bookDetail.put("appoint",jsonObject2.getString("appoint"));
							bookDetails.add(bookDetail);
					}
					adapter=new SimpleAdapter(BookDetailActivity.this,bookDetails,R.layout.listview_library_book_detail,new String[]{"code","locate","type","dateIn","borrowed","status","appoint"},
						new int[]{R.id.sportsplace,R.id.locate,R.id.type,R.id.dateIn,R.id.borrowed,R.id.status,R.id.appoint});
					listViewBookDetail.setAdapter(adapter);
					processBarLoadingBookDetail.setVisibility(View.INVISIBLE);
					}catch(Exception e){
						Log.i("error", e.toString());
					}
					processBarLoadingBookDetail.setVisibility(View.INVISIBLE);
			}
				
			}else if(msg.what==2){
					imageViewBookImg.setImageBitmap(bitmap);
				}
			
		}
		
	};
	
	
	public void onResume() {
		
		super.onResume();
		StatService.onResume(this);
	}

	public void onPause() {
		
		super.onPause();
		StatService.onPause(this);
	}


}
