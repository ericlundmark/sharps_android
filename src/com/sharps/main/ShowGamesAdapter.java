package com.sharps.main;

import Database.MySQLiteHelper;
import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public abstract class ShowGamesAdapter extends MyAdapter {

	public ShowGamesAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView title = (TextView) view.findViewById(mTo[0]);
		TextView subtitle = (TextView) view.findViewById(mTo[1]);
		String team1 = cursor.getString(cursor
				.getColumnIndex(MySQLiteHelper.COLUMN_TEAM1));
		String team2 = cursor.getString(cursor
				.getColumnIndex(MySQLiteHelper.COLUMN_TEAM2));
		String sign = cursor.getString(cursor
				.getColumnIndex(MySQLiteHelper.COLUMN_SIGN));
		if (sign.equals("1")) {
			title.setText(Html.fromHtml("<b>" + team1 + "</b>-" + team2));
		} else if (sign.equals(2)) {
			title.setText(Html.fromHtml(team1 + "-<b>" + team2 + "</b>"));
		} else {
			title.setText(team1 + "-" + team2);
		}
		String result = cursor.getString(cursor
				.getColumnIndex(MySQLiteHelper.COLUMN_RESULT));
		String odds = cursor.getString(cursor
				.getColumnIndex(MySQLiteHelper.COLUMN_ODDS));
		String amount = cursor.getString(cursor
				.getColumnIndex(MySQLiteHelper.COLUMN_AMOUNT));
		subtitle.setText("Insats: " + amount + " Odds: " + odds + " Netto: "
				+ result);
		subtitle.setTextColor(getColor(cursor));
	}
}
