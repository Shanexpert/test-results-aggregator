package com.jenkins.testresultsaggregator.helper;

import java.util.ResourceBundle;

public enum LocalMessages {
	REPORT_DISPLAYNAME ("displayName"),
	START_AGGREGATE ("startAggregate"),
	FINISHED_AGGREGATE ("finishedAggregate"),
	ERROR_OCCURRED ("errorOccurred"),
	VALIDATION_POSITIVE_NUMBER ("validationPositiveNumber"),
	VALIDATION_INTEGER_NUMBER ("validationIntegerNumber"),
	SUCCESS ("success"),
	GENERATE ("generate"),
	ANALYZE ("analyze"),
	FINISHED ("finished"),
	HTML_REPORT ("htmlReport"),
	EMAIL_REPORT ("emailReport"),
	VALIDATION_MAIL_NOT_FOUND_JOBS ("validation_mail_not_found_jobs"),
	VALIDATION_MAIL_RECEIPIENTS_EMPTY ("validation_mail_receipients_empty"),
	VALIDATION_MAIL_SMTP_ISSUE ("validation_mail_smtp_issue"),
	SEND_MAIL_TO ("sendMailTo"),
	COLLECT_DATA ("collectData"),
	JOB_NOT_FOUND ("jobNotFound"),
	JOB_IS_DISABLED ("jobIsDisabled"),
	RESULTS_RUNNING ("resultsRunning"),
	RESULTS_SUCCESS ("resultsSuccess"),
	RESULTS_FAILED ("resultsFailed"),
	RESULTS_UNSTABLE ("resultsUnstable"),
	RESULTS_ABORTED ("resultsAborted"),
	COLUMN_GROUP ("columnGroup"),
	COLUMN_GROUP_STATUS ("columnGroupStatus"),
	COLUMN_JOB ("columnJob"),
	COLUMN_JOB_STATUS ("columnJobStatus"),
	COLUMN_TESTS ("columnTests"),
	COLUMN_PASS ("columnPass"),
	COLUMN_FAIL ("columnFail"),
	COLUMN_SKIP ("columnSkip"),
	COLUMN_LAST_RUN ("columnLastRun"),
	COLUMN_COMMITS ("columnCommits"),
	COLUMN_DURATION ("columnDuration"),
	COLUMN_DESCRIPTION ("columnDescription"),
	COLUMN_PERCENTAGE ("columnPercentage"),
	XML_REPORT ("xmlReport");
	
	private final static ResourceBundle MESSAGES = ResourceBundle.getBundle("com.jenkins.testresultsaggregator.Messages");
	private final String msgRef;
	
	private LocalMessages(final String msgReference) {
		msgRef = msgReference;
	}
	
	@Override
	public String toString() {
		return MESSAGES.getString(msgRef);
	}
}
