package com.luckyheartweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class LuckyHeartWeatherOpenHelper extends SQLiteOpenHelper {
	
	/**
	 * SQL statement of creating table Province
	 */
	public static final String CREATE_PROVINCE = "create table Province ("
								+ "id integer primary key autoincrement, "
								+ "province_name text, "
								+ "province_code text)";
	
	/**
	 * SQL statement of creating table City
	 */
	public static final String CREATE_CITY = "create table City ("
								+ "id integer primary key autoincrement, "
								+ "city_name text, "
								+ "city_code text, "
								+ "province_id integer)";
	
	/**
	 * SQL statement of creating table County
	 */
	public static final String CREATE_COUNTY = "create table County ("
								+ "id integer primary key autoincrement, "
								+ "county_name text, "
								+ "county_code text, "
								+ "city_id integer)";

	public LuckyHeartWeatherOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_PROVINCE);  //Creates table Province
		db.execSQL(CREATE_CITY);      //Creates table City
		db.execSQL(CREATE_COUNTY);    //Creates table County
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
