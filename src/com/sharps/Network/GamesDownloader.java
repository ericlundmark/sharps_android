package com.sharps.Network;

import java.io.IOException;
import java.io.StringReader;
import java.util.Observable;

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

import com.sharps.main.GamesActivity;

public class GamesDownloader extends Observable{
	private NetworkMediator mediator = NetworkMediator.getSingletonObject();
	private SQLiteDatabase database;
	private int amountAdded = 0;

	public GamesDownloader(SQLiteDatabase database, String URL) {

		this.database = database;
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
			HttpGet post = new HttpGet(URL);
			// Create local HTTP context
			HttpContext localContext = new BasicHttpContext();
			// Bind custom cookie store to the local context
			localContext.setAttribute(ClientContext.COOKIE_STORE,
					mediator.getCockies());
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			str = hc.execute(post, responseHandler, localContext);
		} catch (IOException e) {
			e.printStackTrace();
		}
		parseContent(str);
	}

	private void parseContent(String str) {
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
					if (tagName.equalsIgnoreCase("date")) {
						values.put(MySQLiteHelper.COLUMN_DATE, readText(parser));
					} else if (tagName.equalsIgnoreCase("time")) {
						values.put(MySQLiteHelper.COLUMN_TIME, readText(parser));
					} else if (tagName.equalsIgnoreCase("team1")) {
						values.put(MySQLiteHelper.COLUMN_TEAM1,
								readText(parser));
					} else if (tagName.equalsIgnoreCase("team2")) {
						values.put(MySQLiteHelper.COLUMN_TEAM2,
								readText(parser));
					} else if (tagName.equalsIgnoreCase("sign")) {
						values.put(MySQLiteHelper.COLUMN_SIGN, readText(parser));
					} else if (tagName.equalsIgnoreCase("sign2")) {
						values.put(MySQLiteHelper.COLUMN_SIGN2,
								readText(parser));
					} else if (tagName.equalsIgnoreCase("amount")) {
						values.put(MySQLiteHelper.COLUMN_AMOUNT,
								readText(parser));
					} else if (tagName.equalsIgnoreCase("odds")) {
						values.put(MySQLiteHelper.COLUMN_ODDS, readText(parser));
					} else if (tagName.equalsIgnoreCase("sport")) {
						values.put(MySQLiteHelper.COLUMN_SPORT,
								readText(parser));
					} else if (tagName.equalsIgnoreCase("country")) {
						values.put(MySQLiteHelper.COLUMN_COUNTRY,
								readText(parser));
					} else if (tagName.equalsIgnoreCase("league")) {
						values.put(MySQLiteHelper.COLUMN_LEAGUE,
								readText(parser));
					} else if (tagName.equalsIgnoreCase("info")) {
						values.put(MySQLiteHelper.COLUMN_INFO, readText(parser));
					} else if (tagName.equalsIgnoreCase("rekare")) {
						values.put(MySQLiteHelper.COLUMN_REKARE,
								readText(parser));
					} else if (tagName.equalsIgnoreCase("bolag")) {
						values.put(MySQLiteHelper.COLUMN_COMPANY,
								readText(parser));
					} else if (tagName.equalsIgnoreCase("live")) {
						values.put(MySQLiteHelper.COLUMN_LIVE, readText(parser));
					} else if (tagName.equalsIgnoreCase("locked")) {
						values.put(MySQLiteHelper.COLUMN_LOCKED,
								readText(parser));
					} else if (tagName.equalsIgnoreCase("period")) {
						values.put(MySQLiteHelper.COLUMN_PERIOD,
								readText(parser));
					} else if (tagName.equalsIgnoreCase("alive")) {
						values.put(MySQLiteHelper.COLUMN_ALIVE,
								readText(parser));
					} else if (tagName.equalsIgnoreCase("result")) {
						values.put(MySQLiteHelper.COLUMN_RESULT,
								readText(parser));
					} else if (tagName.equalsIgnoreCase("spelid")) {
						values.put(MySQLiteHelper.COLUMN_GAMEID,
								readText(parser));
					} else if (tagName.equalsIgnoreCase("sheetid")) {
						values.put(MySQLiteHelper.COLUMN_SHEETID,
								readText(parser));
					}
					// if <content>
					else if (tagName.equalsIgnoreCase("game")) {
						values.clear();
					}
					break;
				case XmlPullParser.END_TAG:
					tagName = parser.getName();
					if (tagName.equalsIgnoreCase("game")) {
						String selection = MySQLiteHelper.COLUMN_GAMEID
								+ " = "
								+ values.getAsString(MySQLiteHelper.COLUMN_GAMEID);
						Cursor cursor = database.query(
								MySQLiteHelper.TABLE_GAMES,
								new String[] { MySQLiteHelper.COLUMN_GAMEID },
								selection, null, null, null, null);
						if (cursor.getCount() > 0) {
							amountAdded++;
							database.update(MySQLiteHelper.TABLE_GAMES, values,
									selection, null);
						} else if (values.size() > 0) {
							amountAdded++;
							database.insert(MySQLiteHelper.TABLE_GAMES, null,
									values);
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
		} finally {
			if (amountAdded == 0) {
				GamesActivity.isLastPageReched = true;
			}
			notifyObservers(amountAdded);
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
