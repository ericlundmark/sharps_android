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

public class GameAdder extends Thread {
	private ArrayList<String> myItems;
	private String id;
	private NetworkMediator mediator = NetworkMediator.getSingletonObject();
	public GameAdder(ArrayList<String> myItems, String id) {
		super();
		// TODO Auto-generated constructor stub
		this.myItems=myItems;
		this.id=id;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		String str = "";
		try {
			int index = 0;
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			for (String item : myItems) {
				if (!item.equals(mediator.getTitles()[index])) {
					nameValuePairs.add(new BasicNameValuePair(mediator.getKeys()[index],
							item));
				}
				index++;
			}
			HttpClient hc = new DefaultHttpClient();
			HttpPost post = new HttpPost(
					"http://www.sharps.se/forums//includes/ss/ajax_edit_spreadsheet.php?id="
							+ id + "&a=1");
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
			// Create local HTTP context
			HttpContext localContext = new BasicHttpContext();
			// Bind custom cookie store to the local context
			localContext.setAttribute(ClientContext.COOKIE_STORE, mediator.getCockies());
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			str = hc.execute(post, responseHandler, localContext);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
