package com.oakonell.utils.preference;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.oakonell.utils.preference.PrefsActivity.PrefFragmentPrefFinder;
import com.oakonell.utils.preference.PrefsActivity.PreferenceConfigurer;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PrefsFragment extends PreferenceFragment {
	private int prefResource;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefResource = getActivity().getResources().getIdentifier(
				getArguments().getString("resource"), "xml",
				getActivity().getPackageName());

		addPreferencesFromResource(prefResource);

		PreferenceConfigurer configurer = getPreferenceConfigurer(prefResource);
		if (configurer != null) {
			configurer.configure();
		}
	}

	protected PreferenceConfigurer getPreferenceConfigurer(int prefResource) {
		return null;
	}

	protected final PrefFragmentPrefFinder getPreferenceFinder() {
		return new PrefFragmentPrefFinder(this);
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
