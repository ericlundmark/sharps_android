package com.sharps.Network;

import java.io.IOException;
import java.util.Hashtable;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class CorrectionHandler extends Thread {
	Hashtable<String, String> game;
	NetworkMediator mediator;
	String id;
	String result;

	public CorrectionHandler(String id, String result,
			Hashtable<String, String> game) {
		super();
		// TODO Auto-generated constructor stub
		this.game = game;
		mediator = NetworkMediator.getSingletonObject();
		this.id = id;
		this.result = result;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(id + " " + game.get("spelid") + " " + result);
		HttpClient hc = new DefaultHttpClient();
		HttpPost post = new HttpPost(
				"http://www.sharps.se/forums/includes/ss/ajax_edit_spreadsheet.php?id="
						+ id + "&grade=" + game.get("spelid") + "&to=" + result);
		// Create local HTTP context
		HttpContext localContext = new BasicHttpContext();
		// Bind custom cookie store to the local context
		localContext.setAttribute(ClientContext.COOKIE_STORE,
				mediator.getCockies());
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		try {
			hc.execute(post, responseHandler, localContext);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
