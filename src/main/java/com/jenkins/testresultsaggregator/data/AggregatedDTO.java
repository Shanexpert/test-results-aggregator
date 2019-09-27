package com.jenkins.testresultsaggregator.data;

import java.util.List;

public class AggregatedDTO {
	
	private List<DataDTO> data;
	private ResultsDTO results;
	private int countJobRunning = 0;
	private int countJobSuccess = 0;
	private int countJobFailures = 0;
	private int countJobUnstable = 0;
	private int countJobAborted = 0;
	
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
	
	public int getCountJobRunning() {
		return countJobRunning;
	}
	
	public void setCountJobRunning(int countJobRunning) {
		this.countJobRunning = countJobRunning;
	}
	
	public int getCountJobSuccess() {
		return countJobSuccess;
	}
	
	public void setCountJobSuccess(int countJobSuccess) {
		this.countJobSuccess = countJobSuccess;
	}
	
	public int getCountJobFailures() {
		return countJobFailures;
	}
	
	public void setCountJobFailures(int countJobFailures) {
		this.countJobFailures = countJobFailures;
	}
	
	public int getCountJobUnstable() {
		return countJobUnstable;
	}
	
	public void setCountJobUnstable(int countJobUnstable) {
		this.countJobUnstable = countJobUnstable;
	}
	
	public int getCountJobAborted() {
		return countJobAborted;
	}
	
	public void setCountJobAborted(int countJobAborted) {
		this.countJobAborted = countJobAborted;
	}
}
