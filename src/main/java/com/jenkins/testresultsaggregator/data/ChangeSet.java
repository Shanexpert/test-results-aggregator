package com.jenkins.testresultsaggregator.data;

import java.io.Serializable;
import java.util.List;

public class ChangeSet implements Serializable {
	
	private static final long serialVersionUID = 3121214223665L;
	
	private String _class;
	private String kind;
	
	private List<Object> items;
	
	public List<Object> getItems() {
		return items;
	}
	
	public void setItems(List<Object> items) {
		this.items = items;
	}
	
	public String get_class() {
		return _class;
	}
	
	public void set_class(String _class) {
		this._class = _class;
	}
	
	public String getKind() {
		return kind;
	}
	
	public void setKind(String kind) {
		this.kind = kind;
	}
}
