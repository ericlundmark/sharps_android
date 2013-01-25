package com.sharps.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.SystemClock;

import com.commonsware.cwac.wakeful.WakefulIntentService;


public class AppListener implements WakefulIntentService.AlarmListener {

	@Override
	public long getMaxAge() {
		return(300000*2);
	}

	@Override
	public void scheduleAlarms(AlarmManager mgr, PendingIntent pi,
			Context context) {
		mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
			    SystemClock.elapsedRealtime() + 15000,
			    300000, pi);
	}

	@Override
	public void sendWakefulWork(Context context) {
		WakefulIntentService.sendWakefulWork(context, SyncService.class);
	}

}
