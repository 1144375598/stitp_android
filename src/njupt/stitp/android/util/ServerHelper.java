package njupt.stitp.android.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class ServerHelper {
	private static final String base = "http://192.168.191.4:8080/NJUPT_STITP_Server/";

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
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());
			out.writeBytes(URLEncoder.encode(buffer.toString(), "UTF-8"));
			if (connection.getResponseCode() == 200) {
				InputStream in = connection.getInputStream();
				// 下面对获取到的输入流进行读取
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
}
