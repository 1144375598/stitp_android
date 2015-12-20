package njupt.stitp.android.model;

public class APP {
	private String username;
	private int appUseTime;  //以分钟为单位
	private String appName;
	private String date;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getAppUseTime() {
		return appUseTime;
	}

	public void setAppUseTime(int appUseTime) {
		this.appUseTime = appUseTime;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
