package com.zuehlke.pgadmissions.utils;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

@Component
public class ApplicationQueryStringParser {

	public Map<String,String> parse(String queryString) {
		String[] parts = queryString.split("\\|\\|");
		Map<String,String> parameterValues = Maps.newHashMapWithExpectedSize(parts.length);
		for (String part : parts) {
			int splitIndex = part.indexOf(":");
			if(splitIndex > 0){
				String key = part.substring(0,splitIndex);
				String value = part.substring(splitIndex+1,part.length());
				parameterValues.put(key, value);
			}
		}
		return parameterValues;
	}

}
