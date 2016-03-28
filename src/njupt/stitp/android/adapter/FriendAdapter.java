package njupt.stitp.android.adapter;

import java.util.List;

import njupt.stitp.android.R;
import njupt.stitp.android.model.Friend;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FriendAdapter extends ArrayAdapter<Friend> {
	private int resourceId;

	public FriendAdapter(Context context, int resource, List<Friend> objects) {
		super(context, resource, objects);
		resourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Friend friend = getItem(position);
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.friendName = (TextView) view.findViewById(R.id.tv_friend_name);
			viewHolder.friendRelationship = (TextView) view
					.findViewById(R.id.tv_friend_relationship);
			view.setTag(viewHolder); // 将ViewHolder存储在View中
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
		}		
		viewHolder.friendName.setText(friend.getUsername());
		viewHolder.friendRelationship.setText(friend.getRelationship());
		return view;

	}

	class ViewHolder {
		TextView friendName;
		TextView friendRelationship;
	}
}
