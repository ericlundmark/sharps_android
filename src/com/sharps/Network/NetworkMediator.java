package com.sharps.Network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sharps.main.AddGame.MyAdapter.ListItem;
import com.sharps.main.Library;
import com.sharps.main.LoginListener;
import com.sharps.main.NetworkContentContainer;
import com.sharps.main.SearchResultsView;
import com.sharps.main.Searchable;
import com.sharps.main.ViewContent;

public class NetworkMediator {
	public static enum Results {
		WIN, PUSH, LOSS
	}

	private static NetworkMediator singletonObject;
	private Library library;
	private CookieStore cockies;
	private ArrayList<NetworkContentContainer> contentContainer = new ArrayList<NetworkContentContainer>();
	LoginListener loginListener;

	private String[] titles = { "Hemmalag", "Bortalag", "Datum", "Tid",
			"Tecken", "Tecken2", "Sport", "Land", "Liga", "Bolag", "Period",
			"Info", "Rekare", "Insats", "Odds", "Netto" };
	private String[] keys = { "team1", "team2", "date", "time", "sign",
			"sign2", "sport", "country", "league", "bolag", "period", "info",
			"rekare", "amount", "odds", "result" };
	public Context context;
	private Searchable searchable;
	
	public Searchable getSearchable() {
		return searchable;
	}

	public void setSearchable(Searchable searchable) {
		this.searchable = searchable;
	}

	public void setCockies(CookieStore cockies) {
		this.cockies = cockies;
	}

	public String[] getTitles() {
		return titles;
	}

	public String[] getKeys() {
		return keys;
	}

	public void setLoginListener(LoginListener loginListener) {
		this.loginListener = loginListener;
	}

	public void setContentContainer(NetworkContentContainer contentContainer) {
		this.contentContainer.add(contentContainer);
	}

	private NetworkMediator() {
		library = new Library();
		cockies = new BasicCookieStore();
	}

	public static synchronized NetworkMediator getSingletonObject() {
		if (singletonObject == null) {
			singletonObject = new NetworkMediator();
		}
		return singletonObject;
	}

	public synchronized void refreshSheets() {
		library.getMySheets().clear();
		library.getGames().clear();
		downloadSpreadsheets();
	}

	public void setResultToGame(String id, String result,
			Hashtable<String, String> game) {
		CorrectionHandler connectionHandler = new CorrectionHandler(id, result,
				game);
	}

	public synchronized void login(String username, String password) {
		LogginHandler handler = new LogginHandler(username, password);
	}

	public Library getLibrary() {
		return library;
	}

	public CookieStore getCockies() {
		return cockies;
	}
	public void searchGames(String searchString){
		SearchGamesGetter gamesGetter=new SearchGamesGetter("http://www.sharps.se/forums/includes/ss/app_infoga.php?letters="+searchString);
	}
	public void doLogOut(){
		HttpClient hc = new DefaultHttpClient();
		HttpPost post = new HttpPost(
				"http://www.sharps.se/forums/login.php?do=logout");
		try {
			// Create local HTTP context
			HttpContext localContext = new BasicHttpContext();
			// Bind custom cookie store to the local context
			localContext.setAttribute(ClientContext.COOKIE_STORE,cockies);
			HttpResponse response = hc.execute(post, localContext);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cockies.clear();
	}

	public void downloadSpreadsheets() {
		SheetDownloader my = new SheetDownloader(
				SheetDownloader.RequestMode.MY,
				"http://www.sharps.se/forums/includes/ss/app_mysheets.php");
		SheetDownloader favs = new SheetDownloader(
				SheetDownloader.RequestMode.FAVOURITE,
				"http://www.sharps.se/forums/includes/ss/app_favsheets.php");
	}

	public synchronized void downloadNextGames(String sheetID) {
		ArrayList<Hashtable<String, String>> content = library.getGames().get(
				sheetID);
		if (content != null) {
			GamesDownloader downloader = new GamesDownloader(
					"http://www.sharps.se/forums/includes/ss/app_games.php?ssid="
							+ sheetID + "&page=" + (content.size() + 1) / 10);
		} else {
			GamesDownloader downloader = new GamesDownloader(
					"http://www.sharps.se/forums/includes/ss/app_games.php?ssid="
							+ sheetID + "&page=" + 0);
		}

	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public void notifyContentContainers(ViewContent mode) {
		for (NetworkContentContainer container : contentContainer) {
			container.updateViewContent(mode);
		}
	}

	public boolean isLoggedIn() {
		Iterator iterator = cockies.getCookies().iterator();
		while (iterator.hasNext()) {
			String string = iterator.next().toString();
			if (string.startsWith(("vbseo"), 19) && string.contains("yes")) {
				System.out.println("logged in");
				return true;
			}
		}
		return false;

	}

	public boolean gotInternet() {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public void layGame(ArrayList<ListItem> myItems, String id) {
		String str = "";
		try {
			int index = 0;
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			for (ListItem item : myItems) {
				if (!item.caption.equals(keys[index])) {
					nameValuePairs.add(new BasicNameValuePair(keys[index],
							item.caption));
				}
				index++;
			}
			HttpClient hc = new DefaultHttpClient();
			HttpPost post = new HttpPost(
					"http://www.sharps.se/forums//includes/ss/ajax_edit_spreadsheet.php?id="
							+ id + "&a=1");
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			// Create local HTTP context
			HttpContext localContext = new BasicHttpContext();
			// Bind custom cookie store to the local context
			localContext.setAttribute(ClientContext.COOKIE_STORE, getCockies());
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			str = hc.execute(post, responseHandler, localContext);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
