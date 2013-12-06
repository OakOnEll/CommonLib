package com.oakonell.utils.preference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;

import com.oakonell.utils.R;
import com.oakonell.utils.activity.AppRater;
import com.oakonell.utils.activity.WhatsNewDisplayer;
import com.oakonell.utils.preference.PrefsActivity.PreferenceConfigurer;
import com.oakonell.utils.preference.PrefsActivity.PreferenceFinder;

public class CommonPreferences implements PreferenceConfigurer {
	private final PreferenceFinder finder;
	private final PreferenceActivity activity;
	private final Class<? extends Activity> aboutActivityClass;

	public CommonPreferences(PreferenceActivity activity, PreferenceFinder finder,
			Class<? extends Activity> aboutActivityClass) {
		this.finder = finder;
		this.activity = activity;
		this.aboutActivityClass = aboutActivityClass;
	}

	@Override
	public void configure() {
		Preference aboutPref = finder.findPreference(activity
				.getString(R.string.pref_about_key));
		if (aboutPref != null) {
			if (aboutActivityClass == null) {
				PreferenceGroup parent = getParent(aboutPref);
				parent.removePreference(aboutPref);
			} else {
				aboutPref
						.setOnPreferenceClickListener(new OnPreferenceClickListener() {
							@Override
							public boolean onPreferenceClick(
									Preference preference) {
								Intent aboutIntent = new Intent(activity,
										aboutActivityClass);
								activity.startActivity(aboutIntent);
								return true;
							}
						});
			}
		}

		Preference rateAppPref = finder.findPreference(activity
				.getString(R.string.pref_rate_app_key));
		if (rateAppPref != null) {
			rateAppPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {
						@Override
						public boolean onPreferenceClick(Preference preference) {
							AppRater.showRateDialog(activity, null);
							return true;
						}
					});
		}

		Preference resetPref = finder.findPreference(activity
				.getString(R.string.pref_reset_preferences_key));
		if (resetPref != null) {
			resetPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {
						@Override
						public boolean onPreferenceClick(Preference preference) {
							resetPreferences();
							return true;
						}
					});
		}

		Preference changesPref = finder.findPreference(activity
				.getString(R.string.pref_changes_key));
		if (changesPref != null) {
			changesPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {
						@Override
						public boolean onPreferenceClick(Preference preference) {
							WhatsNewDisplayer.show(activity, new Handler(),
									false, null);
							return true;
						}
					});
		}

		if (!WhatsNewDisplayer.changesFileExists(activity)) {
			PreferenceCategory otherPrefCat = (PreferenceCategory) finder
					.findPreference(activity
							.getString(R.string.pref_other_category));
			if (otherPrefCat != null) {
				otherPrefCat.removePreference(changesPref);
			}
		}
	}

	protected void resetPreferences() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					SharedPreferences preferences = PreferenceManager
							.getDefaultSharedPreferences(activity);
					SharedPreferences.Editor editor = preferences.edit();
					editor.clear();
					editor.commit();
					// reload this activity
					activity.finish();
					activity.startActivity(activity.getIntent());
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					break;
				default:
					throw new RuntimeException("Unexpected button was clicked");
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		String message = activity.getResources().getString(
				R.string.pref_confirm_reset_preferences);
		builder.setMessage(message)
				.setPositiveButton(android.R.string.yes, dialogClickListener)
				.setNegativeButton(android.R.string.no, dialogClickListener)
				.show();
	}

	private PreferenceGroup getParent(Preference preference) {
		return getParent(activity.getPreferenceScreen(), preference);
	}

	private PreferenceGroup getParent(PreferenceGroup root,
			Preference preference) {
		for (int i = 0; i < root.getPreferenceCount(); i++) {
			Preference p = root.getPreference(i);
			if (p == preference)
				return root;
			if (PreferenceGroup.class.isInstance(p)) {
				PreferenceGroup parent = getParent((PreferenceGroup) p,
						preference);
				if (parent != null)
					return parent;
			}
		}
		return null;
	}

}
