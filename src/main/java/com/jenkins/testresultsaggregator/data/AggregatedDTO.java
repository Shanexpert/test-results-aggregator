package com.jenkins.testresultsaggregator.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.jenkins.testresultsaggregator.TestResultsAggregator;

import hudson.model.Run;
import hudson.tasks.test.TestResult;

public class AggregatedDTO extends BaseResult implements Serializable {
	
	private static final long serialVersionUID = 3491974223665L;
	
	private List<DataDTO> data;
	private ResultsDTO results;
	private int countJobRunning = 0;
	private int countJobSuccess = 0;
	private int countJobFailures = 0;
	private int countJobUnstable = 0;
	private int countJobAborted = 0;
	private int countTotal = 0;
	
	private Run<?, ?> owner;
	
	public AggregatedDTO() {
		super(TestResultsAggregator.URL);
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
	
	public Run<?, ?> getRun() {
		return owner;
	}
	
	public void setRun(Run<?, ?> owner) {
		this.owner = owner;
	}
	
	public int getCountTotal() {
		return countJobAborted + countJobUnstable + countJobFailures + countJobSuccess + countJobRunning;
	}
	
	public void setCountTotal(int countTotal) {
		this.countTotal = countTotal;
	}
	
	@Override
	public Collection<? extends TestResult> getChildren() {
		return null;
	}
	
	@Override
	public boolean hasChildren() {
		return false;
	}
	
}
