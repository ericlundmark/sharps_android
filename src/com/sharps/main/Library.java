package com.sharps.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class Library {
	private HashMap<String, Hashtable<String, String>> sheets=new HashMap<String, Hashtable<String, String>>();
	private HashMap<String, ArrayList<Hashtable<String,String>>> games=new HashMap<String, ArrayList<Hashtable<String,String>>>();
	
	public Library() {
		// TODO Auto-generated constructor stub
	}
	public HashMap<String, Hashtable<String, String>> getMySheets() {
		return sheets;
	}
	public HashMap<String, ArrayList<Hashtable<String,String>>> getGames() {
		return games;
	}
}
