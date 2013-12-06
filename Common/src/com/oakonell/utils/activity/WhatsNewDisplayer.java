package com.oakonell.utils.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;

import com.oakonell.utils.R;

public class WhatsNewDisplayer {
	private static final String CHANGES_TXT_FILENAME = "changes.txt";
	// modified from
	// http://www.srombauts.fr/2011/04/21/adding-a-whats-new-screen-to-your-android-application/
	private static final String PREF_NAME = "whats_new";
	private static final String LAST_VERSION_CODE_KEY = "last_version_code";

	public static void show(Context mContext, Handler handler,
			Runnable continuation) {
		show(mContext, handler, true, continuation);
	}

	// Show the dialog only if not already shown for this version of the
	// application
	public static void show(Context mContext, Handler handler,
			boolean conditional, final Runnable continuation) {
		// Get the versionCode of the Package, which must be different
		// (incremented) in each release on the market in the
		// AndroidManifest.xml

		// TODO offload this from the UI thread, with a progress dialog while
		// loading?

		PackageManager pm = mContext.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(mContext.getPackageName(),
					PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
			throw new RuntimeException("Error getting package info", e);
		}
		final PackageInfo packageInfo = pi;

		final SharedPreferences prefs = mContext.getSharedPreferences(
				PREF_NAME, 0);
		final long lastVersionCode = prefs.getLong(LAST_VERSION_CODE_KEY, -1);

		if (conditional
				&& (packageInfo.versionCode == lastVersionCode || lastVersionCode == -1)) {
			if (lastVersionCode == -1) {
				markVersionAsRead(packageInfo, prefs);
			}
			executeContinuation(continuation);
			return;
		}

		InputStream open;
		try {
			open = mContext.getAssets().open(CHANGES_TXT_FILENAME);
		} catch (FileNotFoundException e) {
			executeContinuation(continuation);
			return;
		} catch (IOException e) {
			throw new RuntimeException("Error getting changes.txt asset", e);
		}
		String message = new java.util.Scanner(open).useDelimiter("\\A").next();
		try {
			open.close();
		} catch (IOException e) {
			throw new RuntimeException("Error Closing changes.txt asset", e);
		}

		ApplicationInfo ai;
		try {
			ai = pm.getApplicationInfo(mContext.getPackageName(), 0);
		} catch (final NameNotFoundException e) {
			ai = null;
		}

		final String applicationName = (String) (ai != null ? pm
				.getApplicationLabel(ai) : "("
				+ mContext.getString(R.string.rate_unknown_app_title) + ")");

		final String title = applicationName + " v" + packageInfo.versionName;

		// Show the News since last version
		final AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(android.R.string.ok,
						new Dialog.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialogInterface, int i) {
								dialogInterface.dismiss();
								new Thread(new Runnable() {
									@Override
									public void run() {
										markVersionAsRead(packageInfo, prefs);
										executeContinuation(continuation);
									}
								}).start();
							}
						});
		Runnable show = new Runnable() {
			@Override
			public void run() {
				builder.show();
			}
		};
		if (handler == null) {
			show.run();
		} else {
			handler.post(show);
		}

	}

	private static void executeContinuation(Runnable continuation) {
		if (continuation != null) {
			continuation.run();
		}
	}

	private static void markVersionAsRead(final PackageInfo packageInfo,
			final SharedPreferences prefs) {
		// Mark this version as read
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(LAST_VERSION_CODE_KEY, packageInfo.versionCode);
		editor.commit();
	}

	public static boolean changesFileExists(Context mContext) {
		try {
			InputStream open = mContext.getAssets().open(CHANGES_TXT_FILENAME);
			open.close();
			return true;
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			throw new RuntimeException("Error getting changes.txt asset", e);
		}
	}

}
