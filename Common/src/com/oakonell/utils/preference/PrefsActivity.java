package com.oakonell.utils.preference;

import java.util.List;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.oakonell.utils.R;
import com.oakonell.utils.Utils;

public abstract class PrefsActivity extends SherlockPreferenceActivity {
	/**
	 * Override me with a list of specific preference resources. These will
	 * probably match those in the "headers" xml file
	 */
	protected abstract int[] getPreV11PreferenceResources();

	/** Override me with the header resource id */
	protected int getV11HeaderResourceId() {
		return -1;
	}

	@Override
	public void onCreate(Bundle aSavedState) {
		super.onCreate(aSavedState);

		Utils.enableStrictMode();

		final ActionBar ab = getSupportActionBar();
		if (ab != null) {
			// set defaults for logo
			ab.setDisplayUseLogoEnabled(true);
			ab.setDisplayHomeAsUpEnabled(true);
			ab.setTitle(R.string.settings);
		}

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			addPreV11Resources();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (useUpNavigation()) {
				NavUtils.navigateUpFromSameTask(this);
			} else {
				onBackPressed();
			}
			return true;
		}
		return false;
	}

	protected boolean useUpNavigation() {
		return false;
	}

	protected void addPreV11Resources() {
		beforePreV11BuildFromResource();
		for (int eachId : getPreV11PreferenceResources()) {
			addPreferencesFromResource(eachId);
			// new PrefActivityPrefFinder(this)
		}
		PreferenceConfigurer configurer = getPreV11PreferenceConfigurer();
		if (configurer != null) {
			configurer.configure();
		}
	}

	protected void beforePreV11BuildFromResource() {
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
		int resId = getV11HeaderResourceId();
		if (resId < 0)
			return;
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