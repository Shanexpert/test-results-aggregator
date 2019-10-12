package com.jenkins.testresultsaggregator.data;

public enum JobStatus {
	
	ABORTED ("1"),
	FAILURE ("2"),
	STILL_FAILING ("3"),
	UNSTABLE ("4"),
	STILL_UNSTABLE ("5"),
	RUNNING ("6"),
	FIXED ("7"),
	SUCCESS ("8"),
	DISABLED ("9"),
	NOT_FOUND ("10");
	
	private String myLocator;
	
	private JobStatus(String locator) {
		myLocator = locator;
	}
	
	public int getPriority() {
		return Integer.parseInt(myLocator);
	}
	
}
