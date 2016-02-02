package njupt.stitp.android.model;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	private String username;
	private int timeOfContinuousUse;
	private int timeOfContinuousListen;
	private String channelId;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getTimeOfContinuousUse() {
		return timeOfContinuousUse;
	}

	public void setTimeOfContinuousUse(int timeOfContinuousUse) {
		this.timeOfContinuousUse = timeOfContinuousUse;
	}

	public int getTimeOfContinuousListen() {
		return timeOfContinuousListen;
	}

	public void setTimeOfContinuousListen(int timeOfContinuousListen) {
		this.timeOfContinuousListen = timeOfContinuousListen;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

}
