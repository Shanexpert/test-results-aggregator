package com.jenkins.testresultsaggregator.helper;

import java.awt.Color;
import java.io.File;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.common.base.Strings;
import com.jenkins.testresultsaggregator.data.JobStatus;
import com.jenkins.testresultsaggregator.data.ResultsDTO;

import hudson.FilePath;

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
					return colorize(bDays + "D:" + bDours + "h ago", Colors.FAILED);
				} else {
					return colorize(bDours + "h ago", Colors.FAILED);
				}
			}
			if (bDays > 0) {
				if (bDays == 1) {
					return bDays + "D:" + bDours + "h ago";
				}
				return bDays + "D:" + bDours + "h ago";
			} else if (bDours == 0) {
				return currentMin + "m ago";
			} else {
				return currentHours + "h ago";
			}
		}
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
			return colorize(result, Colors.SUCCESS);
		} else if (JobStatus.FAILURE.name().equals(result)) {
			return colorize(result, Colors.FAILED);
		} else if (JobStatus.STILL_FAILING.name().equals(result)) {
			return colorize(result, Colors.FAILED);
		} else if (JobStatus.FIXED.name().equals(result)) {
			return colorize(result, Colors.SUCCESS);
		} else if (JobStatus.UNSTABLE.name().equals(result)) {
			return colorize(result, Colors.UNSTABLE);
		} else if (JobStatus.ABORTED.name().equals(result)) {
			return colorize(result, Colors.ABORTED);
		} else if (JobStatus.STILL_UNSTABLE.name().equals(result)) {
			return colorize(result, Colors.UNSTABLE);
		}
		return result;
	}
	
	public static String countPercentage(ResultsDTO resultsDTO, String prefixPercentage) {
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
			percentage = prefixPercentage + colorize(percentage + "%", Colors.SUCCESS);
		} else if (percentageDouble >= 95) {
			percentage = prefixPercentage + colorize(percentage + "%", Colors.UNSTABLE);
		} else {
			percentage = prefixPercentage + colorize(percentage + "%", Colors.FAILED);
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
	
	public static File createFolder(FilePath filePath, String folder) {
		File theDir = new File(filePath.getRemote(), folder);
		// if the directory does not exist, create it
		if (!theDir.exists()) {
			theDir.mkdir();
		}
		return theDir;
	}
	
	public static String diff(long prev, long curr, boolean list) {
		return diff(prev, curr, null, list);
	}
	
	public static String diff(long prev, long curr, String name) {
		return diff(prev, curr, name, null, false);
	}
	
	public static String diff(long prev, long curr, String name, boolean list) {
		return diff(prev, curr, name, null, list);
	}
	
	public static String diff(long prev, long curr, String name, Color color, boolean list) {
		String namePrefix = null;
		String text = null;
		if (Strings.isNullOrEmpty(name)) {
			// Empty name
			name = "";
		}
		if (!Strings.isNullOrEmpty(name)) {
			namePrefix = name + ":";
		} else {
			namePrefix = name;
		}
		if (color != null) {
			text = colorize(namePrefix, color);
		} else {
			text = namePrefix;
		}
		if (list) {
			if (prev == curr) {
				return "<li>" + text + curr + "</li>";
			} else if (prev < curr) {
				return "<li>" + text + curr + colorize("(+" + (curr - prev) + ")", Colors.BLACK) + "</li>";
			} else { // if (a < b)
				return "<li>" + text + curr + colorize("(-" + (prev - curr) + ")", Colors.BLACK) + "</li>";
			}
		} else {
			if (prev == curr) {
				return text + colorize(curr, Colors.BLACK) + "";
			} else if (prev < curr) {
				return text + colorize(curr, color) + colorize("(+" + (curr - prev) + ")", Colors.BLACK);
			} else { // if (a < b)
				return text + colorize(curr, color) + colorize("(-" + (prev - curr) + ")", Colors.BLACK);
			}
		}
	}
	
	private static String colorize(String text, Color color) {
		if (color == null) {
			color = Colors.BLACK;
		}
		return "<font color='" + Colors.html(color) + "'>" + text + "</font>";
	}
	
	private static String colorize(Long text, Color color) {
		if (color == null || text == 0) {
			color = Colors.BLACK;
		}
		return "<font color='" + Colors.html(color) + "'>" + text + "</font>";
	}
}
