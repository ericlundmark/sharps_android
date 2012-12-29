package com.sharps.Network;

import java.io.IOException;

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
	NetworkMediator mediator;
	String sheetID;
	String gameID;
	String result;

	public CorrectionHandler(String sheetID, String gameID, String result) {
		super();
		mediator = NetworkMediator.getSingletonObject();
		this.sheetID = sheetID;
		this.gameID = gameID;
		this.result = result;
	}

	@Override
	public void run() {
		HttpClient hc = new DefaultHttpClient();
		HttpPost post = new HttpPost(
				"http://www.sharps.se/forums/includes/ss/ajax_edit_spreadsheet.php?id="
						+ sheetID + "&grade=" + gameID + "&to=" + result);
		// Create local HTTP context
		HttpContext localContext = new BasicHttpContext();
		// Bind custom cookie store to the local context
		localContext.setAttribute(ClientContext.COOKIE_STORE,
				mediator.getCockies());
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		try {
			hc.execute(post, responseHandler, localContext);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
