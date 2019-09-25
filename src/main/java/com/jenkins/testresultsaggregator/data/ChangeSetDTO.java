package com.jenkins.testresultsaggregator.data;

import java.util.List;

public class ChangeSetDTO {

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
