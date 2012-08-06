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
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sharps.main.ViewContent;

import android.os.AsyncTask;
import android.widget.ProgressBar;

public class GamesDownloader
		extends
		AsyncTask<String, Integer, HashMap<String, ArrayList<Hashtable<String, String>>>> {
	private NetworkMediator mediator = NetworkMediator.getSingletonObject();

	public GamesDownloader(String URL) {
		execute(URL);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected HashMap<String, ArrayList<Hashtable<String, String>>> doInBackground(
			String... params) {
		// TODO Auto-generated method stub
		String str = "";
		try {
			HttpClient hc = new DefaultHttpClient();
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

	@Override
	protected void onPostExecute(
			HashMap<String, ArrayList<Hashtable<String, String>>> result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		mediator.notifyContentContainers(ViewContent.GAMES);
	}

	private HashMap<String, ArrayList<Hashtable<String, String>>> parseContent(
			String str) {
		HashMap<String, ArrayList<Hashtable<String, String>>> map = mediator
				.getLibrary().getGames();
		// TODO Auto-generated method stub
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
				String id = null;
				Node eventNode = listOfGames.item(s);
				if (eventNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eventElement = (Element) eventNode;
					for (int i = 0; i < eventElement.getChildNodes()
							.getLength(); i++) {
						Node e = eventElement.getChildNodes().item(i)
								.getChildNodes().item(0);
						if (e != null && e.getNodeValue() != null) {
							if (eventNode.getChildNodes().item(i).getNodeName()
									.equals("sheetid")) {
								id = e.getNodeValue();
								if (map.get(id)==null) {
									map.put(id,
											new ArrayList<Hashtable<String, String>>());
								}
							}
							table.put(eventNode.getChildNodes().item(i)
									.getNodeName(), e.getNodeValue());
						}
					}
					map.get(id).add(table);
					/*
					 * // ------- NodeList firstNameList = eventElement
					 * .getElementsByTagName("sheetid"); Element
					 * firstNameElement = (Element) firstNameList.item(0);
					 * 
					 * NodeList textFNList = firstNameElement.getChildNodes();
					 * String id = ((Node) textFNList.item(0)).getNodeValue()
					 * .trim(); if (map.get(id) == null) { map.put(id, new
					 * ArrayList<Hashtable<String,String>>()); }
					 * Hashtable<String, String> table=new Hashtable<String,
					 * String>(); // ------- NodeList parlayList = eventElement
					 * .getElementsByTagName("team1"); Element parlayElement =
					 * (Element) parlayList.item(0);
					 * 
					 * NodeList textParlayList = parlayElement.getChildNodes();
					 * String parlay; if(textParlayList.item(0)!=null){ parlay =
					 * ((Node) textParlayList.item(0)) .getNodeValue().trim();
					 * table.put("Parlay", parlay); }else{ table.put("Parlay",
					 * "Not specified"); } // ------- NodeList lastNameList =
					 * eventElement .getElementsByTagName("team1"); Element
					 * lastNameElement = (Element) lastNameList.item(0);
					 * 
					 * NodeList textLNList = lastNameElement.getChildNodes();
					 * String home; if(textLNList.item(0)!=null){ home = ((Node)
					 * textLNList.item(0)) .getNodeValue().trim();
					 * table.put("Hemmalag", home); }else{ table.put("Hemmalag",
					 * "Not specified"); }
					 * 
					 * 
					 * 
					 * // -------
					 * 
					 * NodeList titleNameList = eventElement
					 * .getElementsByTagName("team2"); Element titleNameElement
					 * = (Element) titleNameList.item(0);
					 * 
					 * NodeList textTitleList =
					 * titleNameElement.getChildNodes(); String away;
					 * if(textTitleList.item(0)!=null){ away = ((Node)
					 * textTitleList.item(0)) .getNodeValue().trim(); }else{
					 * away="Not specified"; } table.put("Bortalag", away);
					 * //---------- NodeList oddsList = eventElement
					 * .getElementsByTagName("odds"); Element oddsElement =
					 * (Element) oddsList.item(0);
					 * 
					 * NodeList textOddsList = oddsElement.getChildNodes();
					 * 
					 * String odds; if(textOddsList.item(0)!=null){ odds =
					 * ((Node) textOddsList.item(0)) .getNodeValue().trim();
					 * }else{ odds="Not specified"; } table.put("Odds", odds);
					 * //---------- NodeList stakeList = eventElement
					 * .getElementsByTagName("amount"); Element stakeElement =
					 * (Element) stakeList.item(0);
					 * 
					 * NodeList textStakeList = stakeElement.getChildNodes();
					 * String stake; if(textStakeList.item(0)!=null){ stake =
					 * ((Node) textStakeList.item(0)) .getNodeValue().trim();
					 * }else{ stake="Not specified"; } table.put("Insats",
					 * stake); //---------- NodeList nettoList = eventElement
					 * .getElementsByTagName("result"); Element nettoElement =
					 * (Element) nettoList.item(0);
					 * 
					 * NodeList textNettoList = nettoElement.getChildNodes();
					 * String netto; if(textNettoList.item(0)!=null){ netto =
					 * ((Node) textNettoList.item(0)) .getNodeValue().trim();
					 * }else{ netto="Not specified"; } table.put("Netto",
					 * netto); //---------- NodeList signList = eventElement
					 * .getElementsByTagName("sign"); Element signElement =
					 * (Element) signList.item(0);
					 * 
					 * NodeList textSignList = signElement.getChildNodes();
					 * String sign; if(textNettoList.item(0)!=null){ sign =
					 * ((Node) textSignList.item(0)) .getNodeValue().trim();
					 * }else{ sign="Not specified"; } table.put("Tecken", sign);
					 * //---------- NodeList sign2List = eventElement
					 * .getElementsByTagName("sign2"); Element sign2Element =
					 * (Element) sign2List.item(0);
					 * 
					 * NodeList textSign2List = sign2Element.getChildNodes();
					 * String sign2; if(textSign2List.item(0)!=null){ sign2 =
					 * ((Node) textSign2List.item(0)) .getNodeValue().trim();
					 * }else{ sign2="Not specified"; } table.put("Tecken2",
					 * sign2);
					 * 
					 * //---------- NodeList dateList = eventElement
					 * .getElementsByTagName("date"); Element dateElement =
					 * (Element) dateList.item(0);
					 * 
					 * NodeList textDateList = dateElement.getChildNodes();
					 * String date; if(textDateList.item(0)!=null){ date =
					 * ((Node) textDateList.item(0)) .getNodeValue().trim();
					 * }else{ date="Not specified"; } table.put("Datum", date);
					 * //---------- NodeList spelIDList = eventElement
					 * .getElementsByTagName("spelid"); Element spelIDelement =
					 * (Element) spelIDList.item(0);
					 * 
					 * NodeList textSpelIDList = spelIDelement.getChildNodes();
					 * String spelID; if(textSpelIDList.item(0)!=null){ spelID =
					 * ((Node) textSpelIDList.item(0)) .getNodeValue().trim();
					 * }else{ spelID="Not specified"; } table.put("spelid",
					 * spelID); //---------- NodeList timeList = eventElement
					 * .getElementsByTagName("time"); Element timeElement =
					 * (Element) timeList.item(0);
					 * 
					 * NodeList textTimeList = timeElement.getChildNodes();
					 * String time; if(textTimeList.item(0)!=null){ time =
					 * ((Node) textTimeList.item(0)) .getNodeValue().trim();
					 * }else{ time="Not specified"; } table.put("Tid", time);
					 */
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
		return map;
	}

}
