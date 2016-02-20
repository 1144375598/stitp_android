package njupt.stitp.android.model;

public class UseTimeControl {

	private String username;
	private String start;//样式为12:00
	private String end; //样式为24:00

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
