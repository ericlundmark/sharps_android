package com.sharps.Network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.sharps.main.ViewContent;

import android.os.AsyncTask;

public class SheetDownloader extends
		AsyncTask<String, Integer, HashMap<String, Hashtable<String, String>>> {
	public enum RequestMode {
		FAVOURITE, MY
	}

	private NetworkMediator mediator = NetworkMediator.getSingletonObject();
	private RequestMode mode;

	public SheetDownloader(RequestMode reqEnum, String url) {
		// TODO Auto-generated constructor stub
		execute(url);
		mode = reqEnum;
	}

	@Override
	protected HashMap<String, Hashtable<String, String>> doInBackground(
			String... params) {
		// TODO Auto-generated method stub
		System.out.println(params[0]);
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
		return decodeResponse(str);
	}

	@Override
	protected void onPostExecute(
			HashMap<String, Hashtable<String, String>> result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		// if(mode==RequestMode.MY){
		mediator.getLibrary().getMySheets().putAll(result);
		mediator.notifyContentContainers(ViewContent.SPREADSHEETS);
		// }

	}

	private HashMap<String, Hashtable<String, String>> decodeResponse(String str) {
		// TODO Auto-generated method stub
		HashMap<String, Hashtable<String, String>> decoded = new HashMap<String, Hashtable<String, String>>();
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(str));
			org.w3c.dom.Document doc = docBuilder.parse(is);
			// normalize text representation
			doc.getDocumentElement().normalize();
			//System.out.println("Root element of the doc is "
			//		+ doc.getDocumentElement().getNodeName());
			NodeList listOfEvents = doc.getElementsByTagName("sheet");
			int totalPersons = listOfEvents.getLength();
			//System.out.println("Total no of sheets : " + totalPersons);

			for (int s = 0; s < listOfEvents.getLength(); s++) {
				Node eventNode = listOfEvents.item(s);
				if (eventNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eventElement = (Element) eventNode;

					// -------
					NodeList firstNameList = eventElement
							.getElementsByTagName("id");
					Element firstNameElement = (Element) firstNameList.item(0);

					NodeList textFNList = firstNameElement.getChildNodes();
					String id = ((Node) textFNList.item(0)).getNodeValue()
							.trim();
					//System.out.println("id : " + id);
					if (decoded.get(id) == null) {
						decoded.put(id, new Hashtable<String, String>());
					}

					// -------
					NodeList lastNameList = eventElement
							.getElementsByTagName("roi");
					Element lastNameElement = (Element) lastNameList.item(0);

					NodeList textLNList = lastNameElement.getChildNodes();
					String roi;
					if (textLNList.item(0) != null) {
						roi = ((Node) textLNList.item(0)).getNodeValue().trim();
					} else {
						roi = "Not specified";
					}
					decoded.get(id).put("roi", roi);
					// -------
					NodeList lastAddedList = eventElement
							.getElementsByTagName("lastadded");
					Element lastAddedElement = (Element) lastAddedList.item(0);

					NodeList textLAList = lastAddedElement.getChildNodes();
					String lastAdded;
					if (textLAList.item(0) != null) {
						lastAdded = ((Node) textLAList.item(0)).getNodeValue().trim();
					} else {
						lastAdded = "Not specified";
					}
					decoded.get(id).put("lastadded", lastAdded);

					// -------

					NodeList titleNameList = eventElement
							.getElementsByTagName("title");
					Element titleNameElement = (Element) titleNameList.item(0);

					NodeList textTitleList = titleNameElement.getChildNodes();
					String title;
					if (textTitleList.item(0) != null) {
						title = ((Node) textTitleList.item(0)).getNodeValue()
								.trim();
					} else {
						title = "Not specified";
					}
					decoded.get(id).put("title", title);
					// -------

					NodeList numbOfGamesList = eventElement
							.getElementsByTagName("games");
					Element numbOfGamesElement = (Element) numbOfGamesList
							.item(0);

					NodeList textNumbOfGamesList = numbOfGamesElement
							.getChildNodes();
					String numberOfGames;
					if (textNumbOfGamesList.item(0) != null) {
						numberOfGames = ((Node) textNumbOfGamesList.item(0))
								.getNodeValue().trim();
					} else {
						numberOfGames = "Not specified";
					}
					decoded.get(id).put("numberOfGames", numberOfGames);
					if (mode==RequestMode.MY) {
						decoded.get(id).put("sheetGroup", "my");
					} else {
						decoded.get(id).put("sheetGroup", "fav");
					}

				}// end of if clause

			}// end of for loop with s var
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decoded;
	}
}
