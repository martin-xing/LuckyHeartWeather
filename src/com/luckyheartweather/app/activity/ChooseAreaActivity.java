package com.luckyheartweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.luckyheartweather.app.R;
import com.luckyheartweather.app.model.City;
import com.luckyheartweather.app.model.County;
import com.luckyheartweather.app.model.LuckyHeartWeatherDB;
import com.luckyheartweather.app.model.Province;
import com.luckyheartweather.app.util.HttpCallbackListener;
import com.luckyheartweather.app.util.HttpUtil;
import com.luckyheartweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private LuckyHeartWeatherDB luckyHeartWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	
	//province list
	private List<Province> provinceList;
	//city list
	private List<City> cityList;
	//county list
	private List<County> countyList;
	
	//selected province
	private Province selectedProvince;
	//selected city
	private City selectedCity;

	//selected level
	private int currentLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		
		luckyHeartWeatherDB = LuckyHeartWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(currentLevel == LEVEL_PROVINCE){
					selectedProvince = provinceList.get(position);
					queryCities();
				}else if(currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(position);
					queryCounties();
				}
			}
			
		});
		
		//load province data list
		queryProvinces();
		
	}

	/**
	 * Query all provinces of the country. 
	 * Query the data from local database first, then from
	 * online server if no data found in local database.
	 */
	private void queryProvinces() {
		provinceList = luckyHeartWeatherDB.loadProvinces();
		if(provinceList.size() > 0){
			dataList.clear();
			for(Province province : provinceList){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("ÖÐ¹ú");
			currentLevel = LEVEL_PROVINCE;			
		}else{
			queryFromServer(null, "province");
		}
		
	}
	
	/**
	 * Query all cities of the province. 
	 * Query the data from local database first, then from
	 * online server if no data found in local database.
	 */
	protected void queryCities() {
		cityList = luckyHeartWeatherDB.loadCities(selectedProvince.getId());
		if(cityList.size() > 0){
			dataList.clear();
			for(City city : cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	
	/**
	 * Query all counties of the city. 
	 * Query the data from local database first, then from
	 * online server if no data found in local database.
	 */
	protected void queryCounties() {
		countyList = luckyHeartWeatherDB.loadCounties(selectedCity.getId());
		if(countyList.size() > 0){
			dataList.clear();
			for(County county : countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		}else{
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}

	/**
	 * Query province, city or county data from online server according
	 * to the place code and type passed.
	 * @param code
	 * @param type
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){

			@Override
			public void onFinish(String response) {
				boolean result = false;
				if("province".equals(type)){
					result = Utility.handleProvincesResponse(luckyHeartWeatherDB, response);
				}else if("city".equals(type)){
					result = Utility.handleCitiesResponse(luckyHeartWeatherDB, response, selectedProvince.getId());
				}else if("county".equals(type)){
					result = Utility.handleCountiesResponse(luckyHeartWeatherDB, response, selectedCity.getId());
				}
				
				if(result){
					//Goes back to main thread to handle UI operation through runOnUiThread() method
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							closeProgressDialog();
							if("province".equals(type)){
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();
							}else if("county".equals(type)){
								queryCounties();
							}
								
						}						
						
					});
				}
					
			}

			@Override
			public void onError(Exception e) {
				//Goes back to main thread to handle UI operation through runOnUiThread() method
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "Failed loading...", Toast.LENGTH_SHORT).show();
					}
					
				});
				
			}
			
		});
	}

	/**
	 * Displays the progress dialog
	 */
	private void showProgressDialog() {
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("Loading...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * Closes the progress dialog
	 */
	private void closeProgressDialog() {
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}

	/**
	 * Handles the back press operation and decides to go back
	 * to city list, province list or exit application according 
	 * to the current level.
	 */
	@Override
	public void onBackPressed() {
		if(currentLevel == LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel == LEVEL_CITY){
			queryProvinces();
		}else{
			finish();
		}
	}
	
	
}
