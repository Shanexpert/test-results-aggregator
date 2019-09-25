package com.jenkins.testresultsaggregator.helper;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Deserialize {
	
	private static ObjectMapper mapper;
	
	private Deserialize() {
		
	}
	
	public static ObjectMapper initializeObjectMapper() {
		mapper = new ObjectMapper()
				.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
				.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
				.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
				.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
				.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper;
	}
	
	public static <T> Object getObject(String objectAsString, Class<T> valueType) throws IOException {
		mapper = initializeObjectMapper();
		T object = mapper.readValue(objectAsString, valueType);
		return object;
	}
}
