package com.jenkins.testresultsaggregator.helper;

import java.awt.Color;

public class Colors {
	
	public static final Color BLACK = Color.BLACK;
	public static final Color SUCCESS = new Color(8, 150, 8);// Color.GREEN;
	public static final Color ABORTED = new Color(115, 115, 115);// Color.DARK_GRAY;
	public static final Color FAILED = new Color(245, 15, 25);// Color.RED;
	public static final Color RUNNING = new Color(0, 25, 235);// Color.BLUE;
	public static final Color UNSTABLE = new Color(250, 150, 10); // Color.ORANGE;
	
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
	
	public static String htmlBlack() {
		return getHTMLColorString(BLACK);
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
