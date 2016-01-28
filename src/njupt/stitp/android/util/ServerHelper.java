package njupt.stitp.android.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.http.protocol.HTTP;

import com.google.gson.Gson;

import android.R.integer;
import android.util.Log;
import njupt.stitp.android.activity.LoginActivity;
import njupt.stitp.android.model.APP;
import njupt.stitp.android.model.Track;

public class ServerHelper {
	private static final String base = "http://192.168.1.107:8080/NJUPT_STITP_Server/";

	public String getResult(String path, Map<String, String> params) {
		StringBuffer buffer = new StringBuffer();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				buffer.append(entry.getKey()).append("=")
						.append(entry.getValue()).append("&");
			}
			buffer.deleteCharAt(buffer.length() - 1);
		}

		try {
			URL url = new URL(base + path);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(8000);
			connection.setReadTimeout(8000);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			byte[] bytes=buffer.toString().getBytes();
			OutputStream out = connection.getOutputStream();
			out.write(bytes);
			if (connection.getResponseCode() == 200) {
				InputStream in = connection.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));
				StringBuilder response = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				if (response.equals(""))
					return new Integer(connection.getResponseCode()).toString();
				else {
					return response.toString();
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean uploadTrackAndAPP(String path, List<Track> tracks,
			List<APP> apps) {
		StringBuffer buffer=new StringBuffer();
		buffer.append("info =");
		if (tracks != null) {
			buffer.append(new Gson().toJson(tracks).toString());
		} else if (apps != null) {
			buffer.append(new Gson().toJson(apps).toString());
		} else {
			return false;
		}
		try {
			URL url = new URL(base + path);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(8000);
			connection.setReadTimeout(8000);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			byte[] bytes=buffer.toString().getBytes();
			OutputStream out = connection.getOutputStream();
			out.write(bytes);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
