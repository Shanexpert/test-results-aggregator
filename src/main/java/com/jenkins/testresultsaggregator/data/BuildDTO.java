package com.jenkins.testresultsaggregator.data;

import java.io.Serializable;
import java.net.URL;

public class BuildDTO implements Serializable {
	
	private static final long serialVersionUID = 3491L;
	
	private URL url;
	private Integer number;
	
	public BuildDTO() {
		
	}
	
	public BuildDTO(URL url, Integer number) {
		this.url = url;
		this.number = number;
	}
	
	public URL getUrl() {
		return url;
	}
	
	public void setUrl(URL url) {
		this.url = url;
	}
	
	public Integer getNumber() {
		return number;
	}
	
	public void setNumber(Integer number) {
		this.number = number;
	}
	
}
