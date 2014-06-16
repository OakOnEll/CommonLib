package com.oakonell.utils.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.oakonell.utils.R;
import com.oakonell.utils.Utils;

/**
 * This utility will prompt the user to rate the application in Google Play
 * store.
 */
public class AppRater extends SherlockDialogFragment {
	// adapted from
	// http://www.androidsnippets.com/prompt-engaged-users-to-rate-your-app-in-the-android-market-appirater

	private static final String PREF_NAME = "apprater";

	private static final String DATE_FIRSTLAUNCH_PREF_KEY = "date_firstlaunch";
	private static final String LAUNCH_COUNT_PREF_KEY = "launch_count";
	private static final String DONT_SHOW_AGAIN_PREF_KEY = "dont_show_again";

	private static final int DAYS_UNTIL_PROMPT = 2;
	private static final int PROMPT_EVERY_N_LAUNCHES = 5;
	private static final long MILLIS_UNTIL_PROMPT = DAYS_UNTIL_PROMPT * 24 *60 *60*1000;

	public static void app_launched(final Activity mContext,
			final Handler handler, final Runnable continuation) {
		final SharedPreferences prefs = mContext.getSharedPreferences(
				PREF_NAME, 0);
		if (prefs.getBoolean(DONT_SHOW_AGAIN_PREF_KEY, false)) {
			executeContinuation(continuation);
			return;
		}

		final SharedPreferences.Editor editor = prefs.edit();

		// Increment launch counter
		long launchCount = prefs.getLong(LAUNCH_COUNT_PREF_KEY, 0) + 1;
		editor.putLong(LAUNCH_COUNT_PREF_KEY, launchCount);

		// Get date of first launch
		Long date_firstLaunch = prefs.getLong(DATE_FIRSTLAUNCH_PREF_KEY, 0);
		if (date_firstLaunch == 0) {
			date_firstLaunch = System.currentTimeMillis();
			editor.putLong(DATE_FIRSTLAUNCH_PREF_KEY, date_firstLaunch);
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

		// http://stackoverflow.com/questions/13266901/dark-text-on-dark-background-on-alertdialog-with-theme-sherlock-light
		// TODO builder.setInverseBackgroundForced(true);

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		LayoutInflater li = LayoutInflater.from(mContext);
		View view = li.inflate(R.layout.dialog_app_rater,
				null, false);
		
		builder.setView(view);
		AlertDialog dialog = builder.create();
		configureView(mContext,dialog,  view, continuation);
		dialog.show();
		
//		AppRater dialog = new AppRater();
//		dialog.initialize(continuation);
//		dialog.show(mContext.getSupportFragmentManager(), "app_rater");
	}
	
	public static  void launchPlayStore(final Activity mContext) {
		mContext.startActivity(new Intent(
				Intent.ACTION_VIEW,
				Uri.parse("market://details?id="
						+ mContext.getPackageName())));
	}

	private Runnable continuation;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_app_rater, container,
				false);
		configureView(getActivity(),getDialog(),view, continuation);
		
		return view;
	}

	private static void configureView(final Activity activity, final Dialog dialog, View view, final Runnable continuation) {
		final String applicationName = Utils.getAppName(activity);
		dialog.setTitle(activity.getResources().getString(R.string.rate_app, applicationName));
		
		TextView rateText = (TextView) view.findViewById(R.id.rate_text);		
		rateText.setText(activity.getResources().getString(R.string.rate_text, applicationName));
		
		RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rate_bar);
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						final SharedPreferences prefs = activity.getSharedPreferences(
								PREF_NAME, 0);
						final Editor editor = prefs.edit();
						editor.putBoolean(DONT_SHOW_AGAIN_PREF_KEY, true);
						editor.commit();
						dialog.dismiss();
						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								launchPlayStore(activity);
								// let bootstrapping restart on next activity
								// launch
								// executeContinuation(continuation);

							}
						});
					}
				}).start();
			}
		});
		
		Button remindLaterButton = (Button) view.findViewById(R.id.rate_remind_later);
		remindLaterButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				new Thread(new Runnable() {
					@Override
					public void run() {
						final SharedPreferences prefs = activity.getSharedPreferences(
								PREF_NAME, 0);
						final Editor editor = prefs.edit();
						if (prefs != null) {
							editor.putBoolean(DONT_SHOW_AGAIN_PREF_KEY, false);
							editor.commit();
						}
						executeContinuation(continuation);
					}
				}).start();

			}
		});

		Button noThanksButton = (Button) view.findViewById(R.id.rate_no_thanks);
		noThanksButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				new Thread(new Runnable() {
					@Override
					public void run() {
						final SharedPreferences prefs = activity.getSharedPreferences(
								PREF_NAME, 0);
						final Editor editor = prefs.edit();
						if (prefs != null) {
							editor.putBoolean(DONT_SHOW_AGAIN_PREF_KEY, true);
							editor.commit();
						}
						executeContinuation(continuation);
					}
				}).start();
			}
		});
	}
}
