package com.sharps.main;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.sharps.Network.NetworkMediator;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ShowGameAdapter extends SimpleAdapter {
	private Hashtable<String, String> game;
	public ShowGameAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to,Hashtable<String, String> game) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		this.game=game;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = super.getView(position, convertView, parent);
		((TextView) view.findViewById(android.R.id.text1))
		.setTextColor(Color.WHITE);
		if(position==getCount()-1){
			System.out.println(game.get("Netto"));
			double netto=Double.parseDouble(game.get("Netto"));
			if (netto>0) {
				((TextView) view.findViewById(android.R.id.text1))
						.setTextColor(Color.GREEN);
			} else if (netto < 0) {
				((TextView) view.findViewById(android.R.id.text1))
						.setTextColor(Color.RED);
			}else{
				((TextView) view.findViewById(android.R.id.text1))
				.setTextColor(Color.GRAY);
			}
		}
		return view;
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return false;
	}

}
