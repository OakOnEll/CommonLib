package com.oakonell.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Extended image view to show the content description in a Toast view when the
 * user long presses.
 * 
 * Note: `android:contentDescription` must be set in order for the toast to work
 * 
 * taken from https://gist.github.com/scruffyfox/3894926
 * 
 * @author Callum Taylor <http://callumtaylor.net>
 */
public class HintedImageView extends ImageView implements OnLongClickListener {
	private Context mContext;
	private OnLongClickListener mOnLongClickListener;

	public HintedImageView(Context context) {
		super(context);
		mContext = context;

		setOnLongClickListener(this);
	}

	public HintedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		setOnLongClickListener(this);
	}

	@Override
	public void setOnLongClickListener(OnLongClickListener l) {
		if (l == this) {
			super.setOnLongClickListener(l);
			return;
		}

		mOnLongClickListener = l;
	}

	@Override
	public boolean onLongClick(View v) {
		if (mOnLongClickListener != null) {
			if (!mOnLongClickListener.onLongClick(v)) {
				handleLongClick();
				return true;
			}
		} else {
			handleLongClick();
			return true;
		}

		return false;
	}

	private void handleLongClick() {
		String contentDesc = getContentDescription().toString();
		if (!TextUtils.isEmpty(contentDesc)) {
			int[] pos = new int[2];
			getLocationInWindow(pos);

			Toast t = Toast.makeText(mContext, contentDesc, Toast.LENGTH_SHORT);
			t.setGravity(Gravity.TOP | Gravity.LEFT,
					pos[0] - ((contentDesc.length() / 2) * 12), pos[1] - 128);
			t.show();
		}
	}
}