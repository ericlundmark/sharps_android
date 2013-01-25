package com.sharps.Network;

import java.io.IOException;
import java.io.StringReader;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import Database.MySQLiteHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sharps.main.SyncService;

public class SheetDownloader {
	private SQLiteDatabase database;
	public static String MY = "1";
	public static String FAVOURITE = "0";

	private String mode;

	public SheetDownloader(SQLiteDatabase database, String mode, String url) {
		this.database = database;
		this.mode = mode;
		startHttp(url);
	}

	private void startHttp(String url) {
		String str = "";
		try {
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 3000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient hc = new DefaultHttpClient(httpParameters);
			HttpGet post = new HttpGet(url);
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
		decodeResponse(str);

	}

	private void decodeResponse(String str) {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(str));
			int eventType = parser.getEventType();
			ContentValues values = new ContentValues();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {

				// at start of a tag: START_TAG
				case XmlPullParser.START_TAG:
					// get tag name
					String tagName = parser.getName();
					// if <study>, get attribute: 'id'
					if (tagName.equalsIgnoreCase("title")) {
						values.put(MySQLiteHelper.COLUMN_TITLE,
								readText(parser));
					} else if (tagName.equalsIgnoreCase("roi")) {
						values.put(MySQLiteHelper.COLUMN_ROI, readText(parser));
					} else if (tagName.equalsIgnoreCase("id")) {
						values.put(MySQLiteHelper.COLUMN_SHEETID,
								readText(parser));
					} else if (tagName.equalsIgnoreCase("lastadded")) {
						values.put(MySQLiteHelper.COLUMN_LAST_ADDED,
								readText(parser));
					} else if (tagName.equalsIgnoreCase("allgames")) {
						values.put(MySQLiteHelper.COLUMN_NUMBER_OF_GAMES,
								readText(parser));
					}
					// if <content>
					else if (tagName.equalsIgnoreCase("sheet")) {
						values.clear();
					}
					break;
				case XmlPullParser.END_TAG:
					tagName = parser.getName();
					if (tagName.equalsIgnoreCase("sheet")) {
						values.put(MySQLiteHelper.COLUMN_OWNER, mode);
						String selection = MySQLiteHelper.COLUMN_SHEETID
								+ " = "
								+ values.getAsString(MySQLiteHelper.COLUMN_SHEETID);
						Cursor cursor = database.query(
								MySQLiteHelper.TABLE_SPREADSHEETS,
								new String[] { MySQLiteHelper.COLUMN_SHEETID },
								selection, null, null, null, null);
						if (cursor.getCount() > 0) {
							database.update(MySQLiteHelper.TABLE_SPREADSHEETS,
									values, selection, null);
						} else {
							values.put(MySQLiteHelper.COLUMN_UNVIEWED_GAMES,
									"0");
							database.insert(MySQLiteHelper.TABLE_SPREADSHEETS,
									null, values);
						}
						cursor.close();
					}
					break;
				}
				// jump to next event
				eventType = parser.next();
			}
			// exception stuffs
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}
}
