package njupt.stitp.android.model;

public class APPDto {
	private String username;
	private int appUseTime; // 以分钟为单位
	private String appName;
	private String addDate; //格式为2015-10-20
	private String icon;

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

	public String getAddDate() {
		return addDate;
	}

	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}


}
