package com.oakonell.utils.activity;

import android.app.Activity;
import android.os.Handler;

public class AppLaunchUtils {
    public static void appLaunched(final Activity mContext, final Runnable continuation) {
    	final Handler handler = new Handler();
    	Runnable runnable = new Runnable() {
			@Override
			public void run() {
		        WhatsNewDisplayer.show(mContext, handler, continuation);				
			}
		};
        AppRater.app_launched(mContext, handler, runnable);
    }
}
