package com.dalma.broker.service.notification;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class BaseBridge {
	
	protected String call(String payload, String path, String apiUrl) throws IOException {
		URL url = new URL(new StringBuilder(apiUrl).append(path).toString());
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod(HttpMethod.POST.name());

		con.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		con.setConnectTimeout(getConnectTimeout());
		con.setReadTimeout(getReadTimeout());

		con.setDoOutput(Boolean.TRUE);
		OutputStream os = con.getOutputStream();
		os.write(payload.getBytes());
		os.flush();
		os.close();

		int responseCode = con.getResponseCode();
		if (HttpStatus.valueOf(responseCode).is2xxSuccessful()) {
		    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		    String inputLine;
		    StringBuilder response = new StringBuilder();
		    while ((inputLine = in.readLine()) != null) {
		        response.append(inputLine);
		    }
		    in.close();
		    return response.toString();
		}
		return null;
	}

	protected abstract int getReadTimeout();

	protected abstract int getConnectTimeout();
}
