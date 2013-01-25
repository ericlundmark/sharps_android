package com.sharps.Network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import Database.MySQLiteHelper;

import com.sharps.main.SyncService;

public class GameAdder extends Thread {
	private ArrayList<String> myItems;
	private String id;
	private String[] allColumns = { MySQLiteHelper.COLUMN_TEAM1,
			MySQLiteHelper.COLUMN_TEAM2, MySQLiteHelper.COLUMN_DATE,
			MySQLiteHelper.COLUMN_TIME, MySQLiteHelper.COLUMN_SIGN,
			MySQLiteHelper.COLUMN_SIGN2, MySQLiteHelper.COLUMN_SPORT,
			MySQLiteHelper.COLUMN_COUNTRY, MySQLiteHelper.COLUMN_LEAGUE,
			MySQLiteHelper.COLUMN_COMPANY, MySQLiteHelper.COLUMN_PERIOD,
			MySQLiteHelper.COLUMN_INFO, MySQLiteHelper.COLUMN_REKARE,
			MySQLiteHelper.COLUMN_AMOUNT, MySQLiteHelper.COLUMN_ODDS,
			MySQLiteHelper.COLUMN_RESULT };
	private String[] keys = { "team1", "team2", "date", "time", "sign",
			"sign2", "sport", "country", "league", "bolag", "period", "info",
			"rekare", "amount", "odds", "result" };

	public GameAdder(ArrayList<String> myItems, String id) {
		super();
		this.myItems = myItems;
		this.id = id;
	}

	@Override
	public void run() {
		super.run();
		String str = "";
		try {
			int index = 0;
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			for (String item : myItems) {
				if (!item.equals(allColumns[myItems.indexOf(item)])) {
					nameValuePairs.add(new BasicNameValuePair(keys[myItems
							.indexOf(item)], item));
				}
			}
			HttpClient hc = new DefaultHttpClient();
			HttpPost post = new HttpPost(
					"http://www.sharps.se/forums//includes/ss/ajax_edit_spreadsheet.php?id="
							+ id + "&a=1");
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			// Create local HTTP context
			HttpContext localContext = new BasicHttpContext();
			// Bind custom cookie store to the local context
			localContext.setAttribute(ClientContext.COOKIE_STORE,
					SyncService.cookieStore);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			str = hc.execute(post, responseHandler, localContext);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
