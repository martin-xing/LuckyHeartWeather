package com.luckyheartweather.app.util;

import com.luckyheartweather.app.model.City;
import com.luckyheartweather.app.model.County;
import com.luckyheartweather.app.model.LuckyHeartWeatherDB;
import com.luckyheartweather.app.model.Province;

import android.text.TextUtils;

public class Utility {

	/**
	 * Gets province list data from online server and save them to local database
	 */
	public synchronized static boolean handleProvincesResponse(LuckyHeartWeatherDB luckyHeartWeatherDB,
			String response){
		
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(",");
			if(allProvinces != null && allProvinces.length > 0){
				for(String p : allProvinces){
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//Stores province list data to table province in local database
					luckyHeartWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 *Gets city list data from online server and save them to local database 
	 */
	public static boolean handleCitiesResponse(LuckyHeartWeatherDB luckyHeartWeatherDB, 
			String response, int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities = response.split(",");
			if(allCities != null && allCities.length > 0){
				for(String c : allCities){
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//Stores city list data to table city in local database
					luckyHeartWeatherDB.saveCity(city);
				}
				return true;
			}
		}		
		
		return false;
	}
	
	/**
	 * Gets county list data from online server and save them to local database
	 */
	public static boolean handleCountiesResponse(LuckyHeartWeatherDB luckyHeartWeatherDB,
			String response, int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties = response.split(",");
			if(allCounties != null && allCounties.length > 0){
				for(String c : allCounties){
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					//Stores county list data to table county in local database
					luckyHeartWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
