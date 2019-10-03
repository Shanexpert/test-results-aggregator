package com.jenkins.testresultsaggregator.data;

import com.jenkins.testresultsaggregator.helper.Helper;

public class ResultsDTO {
	
	private String name;
	
	private String calculatedPass;
	private int pass;
	private int passDif;
	
	private String calculatedFail;
	private int fail;
	private int failDif;
	
	private String calculatedSkip;
	private int skip;
	private int skipDif;
	
	private String calculatedTotal;
	private int total;
	private int totalDif;
	
	private String currentResult;
	private String previousResult;
	
	private int number;
	private long duration;
	private String description;
	private boolean building;
	
	private String reportUrl;
	private String consoleUrl;
	
	private int numberOfChanges;
	private String changesUrl;
	private String timestamp;
	
	public ResultsDTO() {
		setPass(0);
		setPassDif(0);
		
		setFail(0);
		setFailDif(0);
		
		setSkip(0);
		setSkipDif(0);
		
		setTotal(0);
		setTotalDif(0);
	}
	
	public ResultsDTO(String currentResult, String previousResult) {
		setCurrentResult(currentResult);
		setPreviousResult(previousResult);
		
		setPass(0);
		setPassDif(0);
		
		setFail(0);
		setFailDif(0);
		
		setSkip(0);
		setSkipDif(0);
		
		setTotal(0);
		setTotalDif(0);
	}
	
	public ResultsDTO sub(ResultsDTO resultsDTO) {
		this.setTotal(this.getTotal() + resultsDTO.getTotal());
		this.setTotalDif(this.getTotalDif() + resultsDTO.getTotalDif());
		
		this.setFail(this.getFail() + resultsDTO.getFail());
		this.setFailDif(this.getFailDif() + resultsDTO.getFailDif());
		
		this.setPass(this.getPass() + resultsDTO.getPass());
		this.setPassDif(this.getPassDif() + resultsDTO.getPassDif());
		
		this.setSkip(this.getSkip() + resultsDTO.getSkip());
		this.setSkipDif(this.getSkipDif() + resultsDTO.getSkipDif());
		return this;
	}
	
	public int getPass() {
		return pass;
	}
	
	public void setPass(int pass) {
		this.pass = pass;
	}
	
	public int getPassDif() {
		return passDif;
	}
	
	public void setPassDif(int passDif) {
		this.passDif = passDif;
	}
	
	public int getFail() {
		return fail;
	}
	
	public void setFail(int fail) {
		this.fail = fail;
	}
	
	public int getFailDif() {
		return failDif;
	}
	
	public void setFailDif(int failDif) {
		this.failDif = failDif;
	}
	
	public int getSkip() {
		return skip;
	}
	
	public void setSkip(int skip) {
		this.skip = skip;
	}
	
	public int getSkipDif() {
		return skipDif;
	}
	
	public void setSkipDif(int skipDif) {
		this.skipDif = skipDif;
	}
	
	public int getTotal() {
		return total;
	}
	
	public void setTotal(int total) {
		this.total = total;
	}
	
	public int getTotalDif() {
		return totalDif;
	}
	
	public void setTotalDif(int totalDif) {
		this.totalDif = totalDif;
	}
	
	public String getCurrentResult() {
		return currentResult;
	}
	
	public void setCurrentResult(String currentResult) {
		this.currentResult = currentResult;
	}
	
	public String getPreviousResult() {
		return previousResult;
	}
	
	public void setPreviousResult(String previousResult) {
		this.previousResult = previousResult;
	}
	
	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public long getDuration() {
		return duration;
	}
	
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isBuilding() {
		return building;
	}
	
	public void setBuilding(boolean building) {
		this.building = building;
	}
	
	public String getReportUrl() {
		return reportUrl;
	}
	
	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}
	
	public String getConsoleUrl() {
		return consoleUrl;
	}
	
	public void setConsoleUrl(String consoleUrl) {
		this.consoleUrl = consoleUrl;
	}
	
	public int getNumberOfChanges() {
		return numberOfChanges;
	}
	
	public void setNumberOfChanges(int numberOfChanges) {
		this.numberOfChanges = numberOfChanges;
	}
	
	public String getChangesUrl() {
		return changesUrl;
	}
	
	public void setChangesUrl(String changesUrl) {
		this.changesUrl = changesUrl;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCalculatedPass() {
		return getPass() + Helper.singInteger(getPassDif());
	}
	
	public void setCalculatedPass(String calculatedPass) {
		this.calculatedPass = calculatedPass;
	}
	
	public String getCalculatedTotal() {
		return getTotal() + Helper.singInteger(getTotalDif());
	}
	
	public void setCalculatedTotal(String calculatedTotal) {
		this.calculatedTotal = calculatedTotal;
	}
	
	public String getCalculatedSkip() {
		return getSkip() + Helper.singInteger(getSkipDif());
	}
	
	public void setCalculatedSkip(String calculatedSkip) {
		this.calculatedSkip = calculatedSkip;
	}
	
	public String getCalculatedFail() {
		return getFail() + Helper.singInteger(getFailDif());
	}
	
	public String getCalculatedFailColor() {
		return Helper.colorizeFailResult(getFail()) + Helper.singInteger(getFailDif());
	}
	
	public void setCalculatedFail(String calculatedFail) {
		this.calculatedFail = calculatedFail;
	}
	
}
