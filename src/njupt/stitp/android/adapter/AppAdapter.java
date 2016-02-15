package njupt.stitp.android.adapter;

import java.util.List;

import njupt.stitp.android.R;
import njupt.stitp.android.model.APP;
import njupt.stitp.android.util.IconUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppAdapter extends ArrayAdapter<APP> {
	private int resourceId;

	public AppAdapter(Context context, int resource, List<APP> objects) {
		super(context, resource, objects);
		resourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		APP app = getItem(position);
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.appName = (TextView) view.findViewById(R.id.app_name);
			viewHolder.appUseTime = (TextView) view
					.findViewById(R.id.app_use_time);
			viewHolder.appIcon = (ImageView) view.findViewById(R.id.app_icon);
			view.setTag(viewHolder); // 将ViewHolder存储在View中
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
		}
		viewHolder.appName.setText(app.getAppName());
		int useHour = app.getAppUseTime() / 60;
		int useMin = app.getAppUseTime() % 60;
		viewHolder.appUseTime.setText(useHour + "小时 " + useMin + "分钟");
		viewHolder.appIcon.setImageDrawable(IconUtil.byteToDrawable(app
				.getIcon()));
		return view;

	}

	class ViewHolder {
		ImageView appIcon;
		TextView appName;
		TextView appUseTime;
	}
}
