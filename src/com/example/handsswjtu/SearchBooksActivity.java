package com.example.handsswjtu;



import com.baidu.mobstat.StatService;
import com.example.handsswjtu.R.drawable;










import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class SearchBooksActivity extends Activity {

	private Spinner spinnerDdlSearchField;
	private Spinner spinnerDropDownList_Sort;
	private Spinner spinnerDropDownList_OrderStyle;
	private Spinner spinnerDdlDistribution;
	private Spinner spinnerDdlBookType;
	private String txtSearchContent="";
	private String order="正序";
	private String DropDownList_Sort="TITLE";
	private String ddlDistribution="";
	private String ddlBookType="";
	private String ddlSearchField="正序";
	private RelativeLayout btnSearchBooksSubmit;
	private EditText editTextTxtSearchContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_books);
		spinnerDdlSearchField=(Spinner)findViewById(R.id.ddlSearchField);
		spinnerDropDownList_Sort=(Spinner)findViewById(R.id.DropDownList_Sort);
		spinnerDropDownList_OrderStyle=(Spinner)findViewById(R.id.DropDownList_OrderStyle);
		spinnerDdlDistribution=(Spinner)findViewById(R.id.ddlDistribution);
		spinnerDdlBookType=(Spinner)findViewById(R.id.ddlBookType);

		editTextTxtSearchContent=(EditText)findViewById(R.id.txtSearchContent);
			Log.i("error", "发生错误");
		
		String[] itemsDdlSearchField=getResources().getStringArray(R.array.ddlSearchField);
		String[] itemsDropDownList_Sort=getResources().getStringArray(R.array.DropDownList_Sort);
		String[] itemsDropDownList_OrderStyle=getResources().getStringArray(R.array.DropDownList_OrderStyle);
		String[] itemsDdlDistribution=getResources().getStringArray(R.array.ddlDistribution);
		String[] itemsDdlBookType=getResources().getStringArray(R.array.ddlBookType);
		ArrayAdapter<String> spinnerDdlSearchFieldAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, itemsDdlSearchField);
		ArrayAdapter<String> spinnerDropDownListSortAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, itemsDropDownList_Sort);
		ArrayAdapter<String> spinnerDropDownListOrderStyleAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, itemsDropDownList_OrderStyle);
		ArrayAdapter<String> spinnerDdlDistributionAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, itemsDdlDistribution);
		ArrayAdapter<String> spinnerDdlBookTypeAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, itemsDdlBookType);
		spinnerDdlSearchField.setAdapter(spinnerDdlSearchFieldAdapter);
		spinnerDropDownList_Sort.setAdapter(spinnerDropDownListSortAdapter);
		spinnerDropDownList_OrderStyle.setAdapter(spinnerDropDownListOrderStyleAdapter);
		spinnerDdlDistribution.setAdapter(spinnerDdlDistributionAdapter);
		spinnerDdlBookType.setAdapter(spinnerDdlBookTypeAdapter);
		btnSearchBooksSubmit=(RelativeLayout)findViewById(R.id.searchBooksSubmit);
		btnSearchBooksSubmit.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					btnSearchBooksSubmit.setBackgroundColor(Color.parseColor("#03b4fe"));
				}
				if(event.getAction() == MotionEvent.ACTION_UP){
					btnSearchBooksSubmit.setBackgroundResource(R.drawable.buttonunfocused);;
				}
				return false;
				
			}
		});
		btnSearchBooksSubmit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(v.getId()==R.id.searchBooksSubmit)
				{
				switch (spinnerDdlSearchField.getSelectedItemPosition()) {
				case 0:ddlSearchField="TITLE";break;
				case 1:ddlSearchField="AUTHOR";break;
				case 2:ddlSearchField="PUBLISHER";break;
				case 3:ddlSearchField="BRN";break;
				case 4:ddlSearchField="CNO";break;
				case 5:ddlSearchField="UNIFORM";break;
				default:
					break;
			}
				switch (spinnerDropDownList_Sort.getSelectedItemPosition()) {
				case 0:DropDownList_Sort="TITLE";break;
				case 1:DropDownList_Sort="SYS_FLD_SYSID";break;
				case 2:DropDownList_Sort="AUTHOR";break;
				case 3:DropDownList_Sort="PUBLISHER";break;
				default:
					break;
			}
	
				switch (spinnerDropDownList_OrderStyle.getSelectedItemPosition()) {
				case 0:order="正序";break;
				case 1:order="逆序";break;
				default:
					break;
			}
				
				
				switch (spinnerDdlDistribution.getSelectedItemPosition()) {
				case 0:ddlDistribution="";break;
				case 1:ddlDistribution="西南交通大学图书馆";break;
				case 2:ddlDistribution="峨眉校区图书馆";break;
				case 3:ddlDistribution="生命科学与工程学院";break;
				case 4:ddlDistribution="物流学院";break;
				case 5:ddlDistribution="艺传学院";break;
				case 6:ddlDistribution="政治学院";break;
				default:
					break;
			}
				
				switch (spinnerDdlBookType.getSelectedItemPosition()) {
				case 0:ddlBookType="";break;
				case 1:ddlBookType="CBK";break;
				case 2:ddlBookType="BK";break;
				case 3:ddlBookType="CSE";break;
				case 4:ddlBookType="SE";break;
				default:
					break;
				}
				}
				txtSearchContent=editTextTxtSearchContent.getText().toString();
				Bundle bundle=new Bundle();
				bundle.putString("ddlSearchField", ddlSearchField);
				bundle.putString("DropDownList_Sort",DropDownList_Sort);
				bundle.putString("order",order);
				bundle.putString("txtSearchContent",txtSearchContent);
				bundle.putString("ddlDistribution", ddlDistribution);
				bundle.putString("ddlBookType", ddlBookType);
				Intent it=new Intent(SearchBooksActivity.this,BookListActivity.class);
				it.putExtra("searchBooksData", bundle);
				startActivity(it);
				
				
			}
		});
		
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
