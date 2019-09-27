package com.jenkins.testresultsaggregator.data;

import java.util.List;

public class AggregatedDTO {
	
	private List<DataDTO> data;
	private ResultsDTO results;
	
	public AggregatedDTO() {
	}
	
	public AggregatedDTO(List<DataDTO> data, ResultsDTO results) {
		setData(data);
		setResults(results);
	}
	
	public ResultsDTO getResults() {
		return results;
	}
	
	public void setResults(ResultsDTO results) {
		this.results = results;
	}
	
	public List<DataDTO> getData() {
		return data;
	}
	
	public void setData(List<DataDTO> data) {
		this.data = data;
	}
}
