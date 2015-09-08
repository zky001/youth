package com.example.handsswjtu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mobstat.StatService;
import com.handsSwjtu.common.DifferentColorListViewAdapter;
import com.handsSwjtu.httpConnect.HttpConnect;
import com.handsSwjtu.Objects.BookInfo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.R.bool;
import android.R.string;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class BookListActivity extends Activity {
	private String txtPageIndex = "1";
	private String order = "正序";
	private String txtSearchContent = "业余";
	private String ddlSearchField = "TITLE";
	private String DropDownList_Sort = "TITLE";
	private String ddlDistribution = "";
	private String ddlBookType = "";
	private TextView textViewSearchBooksResult;
	private ProgressBar processBarLoadingSearchBooks;
	private DifferentColorListViewAdapter adapter;
	private ListView listViewSearchBooksResult;
	private int pageNow = 1;
	private List<Map<String, Object>> bookInfos;
	private int loadingFlag = 0;
	private boolean NO_MORE_DATA = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_books_list);
		processBarLoadingSearchBooks = (ProgressBar) findViewById(R.id.loadingSearchBook);
		listViewSearchBooksResult = (ListView) findViewById(R.id.searchBooksResult);
		Bundle bundle = getIntent().getBundleExtra("searchBooksData");
		ddlSearchField = bundle.getString("ddlSearchField");
		DropDownList_Sort = bundle.getString("DropDownList_Sort");
		order = bundle.getString("order");
		txtSearchContent = bundle.getString("txtSearchContent");
		ddlDistribution = bundle.getString("ddlDistribution");
		ddlBookType = bundle.getString("ddlBookType");
		bookInfos = new ArrayList<Map<String, Object>>();
		/*
		 * Map<String,Object>title=new HashMap<String,Object>(); title.put("Id",
		 * "序号"); title.put("sysid", "书目号"); title.put("bookName", "书名");
		 * title.put("author", "作者"); title.put("publisher", "出版社");
		 * bookInfos.add(title);
		 */

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				String searchBooksResult = HttpConnect.getBookListFromServer(
						ddlSearchField, DropDownList_Sort, order,
						txtSearchContent, ddlDistribution, ddlBookType,
						String.valueOf(pageNow));
				Message msg = new Message();
				try {

					if (!searchBooksResult.equals("null")) {
						Bundle bundle = new Bundle();
						bundle.putString("searchBooksResult", searchBooksResult);
						msg.what = 1;
						msg.setData(bundle);
					} else {
						msg.what = 4;
					}
					handle.sendMessage(msg);
				} catch (Exception e) {
					Log.i("errorjson", e.toString());
				}
			}
		}).start();

		listViewSearchBooksResult.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						& view.getLastVisiblePosition() == (view.getCount() - 1)
						&& loadingFlag == 0) {
					if (!NO_MORE_DATA) {
						pageNow++;
						loadingFlag = 1;
						processBarLoadingSearchBooks
								.setVisibility(View.VISIBLE);
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub

								loadMoreData();
								handle.sendEmptyMessage(2);

							}

							//
						}).start();
					} else {
						handle.sendEmptyMessage(3);
						// Toast.makeText(LibrarySearchBooks.this, "没有更多数据了哟",
						// Toast.LENGTH_LONG).show();
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
			}
		});

		listViewSearchBooksResult
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View arg1,
							int position, long arg3) {
						// TODO Auto-generated method stub
						ListView listView = (ListView) parent;
						HashMap<String, String> map = (HashMap<String, String>) listView
								.getItemAtPosition(position);
						String sysid = map.get("sysid");
						String bookId = map.get("bookId");
						String bookName = map.get("bookName");
						String author = map.get("author");
						String publisher = map.get("publisher");
						//Toast.makeText(BookListActivity.this, sysid, 2000).show();
						Intent it = new Intent(BookListActivity.this,
								BookDetailActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("sysid", sysid);
						bundle.putString("bookName", bookName);
						bundle.putString("bookId", bookId);
						bundle.putString("author", author);
						bundle.putString("publisher", publisher);
						it.putExtra("bookDetail", bundle);
						startActivity(it);

					}
				});

	}

	public void loadMoreData() {

		String searchBooksResult = HttpConnect.getBookListFromServer(
				ddlSearchField, DropDownList_Sort, order, txtSearchContent,
				ddlDistribution, ddlBookType, String.valueOf(pageNow));
		if (!searchBooksResult.equals("null")) {
			try {
				JSONArray jsonArray = new JSONArray(searchBooksResult);
				for (int i = 0; i < jsonArray.length(); i++) {
					Map<String, Object> bookInfo = new HashMap<String, Object>();
					JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
					bookInfo.put("Id", (pageNow - 1) * 30 + i + 1);
					bookInfo.put("sysid", jsonObject2.getString("sysid"));
					bookInfo.put("bookId", jsonObject2.getString("bookId"));
					bookInfo.put("bookName", jsonObject2.getString("bookName"));
					bookInfo.put("author", jsonObject2.getString("author"));
					bookInfo.put("publisher",
							jsonObject2.getString("publisher"));
					bookInfos.add(bookInfo);
					if (jsonArray.length()<30) {
						NO_MORE_DATA=true;
					}
				}
				// adapter=new
				// SimpleAdapter(LibrarySearchBooks.this,bookInfos,R.layout.listview_library_search_books,new
				// String[]{"bookId","bookName","author","publisher"},
				// new
				// int[]{R.id.bookId,R.id.bookName,R.id.author,R.id.publisher});
				// listViewSearchBooksResult.setAdapter(adapter);

			} catch (Exception e) {
				Log.i("error", e.toString());
			}
		} else {
			Toast.makeText(this, "加载错误", 1000);
		}

	}

	Handler handle = new Handler() {

		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				String searchBooksResult = msg.getData().getString(
						"searchBooksResult");
				if (!searchBooksResult.equals("null")) {
					try {
						JSONArray jsonArray = new JSONArray(searchBooksResult);

						for (int i = 0; i < jsonArray.length(); i++) {
							Map<String, Object> bookInfo = new HashMap<String, Object>();
							JSONObject jsonObject2 = (JSONObject) jsonArray
									.opt(i);
							bookInfo.put("Id", (pageNow - 1) * 30 + i + 1);
							bookInfo.put("sysid",
									jsonObject2.getString("sysid"));
							bookInfo.put("bookId",
									jsonObject2.getString("bookId"));
							bookInfo.put("bookName",
									jsonObject2.getString("bookName"));
							bookInfo.put("author",
									jsonObject2.getString("author"));
							bookInfo.put("publisher",
									jsonObject2.getString("publisher"));
							bookInfos.add(bookInfo);
						}
						adapter = new DifferentColorListViewAdapter(
								BookListActivity.this, bookInfos,
								R.layout.listview_library_search_books,
								new String[] { "sysid", "Id", "bookId",
										"bookName", "author", "publisher" },
								new int[] { R.id.sysid, R.id.secondClassId, R.id.bookId,
										R.id.bookName, R.id.author,
										R.id.publisher });
						listViewSearchBooksResult.setAdapter(adapter);
						processBarLoadingSearchBooks
								.setVisibility(View.INVISIBLE);
						if (jsonArray.length() < 30) {
							NO_MORE_DATA = true;
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
				}
			} else if (msg.what == 2) {
				adapter.notifyDataSetChanged();
				processBarLoadingSearchBooks.setVisibility(View.INVISIBLE);
				loadingFlag = 0;
			} else if (msg.what == 3) {
				Toast.makeText(BookListActivity.this, "没有更多书籍了哟",
						Toast.LENGTH_LONG).show();
				processBarLoadingSearchBooks.setVisibility(View.INVISIBLE);
			} else {
				Toast.makeText(BookListActivity.this, "没有查询到结果", 1000).show();
				processBarLoadingSearchBooks.setVisibility(View.INVISIBLE);
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
