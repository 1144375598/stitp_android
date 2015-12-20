package njupt.stitp.android.model;

public class UseTimeControl {

	private String username;
	// 如start="12:10:00" end="23:30:00" ----24小时制
	private String start;
	private String end;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

}
