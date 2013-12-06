package com.oakonell.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.StrictMode;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.TextView;

public final class Utils {
	private Utils() {
		// prevent instantiation
	}

	public static Drawable getAppLauncherIcon(Context context) {
		return context.getApplicationInfo().loadIcon(
				context.getPackageManager());
	}

	public static void setHighlightedText(TextView textView,
			String description, String filterString) {
		SpannableString text = new SpannableString(description);

		int max = description.length();
		int filterLength = filterString.length();
		String lowerFilterString = filterString
				.toLowerCase(Locale.getDefault());
		String lowerString = description.toLowerCase(Locale.getDefault());
		int index = -1;
		while (index < max
				&& (index = lowerString.indexOf(lowerFilterString, index + 1)) >= 0) {
			text.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC),
					index, index + filterLength,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		textView.setText(text, TextView.BufferType.SPANNABLE);
	}

	public static boolean isIntentHandlable(Context ctx, Intent intent) {
		final PackageManager mgr = ctx.getPackageManager();
		List<ResolveInfo> list = mgr.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	@TargetApi(11)
	public static void enableStrictMode() {
		if (Utils.hasGingerbread()) {
			StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyLog();
			StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyLog();

			if (Utils.hasHoneycomb()) {
				threadPolicyBuilder.penaltyFlashScreen();
				// vmPolicyBuilder
				// .setClassInstanceLimit(ImageGridActivity.class, 1)
				// .setClassInstanceLimit(ImageDetailActivity.class, 1);
			}
			StrictMode.setThreadPolicy(threadPolicyBuilder.build());
			StrictMode.setVmPolicy(vmPolicyBuilder.build());
		}
	}

	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean hasIceCreamSandwich() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}

	public static String getVersion(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			return pInfo.versionName;
		} catch (NameNotFoundException e1) {
			Log.e(Utils.class.getSimpleName(), "Name not found", e1);
			return "error";
		}
	}

	public static int getVersionCode(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			return pInfo.versionCode;
		} catch (NameNotFoundException e1) {
			Log.e(Utils.class.getSimpleName(), "Name not found", e1);
			return -1;
		}
	}

	public static String getAppPackage(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			// TODO .. how to get the app name?
			return pInfo.applicationInfo.packageName;
		} catch (NameNotFoundException e1) {
			Log.e(Utils.class.getSimpleName(), "Name not found", e1);
			return "error";
		}
	}

	public static String getAppName(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			PackageManager p = context.getPackageManager();
			String label = p.getApplicationLabel(pInfo.applicationInfo)
					.toString();
			// TODO handle null values?
//			(String) (ai != null ? pm
//			.getApplicationLabel(ai) : "("
//			+ mContext.getString(R.string.rate_unknown_app_title) + ")");
			return label;
		} catch (NameNotFoundException e1) {
			Log.e(Utils.class.getSimpleName(), "Name not found", e1);
			return "error";
		}

	}

	public static byte[] compress(String string) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
		GZIPOutputStream gos = new GZIPOutputStream(os);
		gos.write(string.getBytes());
		gos.close();
		byte[] compressed = os.toByteArray();
		os.close();
		return compressed;
	}

	public static String decompress(byte[] compressed) throws IOException {
		final int BUFFER_SIZE = 32;
		ByteArrayInputStream is = new ByteArrayInputStream(compressed);
		GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
		StringBuilder string = new StringBuilder();
		byte[] data = new byte[BUFFER_SIZE];
		int bytesRead;
		while ((bytesRead = gis.read(data)) != -1) {
			string.append(new String(data, 0, bytesRead));
		}
		gis.close();
		is.close();
		return string.toString();
	}

}
