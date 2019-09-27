package com.jenkins.testresultsaggregator.helper;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.common.base.Strings;
import com.jenkins.testresultsaggregator.data.JobStatus;
import com.jenkins.testresultsaggregator.data.ResultsDTO;

public class Helper {
	
	public static String getTimeStamp(String timeStamp) {
		if (Strings.isNullOrEmpty(timeStamp)) {
			return "";
		} else {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss:SSS");
			LocalDateTime date = LocalDateTime.parse(timeStamp, formatter);
			return date.toString();
		}
	}
	
	public static String getTimeStamp(String outOfDateResults, String timeStamp) {
		if (Strings.isNullOrEmpty(timeStamp)) {
			return "";
		} else {
			int outOfDate = Integer.parseInt(outOfDateResults) * 3600;
			LocalDateTime today = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss:SSS");
			LocalDateTime date = LocalDateTime.parse(timeStamp, formatter);
			Duration d = Duration.between(date, today);
			long currentHours = d.getSeconds() / 3600;
			long currentMin = d.getSeconds() / 60;
			long bDours = currentHours % 24;
			long bDays = currentHours / 24;
			if (d.getSeconds() > outOfDate) {
				if (bDays > 0) {
					return "<font color='red'>" + bDays + " Days and" + bDours + " hours ago</font>";
				} else {
					return "<font color='red'>" + bDours + " hours ago</font>";
				}
			}
			if (bDays > 0) {
				if (bDays == 1) {
					return bDays + " Day and " + bDours + " hours ago";
				}
				return bDays + " Days and " + bDours + " hours ago";
			} else if (bDours == 0) {
				return currentMin + " minutes ago";
			} else {
				if (currentHours == 1) {
					return currentHours + " hour ago";
				}
				return currentHours + " hours ago";
			}
		}
	}
	
	public static String singInteger(int value) {
		if (value < 0) {
			return "(" + Integer.toString(value) + ")";
		} else if (value > 0) {
			return "(+" + value + ")";
		} else {
			return "";
		}
	}
	
	public static String colorizeFailResult(int result) {
		if (result > 0) {
			return "<b><font color='red'>" + result + "</font></b>";
		}
		return Integer.toString(result);
	}
	
	public static String getNumber(int value) {
		if (value < 0) {
			return Integer.toString(value);
		} else if (value > 0) {
			return Integer.toString(value);
		} else {
			return "";
		}
	}
	
	public static String urlNumberofChanges(String url, String number) {
		if (!number.isEmpty()) {
			return "<a href = '" + url + "'>" + number + "</a>";
		}
		return "";
	}
	
	public static String colorizeResultStatus(String result) {
		if (JobStatus.SUCCESS.name().equals(result)) {
			return "<font color='green'>" + result + "</font>";
		} else if (JobStatus.FAILURE.name().equals(result)) {
			return "<font color='red'><b>" + result + "</b></font>";
		} else if (JobStatus.STILL_FAILING.name().equals(result)) {
			return "<font color='red'><b>" + result + "</b></font>";
		} else if (JobStatus.FIXED.name().equals(result)) {
			return "<font color='green'>" + result + "</font>";
		} else if (JobStatus.UNSTABLE.name().equals(result)) {
			return "<font color='orange'><b>" + result + "</b></font>";
		} else if (JobStatus.STILL_UNSTABLE.name().equals(result)) {
			return "<font color='orange'><b>" + result + "</b></font>";
		}
		return result;
	}
	
	public static String countPercentage(ResultsDTO resultsDTO) {
		String percentage = "0";
		try {
			percentage = singDoubleSingle((resultsDTO.getPass() + resultsDTO.getSkip()) * 100 / resultsDTO.getTotal());
		} catch (Exception ex) {
			
		}
		double percentageDouble = 0;
		try {
			percentageDouble = Double.parseDouble(percentage);
		} catch (Exception ex) {
			
		}
		if (percentageDouble >= 100) {
			percentage = "Pass Rate : <font color='green'>" + percentage + "%" + "</font>";
		} else if (percentageDouble >= 95) {
			percentage = "Pass Rate : <font color='orange'>" + percentage + "%" + "</font>";
		} else {
			percentage = "Pass Rate : <font color='red'>" + percentage + "%" + "</font>";
		}
		return percentage;
	}
	
	private static String singDoubleSingle(double value) {
		DecimalFormat df = new DecimalFormat("#.####");
		String valueAsString = df.format(value);
		value = Double.valueOf(valueAsString);
		if (Math.abs(value) < 0.005) {
			return "";
		} else if (Math.abs(value) == 0) {
			return "";
		} else if (value < 0.00) {
			return df.format(value);
		} else if (value > 0) {
			return df.format(value);
		} else {
			return "";
		}
	}
}
