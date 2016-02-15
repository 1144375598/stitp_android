package njupt.stitp.android.util;

import njupt.stitp.android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

//本类定义了百度地图中轨迹覆盖物，便于转化为bitmap后显示在地图上 
// 由一个背景图片和文字构成，文字用于描述轨迹的编号 
public class ReceiverView {
	private static Context context;
	private static int bg_resid = R.drawable.ic_track;

	public ReceiverView(Context _context) {
		context = _context;
	}

	public Bitmap getBitmapFromView(int fontColor, int fontSize, String text) {
		TextView textView = new TextView(context);
		textView.setGravity(Gravity.CENTER);
		textView.setBackgroundResource(bg_resid);
		textView.setTextColor(fontColor);
		textView.setText(text);
		textView.setTextSize(fontSize);

		textView.destroyDrawingCache();
		textView.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		textView.layout(0, 0, textView.getMeasuredWidth(),
				textView.getMeasuredHeight());
		textView.setDrawingCacheEnabled(true);
		Bitmap bitmap = textView.getDrawingCache(true);
		return bitmap;
	}
}
