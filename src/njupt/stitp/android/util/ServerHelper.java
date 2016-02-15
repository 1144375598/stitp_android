package njupt.stitp.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import njupt.stitp.android.model.APP;
import njupt.stitp.android.model.APPDto;
import njupt.stitp.android.model.Track;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

public class ServerHelper {
	private static final String base = "http://192.168.1.106:8080/NJUPT_STITP_Server/";

	// 服务器无返回结果则返回状态代码，有返回值则返回json字符串
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
			byte[] bytes = buffer.toString().getBytes();
			OutputStream out = connection.getOutputStream();
			out.write(bytes);
			Integer resultCode = connection.getResponseCode();
			Log.i("resultCode", url + "***" + resultCode.toString());
			if (resultCode == 200) {
				InputStream in = connection.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));
				StringBuilder response = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				if (response.length() == 0) {
					return resultCode.toString();
				}
			}
			return resultCode.toString();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 上传成功返回true，失败返回false
	public boolean uploadTrackAndAPP(String path, List<Track> tracks,
			List<APP> apps) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("info =");
		if (tracks != null) {
			buffer.append(new Gson().toJson(tracks));
		} else if (apps != null) {
			// 将icon的byte[]转换为base64字符串上传
			List<APPDto> appDtos = new ArrayList<APPDto>();
			for (APP app : apps) {
				APPDto appDto = new APPDto();
				appDto.setAddDate(app.getAddDate());
				appDto.setAppName(app.getAppName());
				appDto.setAppUseTime(app.getAppUseTime());
				appDto.setUsername(app.getUsername());
				appDto.setIcon(Base64.encodeToString(app.getIcon(),
						Base64.URL_SAFE | Base64.NO_WRAP));
				appDtos.add(appDto);
			}
			buffer.append(new Gson().toJson(appDtos));
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
			byte[] bytes = buffer.toString().getBytes();
			OutputStream out = connection.getOutputStream();
			out.write(bytes);
			if (connection.getResponseCode() == 200) {
				return true;
			} else {
				return false;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
