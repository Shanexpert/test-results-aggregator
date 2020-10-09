package com.jenkins.testresultsaggregator.helper;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import com.google.common.base.Strings;
import com.jenkins.testresultsaggregator.data.JobStatus;
import com.jenkins.testresultsaggregator.data.Results;

import hudson.FilePath;
import hudson.remoting.VirtualChannel;

public class Helper {
	
	public static String encodeValue(String value) throws UnsupportedEncodingException, MalformedURLException {
		return java.net.URLEncoder.encode(value, "UTF-8").replace("+", "%20").trim();
	}
	
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
					return colorize(bDays + "d:" + bDours + "h ago", Colors.FAILED);
				} else {
					return colorize(bDours + "h ago", Colors.FAILED);
				}
			}
			if (bDays > 0) {
				if (bDays == 1) {
					return bDays + "d:" + bDours + "h ago";
				}
				return bDays + "d:" + bDours + "h ago";
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
			return "<a href = '" + url + "' ><font color='" + Colors.htmlJOB_NAME_URL() + "'>" + number + "</font></a>";
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
	
	public static Double countPercentage(Results results) {
		String percentage = "0";
		if (results != null && results.getTotal() != 0) {
			try {
				percentage = singDoubleSingle((double) (results.getPass() + results.getSkip()) * 100 / results.getTotal());
				double percentageDouble = 0;
				try {
					percentageDouble = Double.parseDouble(percentage);
				} catch (Exception ex) {
					
				}
				return percentageDouble;
			} catch (Exception ex) {
				
			}
		} else {
			percentage = null;
		}
		return -1D;
	}
	
	public static String countPercentage(int pass, int total) {
		Results results = new Results();
		results.setPass(pass);
		results.setSkip(0);
		results.setTotal(total);
		return countPercentage(results).toString();
	}
	
	/**
	 * Return the percentage of x/y
	 * 
	 * @param pass
	 * @param total
	 * @return Double
	 */
	public static Double countPercentageD(int pass, int total) {
		Results results = new Results();
		results.setPass(pass);
		results.setSkip(0);
		results.setTotal(total);
		return countPercentage(results);
	}
	
	public static String colorizePercentage(double percentageDouble) {
		if (percentageDouble >= 100) {
			return colorize(100 + "%", Colors.SUCCESS);
		} else if (percentageDouble >= 95) {
			return colorize(percentageDouble + "%", Colors.UNSTABLE);
		} else {
			return colorize(percentageDouble + "%", Colors.FAILED);
		}
	}
	
	private static String singDoubleSingle(double value) {
		if (value == 0) {
			return "0";
		} else {
			DecimalFormat df = new DecimalFormat("#.##");
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
	
	public static FilePath createFolder(FilePath filePath, String folder) throws IOException, InterruptedException {
		FilePath fp;
		if (filePath.isRemote()) {
			VirtualChannel channel = filePath.getChannel();
			if (filePath.child(folder).exists()) {
				filePath.child(folder).deleteRecursive();
			}
			fp = new FilePath(channel, filePath.child(folder).getRemote());
		} else {
			if (filePath.child(folder).exists()) {
				filePath.child(folder).deleteRecursive();
			}
			fp = new FilePath(new File(filePath.getRemote(), folder));
		}
		fp.mkdirs();
		return fp;
	}
	
	public static FilePath createFile(FilePath filePath, String filename) throws IOException, InterruptedException {
		FilePath fp;
		if (filePath.isRemote()) {
			VirtualChannel channel = filePath.getChannel();
			fp = new FilePath(channel, filePath.getRemote() + "/" + filename);
		} else {
			fp = new FilePath(new File(filePath.getRemote() + "/" + filename));
		}
		return fp;
	}
	
	public static String diff(long prev, long curr, boolean list) {
		return diff(prev, curr, null, list);
	}
	
	public static String diff(long prev, long curr, String name) {
		return diff(prev, curr, name, null, false, false);
	}
	
	public static String diff(long prev, long curr, String name, boolean list) {
		return diff(prev, curr, name, null, list, false);
	}
	
	public static String diff(long prev, long curr, String name, Color color, boolean list, boolean percentage) {
		String namePrefix = null;
		String text = null;
		if (Strings.isNullOrEmpty(name)) {
			// Empty name
			name = "";
		}
		if (!Strings.isNullOrEmpty(name)) {
			namePrefix = name + ": ";
		} else {
			namePrefix = name;
		}
		if (color != null) {
			text = colorize(namePrefix, color);
		} else {
			text = namePrefix;
		}
		String percentageIcon = "";
		if (percentage) {
			percentageIcon = "%";
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
				if (curr == 0) {
					return "";
				} else {
					return text + colorize(curr, Colors.BLACK) + percentageIcon;
				}
			} else if (prev < curr) {
				if (curr == 0) {
					return text + colorize("+" + (curr - prev), Colors.BLACK) + percentageIcon;
				} else if (prev == 0) {
					return text + colorize(curr, color) + percentageIcon;
				} else {
					return text + colorize(curr, color) + colorize("(+" + (curr - prev) + ")", Colors.BLACK) + percentageIcon;
				}
			} else { // if (a < b)
				if (curr == 0) {
					return text + colorize("-" + (prev - curr), Colors.BLACK) + percentageIcon;
				} else {
					return text + colorize(curr, color) + colorize("(-" + (prev - curr) + ")", Colors.BLACK) + percentageIcon;
				}
			}
		}
	}
	
	private static String colorize(String text, Color color) {
		if (color == null) {
			color = Colors.BLACK;
		}
		if (!Strings.isNullOrEmpty(text)) {
			return "<font color='" + Colors.html(color) + "'>" + text + "</font>";
		}
		return text;
	}
	
	private static String colorize(Long text, Color color) {
		if (color == null || text == 0) {
			color = Colors.BLACK;
		}
		return "<font color='" + Colors.html(color) + "'>" + text + "</font>";
	}
	
	public static String duration(Long millis) {
		Duration duration = Duration.of(millis, ChronoUnit.MILLIS);
		long durationInSeconds = duration.getSeconds();
		long hours = durationInSeconds / 3600;
		long minutes = (durationInSeconds % 3600) / 60;
		String hoursString = "";
		String minString = "";
		if (hours < 10) {
			hoursString = "0" + hours;
		} else {
			hoursString = Long.toString(hours);
		}
		if (minutes < 10) {
			minString = "0" + minutes;
		} else {
			minString = Long.toString(minutes);
		}
		if (hours == 0 && minutes == 0) {
			return null;
		} else if (hours == 0) {
			return "00:" + minString;
		} else {
			return hoursString + ":" + minString;
		}
	}
	
	public static String calculateStatus(String currentResult, String previousResult) {
		if (JobStatus.SUCCESS.name().equals(currentResult) && JobStatus.SUCCESS.name().equals(previousResult)) {
			return JobStatus.SUCCESS.name();
		} else if (JobStatus.SUCCESS.name().equals(currentResult) && JobStatus.FAILURE.name().equals(previousResult)) {
			return JobStatus.FIXED.name();
		} else if (JobStatus.SUCCESS.name().equals(currentResult) && JobStatus.UNSTABLE.name().equals(previousResult)) {
			return JobStatus.FIXED.name();
		} else if (JobStatus.SUCCESS.name().equals(currentResult) && previousResult == null) {
			return JobStatus.SUCCESS.name();
		} else if (JobStatus.UNSTABLE.name().equals(currentResult) && JobStatus.UNSTABLE.name().equals(previousResult)) {
			return JobStatus.STILL_UNSTABLE.name();
		} else if (JobStatus.FAILURE.name().equals(currentResult) && JobStatus.FAILURE.name().equals(previousResult)) {
			return JobStatus.STILL_FAILING.name();
		} else if (JobStatus.FAILURE.name().equals(currentResult)) {
			return JobStatus.FAILURE.name();
		} else if (JobStatus.UNSTABLE.name().equals(currentResult)) {
			return JobStatus.UNSTABLE.name();
		} else if (JobStatus.RUNNING.name().equals(currentResult)) {
			return JobStatus.RUNNING.name();
		} else if (JobStatus.ABORTED.name().equals(currentResult)) {
			return JobStatus.ABORTED.name();
		} else if (JobStatus.SUCCESS.name().equals(currentResult)) {
			return JobStatus.SUCCESS.name();
		} else {
			return currentResult;
		}
	}
}
