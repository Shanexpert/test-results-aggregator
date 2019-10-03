package com.jenkins.testresultsaggregator.helper;

import java.awt.Color;

public class Colors {
	
	public static final Color SUCCESS = Color.GREEN;
	public static final Color ABORTED = Color.GRAY;
	public static final Color FAILED = Color.RED;
	public static final Color RUNNING = Color.LIGHT_GRAY;
	public static final Color UNSTABLE = Color.YELLOW;
	
	private static String getHTMLColorString(Color color) {
		String red = Integer.toHexString(color.getRed());
		String green = Integer.toHexString(color.getGreen());
		String blue = Integer.toHexString(color.getBlue());
		return "#" +
				(red.length() == 1 ? "0" + red : red) +
				(green.length() == 1 ? "0" + green : green) +
				(blue.length() == 1 ? "0" + blue : blue);
	}
	
	public static String html(Color color) {
		return getHTMLColorString(color);
	}
	
	public static String htmlSUCCESS() {
		return getHTMLColorString(SUCCESS);
	}
	
	public static String htmlABORTED() {
		return getHTMLColorString(ABORTED);
	}
	
	public static String htmlFAILED() {
		return getHTMLColorString(FAILED);
	}
	
	public static String htmlRUNNING() {
		return getHTMLColorString(RUNNING);
	}
	
	public static String htmlUNSTABLE() {
		return getHTMLColorString(UNSTABLE);
	}
}
