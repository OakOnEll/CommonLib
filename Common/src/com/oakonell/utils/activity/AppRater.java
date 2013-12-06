package com.oakonell.utils.activity;

import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.oakonell.utils.R;
import com.oakonell.utils.Utils;

/**
 * This utility will prompt the user to rate the application in Google Play
 * store.
 */
public class AppRater {
	// adapted from
	// http://www.androidsnippets.com/prompt-engaged-users-to-rate-your-app-in-the-android-market-appirater

	private static final String PREF_NAME = "apprater";
	private static final int DAYS_UNTIL_PROMPT = 1;
	private static final int PROMPT_EVERY_N_LAUNCHES = 5;

	private static final long MILLIS_UNTIL_PROMPT = TimeUnit.DAYS.convert(
			DAYS_UNTIL_PROMPT, TimeUnit.MILLISECONDS);
	private static final String DATE_FIRSTLAUNCH = "date_firstlaunch";
	private static final String LAUNCH_COUNT = "launch_count";
	private static final String DONT_SHOW_AGAIN = "dont_show_again";

	public static void app_launched(final Activity mContext,
			final Handler handler, final Runnable continuation) {
		final SharedPreferences prefs = mContext.getSharedPreferences(
				PREF_NAME, 0);
		if (prefs.getBoolean(DONT_SHOW_AGAIN, false)) {
			executeContinuation(continuation);
			return;
		}

		final SharedPreferences.Editor editor = prefs.edit();

		// Increment launch counter
		long launchCount = prefs.getLong(LAUNCH_COUNT, 0) + 1;
		editor.putLong(LAUNCH_COUNT, launchCount);

		// Get date of first launch
		Long date_firstLaunch = prefs.getLong(DATE_FIRSTLAUNCH, 0);
		if (date_firstLaunch == 0) {
			date_firstLaunch = System.currentTimeMillis();
			editor.putLong(DATE_FIRSTLAUNCH, date_firstLaunch);
		}
		editor.commit();

		if ((launchCount % PROMPT_EVERY_N_LAUNCHES == 0)
				&& System.currentTimeMillis() >= date_firstLaunch
						+ MILLIS_UNTIL_PROMPT) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					editor.commit();
					showRateDialog(mContext, continuation);
				}
			});
		} else {
			editor.commit();
			executeContinuation(continuation);
		}
	}

	private static void executeContinuation(Runnable continuation) {
		if (continuation != null) {
			continuation.run();
		}
	}

	public static void showRateDialog(final Activity mContext,
			final Runnable continuation) {
		final SharedPreferences prefs = mContext.getSharedPreferences(
				PREF_NAME, 0);
		final Editor editor = prefs.edit();

		// http://stackoverflow.com/questions/13266901/dark-text-on-dark-background-on-alertdialog-with-theme-sherlock-light
		// TODO builder.setInverseBackgroundForced(true);
		final Dialog dialog = new Dialog(mContext);

		final String applicationName = Utils.getAppName(mContext);
		dialog.setTitle(mContext.getString(R.string.rate_app, applicationName));

		LinearLayout ll = new LinearLayout(mContext);
		ll.setOrientation(LinearLayout.VERTICAL);

		TextView tv = new TextView(mContext);
		tv.setText(mContext.getString(R.string.rate_text, applicationName));
		tv.setWidth(240);
		tv.setPadding(4, 0, 4, 10);
		ll.addView(tv);

		RatingBar b1 = new RatingBar(mContext);
		b1.setNumStars(5);
		b1.setRating(4);
		b1.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						editor.putBoolean(DONT_SHOW_AGAIN, true);
						editor.commit();
						dialog.dismiss();
						mContext.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								mContext.startActivity(new Intent(
										Intent.ACTION_VIEW,
										Uri.parse("market://details?id="
												+ mContext.getPackageName())));
								// let bootstrapping restart on next activity
								// launch
								// executeContinuation(continuation);

							}
						});
					}
				}).start();
			}
		});
		ll.addView(b1);

		TextView rateCommentView = new TextView(mContext);
		rateCommentView.setText("("
				+ mContext.getString(R.string.rate_redirects_to_market) + ")");
		rateCommentView.setGravity(Gravity.CENTER_HORIZONTAL);
		ll.addView(rateCommentView);

		Button b2 = new Button(mContext);
		b2.setText(mContext.getText(R.string.rate_remind_later));
		b2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (prefs != null) {
							editor.putBoolean(DONT_SHOW_AGAIN, false);
							editor.commit();
						}
						executeContinuation(continuation);
					}
				}).start();

			}
		});
		ll.addView(b2);

		Button b3 = new Button(mContext);
		b3.setText(mContext.getText(R.string.rate_no_thanks));
		b3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (prefs != null) {
							editor.putBoolean(DONT_SHOW_AGAIN, true);
							editor.commit();
						}
						executeContinuation(continuation);
					}
				}).start();
			}
		});
		ll.addView(b3);

		dialog.setContentView(ll);

		dialog.show();
	}
}
