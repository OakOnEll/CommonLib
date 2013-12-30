package com.oakonell.utils.activity;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.oakonell.utils.R;
import com.oakonell.utils.Utils;

public class GenericAboutActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.generic_about_activity);
		Utils.enableStrictMode();

		final ActionBar ab = getSupportActionBar();
		if (ab != null) {
			// set defaults for logo
			ab.setDisplayUseLogoEnabled(true);
			ab.setDisplayHomeAsUpEnabled(true);
			ab.setTitle(R.string.about_title);
		}
		TextView nameText = (TextView) findViewById(R.id.app_name_id);
		nameText.setText(Utils.getAppName(this));

		TextView versionText = (TextView) findViewById(R.id.version);
		versionText.setText(Utils.getVersion(this));

		ImageView iconImage = (ImageView) findViewById(R.id.icon_image);
		iconImage.setImageDrawable(Utils.getAppLauncherIcon(this));

		WebView aboutText = (WebView) findViewById(R.id.about_description);
		aboutText.loadUrl("file:///android_asset/credits.html");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return false;
	}
}
