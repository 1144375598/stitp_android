package njupt.stitp.android.adapter;

import java.util.List;

import njupt.stitp.android.R;
import njupt.stitp.android.model.GeoFencing;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GeoAdapter extends ArrayAdapter<GeoFencing>{
	private int resourceId;

	public GeoAdapter(Context context, int resource, List<GeoFencing> objects) {
		super(context, resource, objects);
		resourceId = resource;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GeoFencing geoFencing= getItem(position);
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.geoName = (TextView) view.findViewById(R.id.item_geo_name);
			viewHolder.geoCenter = (TextView) view
					.findViewById(R.id.item_geo_center);
			viewHolder.geoRange = (TextView) view.findViewById(R.id.item_geo_range);
			view.setTag(viewHolder); // 将ViewHolder存储在View中
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
		}
		viewHolder.geoName.setText(geoFencing.getGeoName());		
		viewHolder.geoCenter.setText(geoFencing.getAddress());
		viewHolder.geoRange.setText(((Double)(geoFencing.getDistance())).toString());
		return view;

	}

	class ViewHolder {
		TextView geoName;
		TextView geoCenter;
		TextView geoRange;
	}
}
