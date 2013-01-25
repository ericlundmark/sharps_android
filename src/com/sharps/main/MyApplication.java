package com.sharps.main;

import Database.MySQLiteHelper;
import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class MyApplication extends Application {
	private MySQLiteHelper dbHelper;
	private SQLiteDatabase database;

	@Override
	public void onCreate() {
		dbHelper = new MySQLiteHelper(getApplicationContext());
		database = dbHelper.getWritableDatabase();
		WakefulIntentService.scheduleAlarms(new AppListener(),
                this.getApplicationContext(), false);
	}

	public SQLiteDatabase getDatabase() {
		return database;
	}
}
