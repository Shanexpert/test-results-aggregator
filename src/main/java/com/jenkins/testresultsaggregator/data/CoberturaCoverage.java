package com.jenkins.testresultsaggregator.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CoberturaCoverage {
	
	@JsonProperty(value = "_class")
	private String classString;
	private CoberturaResult results;
	
	public static class CoberturaResult {
		private List<Element> elements;
		
		public List<Element> getElements() {
			return elements;
		}
		
		public void setElements(List<Element> elements) {
			this.elements = elements;
		}
	}
	
	public static class Element {
		private int denominator;
		private String name;
		private int numerator;
		private Double ratio;
		
		public int getDenominator() {
			return denominator;
		}
		
		public void setDenominator(int denominator) {
			this.denominator = denominator;
		}
		
		public int getNumerator() {
			return numerator;
		}
		
		public void setNumerator(int numerator) {
			this.numerator = numerator;
		}
		
		public Double getRatio() {
			return ratio;
		}
		
		public void setRatio(Double ratio) {
			this.ratio = ratio;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
	}
	
	public CoberturaResult getResults() {
		return results;
	}
	
	public void setResults(CoberturaResult results) {
		this.results = results;
	}
}
