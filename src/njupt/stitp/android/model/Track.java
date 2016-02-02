package njupt.stitp.android.model;

import java.io.Serializable;

import android.R.integer;

public class Track implements Serializable {

	private static final long serialVersionUID = -758459502806858414L;
	private String username;
	private double longitude;
	private double latitude;
	//addTime格式为2015-10-15 10:20:00
	private String addTime;
	private String address;
	private int stayTime;//在该地点停留的时间，以分钟为单位

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getStayTime() {
		return stayTime;
	}

	public void setStayTime(int stayTime) {
		this.stayTime = stayTime;
	}

}