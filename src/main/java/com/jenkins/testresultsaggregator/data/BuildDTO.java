package com.jenkins.testresultsaggregator.data;

import java.net.URL;

public class BuildDTO {
	
	private String _class;
	private URL url;
	private Integer number;
	
	public BuildDTO() {
		
	}
	
	public BuildDTO(String _class, URL url, Integer number) {
		this._class = _class;
		this.url = url;
		this.number = number;
	}
	
	public String get_class() {
		return _class;
	}
	
	public void set_class(String _class) {
		this._class = _class;
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
