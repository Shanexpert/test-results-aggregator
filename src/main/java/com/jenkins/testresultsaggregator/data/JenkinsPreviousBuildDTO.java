package com.jenkins.testresultsaggregator.data;

import java.net.URL;

public class JenkinsPreviousBuildDTO {

	private int number;
	private URL url;

	public JenkinsPreviousBuildDTO() {

	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}
}
