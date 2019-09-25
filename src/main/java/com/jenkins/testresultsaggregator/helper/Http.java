package com.jenkins.testresultsaggregator.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Http {
	
	public static String get(String url, String authentication) throws IOException {
		return get(new URL(url), authentication);
	}
	
	public static String get(URL url, String authentication) throws IOException {
		StringBuilder buf = new StringBuilder();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(10000);
		conn.setRequestProperty("User-Agent", "Mozilla/5.0");
		conn.setDoOutput(true);
		if (authentication != null) {
			conn.setRequestProperty("Authorization", "Basic " + authentication);
		}
		// Get the response
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF8"));
		String line;
		while ((line = rd.readLine()) != null) {
			buf.append(line);
		}
		rd.close();
		return buf.toString();
	}
	
	public static int getResponseCode(String url, String authentication) {
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setReadTimeout(2000);
			con.setConnectTimeout(2000);
			if (authentication != null) {
				con.setRequestProperty("Authorization", "Basic " + authentication);
			}
			int responseCode = con.getResponseCode();
			con.disconnect();
			return responseCode;
		} catch (Exception ex) {
		}
		return 0;
	}
}
