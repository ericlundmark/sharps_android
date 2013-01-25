package com.sharps.main;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;

import Database.MySQLiteHelper;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Join;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.sharps.R;
import com.sharps.Network.GamesDownloader;

public class SyncService extends WakefulIntentService implements Observer {
	private SQLiteDatabase database;
	private int amountOfSheets = 0;
	private int sheetsDoneDownloading = 0;
	private int amountOfNewGames = 0;
	public static CookieStore cookieStore = new BasicCookieStore();

	public SyncService() {
		super("SyncService");
	}

	private void generateNotification(int i) {
		NotificationManager notificationManager = (NotificationManager) getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_launcher;
		Intent notificationIntent = new Intent(getApplicationContext(),
				SpreadsheetActivity.class);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		notificationIntent.putExtra("update", "1");
		PendingIntent intent = PendingIntent.getActivity(
				getApplicationContext(), 0, notificationIntent, 0);
		Uri defaultSound = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		Notification notification = new NotificationCompat.Builder(
				getApplicationContext()).setContentTitle("Notification")
				.setContentText(i + " nya spel").setContentIntent(intent)
				.setSmallIcon(icon).setLights(Color.YELLOW, 1, 2)
				.setAutoCancel(true).setSound(defaultSound).build();

		notificationManager.notify(0, notification);
	}

	private Thread downloadGames(final int page, final String sheetID) {
		GamesDownloader gamesDownloader = new GamesDownloader(database,
				sheetID, page);
		gamesDownloader.addObserver(this);
		Thread t=new Thread(gamesDownloader);
		t.start();
		return t;
	}

	private ArrayList<String> getAllSheetID() {
		ArrayList<String> sheetID = new ArrayList<String>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_SPREADSHEETS,
				new String[] { MySQLiteHelper.COLUMN_SHEETID, },
				MySQLiteHelper.COLUMN_OWNER + " = 0", null, null, null, null);
		while (cursor.moveToNext()) {
			String str = cursor.getString(cursor
					.getColumnIndex(MySQLiteHelper.COLUMN_SHEETID));
			sheetID.add(str);
		}
		cursor.close();
		return sheetID;
	}

	@Override
	public synchronized void update(Observable observable, Object data) {
		String[] updateData = ((String) data).split(":");
		amountOfNewGames += Integer.parseInt(updateData[1]);
		ContentValues contentValues = new ContentValues();
		contentValues.put(MySQLiteHelper.COLUMN_UNVIEWED_GAMES, updateData[1]);
		String where = MySQLiteHelper.COLUMN_SHEETID + " = " + updateData[0];
		database.update(MySQLiteHelper.TABLE_SPREADSHEETS, contentValues,
				where, null);
		sheetsDoneDownloading++;
		if (amountOfSheets == sheetsDoneDownloading) {
			if (amountOfNewGames > 0) {
				generateNotification(amountOfNewGames);
			}
			amountOfNewGames = 0;
			sheetsDoneDownloading = 0;
		}
	}

	@Override
	protected void doWakefulWork(Intent arg0) {
		database = ((MyApplication) getApplication()).getDatabase();
		ArrayList<String> sheetID = getAllSheetID();
		ArrayList<Thread> threads = new ArrayList<Thread>();
		amountOfSheets = sheetID.size();
		for (String string : sheetID) {
			threads.add(downloadGames(0, string));
		}
		for (int i = 0; i < threads.size(); i++){
			try {
				threads.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
