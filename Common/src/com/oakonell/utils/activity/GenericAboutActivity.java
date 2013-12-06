package com.oakonell.utils.activity;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.oakonell.utils.R;
import com.oakonell.utils.Utils;

public class GenericAboutActivity extends SherlockFragmentActivity {

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case android.R.id.home:
	// // app icon in action bar clicked; go home
	// Intent intent = new Intent(this, Preferences.class);
	// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
	// | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	// startActivity(intent);
	// return true;
	// default:
	// return super.onOptionsItemSelected(item);
	// }
	// }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.generic_about_activity);
		Utils.enableStrictMode();

		final ActionBar ab = getSupportActionBar();
		// set defaults for logo
		ab.setDisplayUseLogoEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setTitle(R.string.about_title);

		TextView versionText = (TextView) findViewById(R.id.version);
		versionText.setText(Utils.getVersion(this));

		TextView nameText = (TextView) findViewById(R.id.app_name_id);
		nameText.setText(Utils.getAppName(this));

		ImageView iconImage = (ImageView) findViewById(R.id.icon_image);
		iconImage.setImageDrawable(Utils.getAppLauncherIcon(this));

		WebView aboutText = (WebView) findViewById(R.id.about_description);
		aboutText.loadUrl("file:///android_asset/credits.html");
	}

}
