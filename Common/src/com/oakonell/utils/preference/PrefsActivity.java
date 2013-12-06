package com.oakonell.utils.preference;

import java.util.List;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.oakonell.utils.Utils;

public abstract class PrefsActivity extends SherlockPreferenceActivity {
	/**
	 * Override me with a list of specific preference resources. These will
	 * probably match those in the "headers" xml file
	 */
	protected abstract int[] getPreV11PreferenceResources();

	/** Override me with the header resource id */
	protected abstract int getV11HeaderResourceId();

	@Override
	public void onCreate(Bundle aSavedState) {
		super.onCreate(aSavedState);

		Utils.enableStrictMode();

		final ActionBar ab = getSupportActionBar();
		// set defaults for logo
		ab.setDisplayUseLogoEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setTitle("Settings");

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			for (int eachId : getPreV11PreferenceResources()) {
				addPreferencesFromResource(eachId);
				// new PrefActivityPrefFinder(this)
				PreferenceConfigurer configurer = getPreV11PreferenceConfigurer();
				if (configurer != null) {
					configurer.configure();
				}
			}
		}
	}

	protected final PrefActivityPrefFinder getPrefFinder() {
		return new PrefActivityPrefFinder(this);
	}

	protected PreferenceConfigurer getPreV11PreferenceConfigurer() {
		return null;
	}

	public interface PreferenceConfigurer {
		void configure();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(getV11HeaderResourceId(), target);
	}

	public static abstract class PreferenceFinder {
		public abstract Preference findPreference(String key);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class PrefFragmentPrefFinder extends PreferenceFinder {
		private final PreferenceFragment frag;

		PrefFragmentPrefFinder(PreferenceFragment frag) {
			this.frag = frag;
		}

		@Override
		public Preference findPreference(String key) {
			return frag.findPreference(key);
		}

	}

	public static class PrefActivityPrefFinder extends PreferenceFinder {
		private final PreferenceActivity activity;

		public PrefActivityPrefFinder(PreferenceActivity activity) {
			this.activity = activity;
		}

		@SuppressWarnings("deprecation")
		@Override
		public Preference findPreference(String key) {
			return activity.findPreference(key);
		}
	}

	public PreferenceConfigurer configureMultiple(
			final PreferenceConfigurer... configurers) {
		return new PreferenceConfigurer() {
			@Override
			public void configure() {
				for (PreferenceConfigurer each : configurers) {
					each.configure();
				}
			}
		};
	}

}