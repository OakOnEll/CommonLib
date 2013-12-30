package com.oakonell.utils;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

public class StringUtils {
	public static boolean isEmpty(String string) {
		return string == null || string.trim().length() == 0;
	}

	public static void applyFlashEnlargeAnimation(TextView textView) {
		AnimationSet animSet = new AnimationSet(true);
		Animation scaleUp = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f);
		Animation anim = new AlphaAnimation(0.0f, 1.0f);
		animSet.addAnimation(scaleUp);
		animSet.addAnimation(anim);

		animSet.setDuration(100);
		animSet.setRepeatCount(3);
		animSet.setRepeatMode(Animation.REVERSE);

		textView.startAnimation(animSet);
	}
	
	/**
	 * Sets a hyperlink style to the textview.
	 */
	public static void makeTextViewHyperlink(TextView tv) {
		SpannableStringBuilder ssb = new SpannableStringBuilder();
		ssb.append(tv.getText());
		ssb.setSpan(new URLSpan("#"), 0, ssb.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setText(ssb, TextView.BufferType.SPANNABLE);
	}

	public static SpannableString getLinkText(String input) {
		SpannableString string = new SpannableString(input);
		string.setSpan(new UnderlineSpan(), 0, string.length(), 0);
		return string;
	}

	/**
	 * (Copied from
	 * http://stackoverflow.com/questions/4826061/what-is-the-fastest
	 * -way-to-get-the-domain-host-name-from-a-url) Will take a url such as
	 * http://www.stackoverflow.com and return www.stackoverflow.com
	 * 
	 * @param url
	 * @return
	 */
	public static String getHost(String url) {
		if (url == null || url.length() == 0)
			return "";

		int doubleslash = url.indexOf("//");
		if (doubleslash == -1)
			doubleslash = 0;
		else
			doubleslash += 2;

		int end = url.indexOf('/', doubleslash);
		end = end >= 0 ? end : url.length();

		return url.substring(doubleslash, end);
	}

	/**
	 * Based on :
	 * http://grepcode.com/file/repository.grepcode.com/java/ext/com.google
	 * .android/android/2.3
	 * .3_r1/android/webkit/CookieManager.java#CookieManager.getBaseDomain%28java.lang.String%2
	 * 9 Get the base domain for a given host or url. E.g. mail.google.com will
	 * return google.com
	 * 
	 * @param host
	 * @return
	 */
	public static String getBaseDomain(String url) {
		String host = getHost(url);

		int startIndex = 0;
		int nextIndex = host.indexOf('.');
		int lastIndex = host.lastIndexOf('.');
		while (nextIndex < lastIndex) {
			startIndex = nextIndex + 1;
			nextIndex = host.indexOf('.', startIndex);
		}
		if (startIndex > 0) {
			return host.substring(startIndex);
		} else {
			return host;
		}
	}
}
