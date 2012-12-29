package com.sharps.Network;

import java.util.Iterator;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sharps.main.LoginListener;

public class NetworkMediator {
	

	private static NetworkMediator singletonObject;
	private CookieStore cockies;
	LoginListener loginListener;

	private String[] titles = { "Hemmalag", "Bortalag", "Datum", "Tid",
			"Tecken", "Tecken2", "Sport", "Land", "Liga", "Bolag", "Period",
			"Info", "Rekare", "Insats", "Odds", "Netto" };
	

	public void setCockies(CookieStore cockies) {
		this.cockies = cockies;
	}

	public String[] getTitles() {
		return titles;
	}

	public void setLoginListener(LoginListener loginListener) {
		this.loginListener = loginListener;
	}

	private NetworkMediator() {
		cockies = new BasicCookieStore();
	}

	public static synchronized NetworkMediator getSingletonObject() {
		if (singletonObject == null) {
			singletonObject = new NetworkMediator();
		}
		return singletonObject;
	}

	public synchronized void login(String username, String password) {
		new LogginHandler(username, password);
	}

	public CookieStore getCockies() {
		return cockies;
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

	public boolean gotInternet(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
}
