package njupt.stitp.android.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import njupt.stitp.android.R;
import njupt.stitp.android.adapter.FriendAdapter;
import njupt.stitp.android.application.MyApplication;
import njupt.stitp.android.db.RelationshipDB;
import njupt.stitp.android.db.UserDB;
import njupt.stitp.android.model.Friend;
import njupt.stitp.android.model.UseTimeControl;
import njupt.stitp.android.util.JsonUtil;
import njupt.stitp.android.util.MyActivityManager;
import njupt.stitp.android.util.ServerHelper;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class AddFriendActivity extends ActionBarActivity {
	private Button confirm;
	private EditText friendName;
	private String tempFriendName;
	private UserDB userDB;
	private String loginName;
	private RadioButton parent;
	private RadioButton child;
	private Handler handler;
	private ListView friendList;
	private RelationshipDB relationshipDB;

	public static final String REQUEST_ADD_FRIEND_ACTION = "REQUEST ADD FRIEND";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getInstance().addActivity(this);
		setContentView(R.layout.activity_addfriend);

		getSupportActionBar().setTitle(
				new StringBuffer(getString(R.string.friend_manage)));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		confirm = (Button) findViewById(R.id.et_search_input);
		friendName = (EditText) findViewById(R.id.addfriend_sure);
		parent = (RadioButton) findViewById(R.id.relationship_parent);
		child = (RadioButton) findViewById(R.id.relationship_child);
		friendList = (ListView) findViewById(R.id.friend_list);

		child.setChecked(true);
		userDB = new UserDB(this);
		relationshipDB = new RelationshipDB(this);
		loginName = ((MyApplication) getApplication()).getUsername();
		registerForContextMenu(friendList);
		setListView();

		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					Toast.makeText(AddFriendActivity.this,
							getString(R.string.request_have_send),
							Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(AddFriendActivity.this,
							getString(R.string.addfriend_user_not_exist),
							Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			};
		};

		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String name = friendName.getText().toString();
				if (name.equals("")) {
					friendName.requestFocus();
					friendName.setError(new StringBuffer(
							getString(R.string.username_not_exist)));
					return;
				} else if (name.equals(loginName)) {
					friendName.requestFocus();
					friendName.setError(new StringBuffer(
							getString(R.string.cannot_add_self_be_friend)));
					return;
				} else if (userDB.isFriend(loginName, name)) {
					Toast.makeText(AddFriendActivity.this,
							getString(R.string.have_be_friend),
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					new Thread(new Runnable() {

						@Override
						public void run() {
							String path = "user/addFriend";
							Map<String, String> params = new HashMap<String, String>();
							params.put("user.username", loginName);
							params.put("friendName", friendName.getText()
									.toString());
							if (parent.isChecked()) {
								params.put("relationship", "parent");
							} else {
								params.put("relationship", "child");
							}
							String result = new ServerHelper().getResult(path,
									params);
							int result_code = JsonUtil.getResultCode(result);
							Message msg = new Message();
							msg.what = result_code;
						}
					}).start();
				}
			}
		});
	}

	private void setListView() {
		List<Friend> list = relationshipDB.getFriend(loginName);
		friendList.removeAllViewsInLayout();
		FriendAdapter adapter = new FriendAdapter(this,
				R.layout.item_controltime, list);
		friendList.setAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(0, 1, 0, "删除");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item
					.getMenuInfo();
			View view = friendList.getChildAt(menuInfo.position);
			TextView friendNameTV = (TextView) view
					.findViewById(R.id.tv_friend_name);
			TextView friendRelationshipTV = (TextView) view
					.findViewById(R.id.tv_friend_relationship);
			tempFriendName = friendNameTV.getText().toString();
			String friendRelationship = friendRelationshipTV.getText()
					.toString();
			if (TextUtils.equals(friendRelationship, "家长")) {
				Toast.makeText(this, getString(R.string.cannot_delete_parent),
						Toast.LENGTH_SHORT).show();
				break;
			} else {
				new Thread(new Runnable() {

					@Override
					public void run() {
						String path = "user/deleteChild";
						Map<String, String> params = new HashMap<String, String>();
						params.put("user.username", loginName);
						params.put("friendName", tempFriendName);
						new ServerHelper().getResult(path, params);
					}
				}).start();
				relationshipDB.deleteFriend(loginName, tempFriendName);
				setListView();
			}
			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = getIntent();
		String action = intent.getAction();
		if (TextUtils.equals(action, REQUEST_ADD_FRIEND_ACTION)) {
			String message = intent.getExtras().getString("message");
			final String relationship = intent.getExtras().getString(
					"relationship");
			final String requestName = intent.getExtras().getString(
					"requestName");
			AlertDialog.Builder builder = new Builder(this);
			builder.setMessage(message);
			builder.setTitle(R.string.add_friend_request);
			builder.setPositiveButton(R.string.agree,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							new Thread(new Runnable() {

								@Override
								public void run() {
									String path = "user/addFriendResult";
									Map<String, String> params = new HashMap<String, String>();
									params.put("user.username", requestName);
									params.put("friendName", loginName);
									params.put("resultCode", "0");
									params.put("relationship", relationship);
									new ServerHelper().getResult(path, params);
									RelationshipDB relationshipDB = new RelationshipDB(
											AddFriendActivity.this);
									if (TextUtils
											.equals(relationship, "parent")) {
										relationshipDB.addRelationship(
												loginName, requestName);
									} else if (TextUtils.equals(relationship,
											"child")) {
										relationshipDB.addRelationship(
												requestName, loginName);
									}
									relationshipDB.close();
									AddFriendActivity.this.finish();
								}
							}).start();
						}
					});
			builder.setNegativeButton(R.string.refuse,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							new Thread(new Runnable() {

								@Override
								public void run() {
									String path = "user/addFriendResult";
									Map<String, String> params = new HashMap<String, String>();
									params.put("user.username", requestName);
									params.put("friendName", loginName);
									params.put("resultCode", "1");
									params.put("relationship", relationship);
									new ServerHelper().getResult(path, params);
									AddFriendActivity.this.finish();
								}
							}).start();
						}
					});
		}

	}

	@Override
	protected void onDestroy() {
		userDB.close();
		relationshipDB.close();
		super.onDestroy();
	}
}
