package njupt.stitp.android.adapter;

import java.util.List;

import njupt.stitp.android.R;
import njupt.stitp.android.model.UseTimeControl;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class UseControlAdapter extends ArrayAdapter<UseTimeControl> {
	private int resourceId;

	public UseControlAdapter(Context context, int resource,
			List<UseTimeControl> objects) {
		super(context, resource, objects);
		resourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		UseTimeControl useTimeControl = getItem(position);
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.controlTime = (TextView) view
					.findViewById(R.id.tv_control_time);
			view.setTag(viewHolder); // 将ViewHolder存储在View中
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
		}
		viewHolder.controlTime.setText(useTimeControl.getStart() + " - "
				+ useTimeControl.getEnd());
		return view;

	}

	class ViewHolder {
		TextView controlTime;
	}
}
