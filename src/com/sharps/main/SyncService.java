package com.sharps.main;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import Database.MySQLiteHelper;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.sharps.R;
import com.sharps.Network.GamesDownloader;

public class SyncService extends Service implements Observer {

	private SQLiteDatabase database;
	private int amountOfSheets = 0;
	private int sheetsDoneDownloading = 0;
	private int amountOfNewGames = 0;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		database = ((MyApplication) getApplication()).getDatabase();
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				ArrayList<String> sheetID = getAllSheetID();
				amountOfSheets = sheetID.size();
				for (String string : sheetID) {
					downloadGames(0, string);
				}
			}
		}, 0, 600000);
	}

	private void generateNotification() {
		NotificationManager notificationManager = (NotificationManager) getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_home;
		Intent notificationIntent = new Intent(getApplicationContext(),
				SpreadsheetActivity.class);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(
				getApplicationContext(), 0, notificationIntent, 0);
		Uri defaultSound = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		Notification notification = new NotificationCompat.Builder(
				getApplicationContext()).setContentTitle("Notification")
				.setContentText("Nya spel").setContentIntent(intent)
				.setSmallIcon(icon).setLights(Color.YELLOW, 1, 2)
				.setAutoCancel(true).setSound(defaultSound).build();

		notificationManager.notify(0, notification);
	}

	private synchronized void downloadGames(final int page, final String sheetID) {
		GamesDownloader downloader = new GamesDownloader(database,
				"http://www.sharps.se/forums/includes/ss/app_games.php?ssid="
						+ sheetID + "&page=" + page);
		downloader.addObserver(this);
	}

	private synchronized ArrayList<String> getAllSheetID() {
		ArrayList<String> sheetID = new ArrayList<String>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_SPREADSHEETS,
				new String[] { MySQLiteHelper.COLUMN_SHEETID, }, null, null,
				null, null, null);
		while (cursor.moveToNext()) {
			String str = cursor.getString(cursor
					.getColumnIndex(MySQLiteHelper.COLUMN_SHEETID));
			sheetID.add(str);
		}
		return sheetID;
	}

	@Override
	public synchronized void update(Observable observable, Object data) {
		amountOfNewGames += (Integer) data;
		if (amountOfSheets == sheetsDoneDownloading) {
			generateNotification();
		} else {
			sheetsDoneDownloading++;
		}
	}
}
