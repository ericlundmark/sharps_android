package com.sharps.main;

import Database.MySQLiteHelper;
import android.app.Application;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

public class MyApplication extends Application {
	private MySQLiteHelper dbHelper;
	private SQLiteDatabase database;

	@Override
	public void onCreate() {
		dbHelper = new MySQLiteHelper(getApplicationContext());
		database = dbHelper.getWritableDatabase();
		startService(new Intent(this, SyncService.class));
	}

	public SQLiteDatabase getDatabase() {
		return database;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		database.close();
	}

}
