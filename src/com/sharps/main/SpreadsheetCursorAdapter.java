package com.sharps.main;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharps.R;

public class SpreadsheetCursorAdapter extends SimpleCursorAdapter {

	

	public SpreadsheetCursorAdapter(Context context, int layout, Cursor c, String[] from,
			int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		String string=((TextView)view.findViewById(R.id.list_complex_caption)).getText().toString();
		string=string.substring(0, string.length()-1);
		if (Double.parseDouble(string)>=100) {
			((TextView)view.findViewById(R.id.list_complex_caption)).setTextColor(Color.GREEN);
		}else if(Double.parseDouble(string)<100&&Double.parseDouble(string)!=0){
			((TextView)view.findViewById(R.id.list_complex_caption)).setTextColor(Color.RED);
		}else{
			((TextView)view.findViewById(R.id.list_complex_caption)).setTextColor(Color.GRAY);
		}
		  return view;
	}

}
