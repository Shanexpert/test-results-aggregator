package com.jenkins.testresultsaggregator.data;

import java.io.Serializable;

import com.jenkins.testresultsaggregator.helper.Colors;
import com.jenkins.testresultsaggregator.helper.Helper;

public class Results implements Serializable {
	
	private static final long serialVersionUID = 3491974223667L;
	
	private String name;
	
	private int pass;
	private int passDif;
	
	private int fail;
	private int failDif;
	
	private int skip;
	private int skipDif;
	
	private int total;
	private int totalDif;
	
	private int ccPackages;
	private int ccPackagesDif;
	
	private int ccFiles;
	private int ccFilesDif;
	
	private int ccClasses;
	private int ccClassesDif;
	
	private int ccMethods;
	private int ccMethodsDif;
	
	private int ccLines;
	private int ccLinesDif;
	
	private int ccConditions;
	private int ccConditionsDif;
	
	private String currentResult;
	private String previousResult;
	private String status;
	
	private int number;
	private Long duration;
	private String description;
	private boolean building;
	
	private String url;
	private String sonarUrl;
	
	private int calculatedNumberOfChanges;
	private int numberOfChanges;
	
	private String changesUrl;
	private String timestamp;
	
	private Double percentage;
	
	public Results() {
		setPass(0);
		setPassDif(0);
		
		setFail(0);
		setFailDif(0);
		
		setSkip(0);
		setSkipDif(0);
		
		setTotal(0);
		setTotalDif(0);
	}
	
	public Results(String currentResult, String previousResult) {
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
	
	public Results addResults(Results resultsDTO) {
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
	
	public Long getDuration() {
		return duration;
	}
	
	public void setDuration(Long duration) {
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
		return Helper.diff(getPassDif(), getPass(), false);
	}
	
	public String getCalculatedTotal() {
		return Helper.diff(getTotalDif(), getTotal(), false);
	}
	
	public String getCalculatedSkip() {
		return Helper.diff(getSkipDif(), getSkip(), false);
	}
	
	public String getCalculatedFail() {
		return Helper.diff(getFailDif(), getFail(), false);
	}
	
	public String getCalculatedFailColor() {
		return Helper.diff(getFailDif(), getFail(), null, Colors.FAILED, false, false);
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public Double getPercentage() {
		return percentage;
	}
	
	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}
	
	public String getStatus() {
		status = Helper.calculateStatus(currentResult, previousResult);
		return status;
	}
	
	public int getCalculatedNumberOfChanges() {
		return calculatedNumberOfChanges;
	}
	
	public void setCalculatedNumberOfChanges(int calculatedNumberOfChanges) {
		this.calculatedNumberOfChanges = calculatedNumberOfChanges;
	}
	
	public String getCalculatedCcPackage() {
		return Helper.diff(getCcPackagesDif(), getCcPackages(), false);
	}
	
	public int getCcPackages() {
		return ccPackages;
	}
	
	public void setCcPackages(int ccPackages) {
		this.ccPackages = ccPackages;
	}
	
	public int getCcPackagesDif() {
		return ccPackagesDif;
	}
	
	public void setCcPackagesDif(int ccPackagesDif) {
		this.ccPackagesDif = ccPackagesDif;
	}
	
	public String getCalculatedCcFiles() {
		return Helper.diff(getCcFilesDif(), getCcFiles(), false);
	}
	
	public int getCcFiles() {
		return ccFiles;
	}
	
	public void setCcFiles(int ccFiles) {
		this.ccFiles = ccFiles;
	}
	
	public int getCcFilesDif() {
		return ccFilesDif;
	}
	
	public void setCcFilesDif(int ccFilesDif) {
		this.ccFilesDif = ccFilesDif;
	}
	
	public String getCalculatedCcClasses() {
		return Helper.diff(getCcClassesDif(), getCcClasses(), false);
	}
	
	public int getCcClasses() {
		return ccClasses;
	}
	
	public void setCcClasses(int ccClasses) {
		this.ccClasses = ccClasses;
	}
	
	public int getCcClassesDif() {
		return ccClassesDif;
	}
	
	public void setCcClassesDif(int ccClassesDif) {
		this.ccClassesDif = ccClassesDif;
	}
	
	public String getCalculatedCcMethods() {
		return Helper.diff(getCcMethodsDif(), getCcMethods(), false);
	}
	
	public int getCcMethods() {
		return ccMethods;
	}
	
	public void setCcMethods(int ccMethods) {
		this.ccMethods = ccMethods;
	}
	
	public int getCcMethodsDif() {
		return ccMethodsDif;
	}
	
	public void setCcMethodsDif(int ccMethodsDif) {
		this.ccMethodsDif = ccMethodsDif;
	}
	
	public String getCalculatedCcLines() {
		return Helper.diff(getCcLinesDif(), getCcLines(), false);
	}
	
	public int getCcLines() {
		return ccLines;
	}
	
	public void setCcLines(int ccLines) {
		this.ccLines = ccLines;
	}
	
	public int getCcLinesDif() {
		return ccLinesDif;
	}
	
	public void setCcLinesDif(int ccLinesDif) {
		this.ccLinesDif = ccLinesDif;
	}
	
	public String getCalculatedCcConditions() {
		return Helper.diff(getCcConditionsDif(), getCcConditions(), false);
	}
	
	public int getCcConditions() {
		return ccConditions;
	}
	
	public void setCcConditions(int ccConditions) {
		this.ccConditions = ccConditions;
	}
	
	public int getCcConditionsDif() {
		return ccConditionsDif;
	}
	
	public void setCcConditionsDif(int ccConditionsDif) {
		this.ccConditionsDif = ccConditionsDif;
	}
	
	public String getSonarUrl() {
		return sonarUrl;
	}
	
	public void setSonarUrl(String sonarUrl) {
		this.sonarUrl = sonarUrl;
	}
	
}
