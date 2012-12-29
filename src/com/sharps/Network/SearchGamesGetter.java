package com.sharps.Network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.AsyncTask;

public class SearchGamesGetter extends AsyncTask<String, Integer,ArrayList<Hashtable<String, String>> > {
	private NetworkMediator mediator = NetworkMediator.getSingletonObject();
	public SearchGamesGetter(String URL) {
		super();
		execute(URL);
	}

	@Override
	protected ArrayList<Hashtable<String, String>> doInBackground(
			String... params) {
		String str = "";
		try {
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT) 
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 3000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient hc = new DefaultHttpClient(httpParameters);
			HttpGet post = new HttpGet(params[0]);
			// Create local HTTP context
			HttpContext localContext = new BasicHttpContext();
			// Bind custom cookie store to the local context
			localContext.setAttribute(ClientContext.COOKIE_STORE,
					mediator.getCockies());
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			str = hc.execute(post, responseHandler, localContext);
			System.out.println("Inkommande: " + str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return parseContent(str);
	}

	private ArrayList<Hashtable<String, String>> parseContent(
			String str) {
		ArrayList<Hashtable<String, String>> map = new ArrayList<Hashtable<String,String>>();
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(str));
			org.w3c.dom.Document doc = docBuilder.parse(is);
			// normalize text representation
			doc.getDocumentElement().normalize();
			System.out.println("Root element of the doc is "
					+ doc.getDocumentElement().getNodeName());
			NodeList listOfGames = doc.getElementsByTagName("game");
			int totalGames = listOfGames.getLength();
			System.out.println("Total no of games : " + totalGames);

			for (int s = 0; s < listOfGames.getLength(); s++) {
				Hashtable<String, String> table = new Hashtable<String, String>();
				Node eventNode = listOfGames.item(s);
				if (eventNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eventElement = (Element) eventNode;
					for (int i = 0; i < eventElement.getChildNodes()
							.getLength(); i++) {
						Node e = eventElement.getChildNodes().item(i)
								.getChildNodes().item(0);
						if (e != null && e.getNodeValue() != null) {
							table.put(eventNode.getChildNodes().item(i)
									.getNodeName(), e.getNodeValue());
						}
					}
					map.add(table);
				}// end of if clause
				
			}// end of for loop with s var
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return map;
	}


}
