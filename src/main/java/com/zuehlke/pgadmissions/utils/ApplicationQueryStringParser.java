package com.zuehlke.pgadmissions.utils;

import org.springframework.stereotype.Component;

@Component
public class ApplicationQueryStringParser {

	public String[] parse(String queryString) {
		String[] parts = queryString.split("\\|\\|");
		String[] params = new String[4];
		for (String part : parts) {
			
			if (part.startsWith("program:")) {
				params[0] = part.substring("program:".length());
			}
			if (part.startsWith("programhome:")) {
				String url = part.substring("programhome:".length());
				if(!url.startsWith("http://") && !url.startsWith("https://")){
					url = "http://" +  url;
				}
				params[1] = url;
			}
			if (part.startsWith("bacthdeadline:")) {
				params[2] = part.substring("bacthdeadline:".length());
			}
			if (part.startsWith("projectTitle:")) {
				int startIndex = queryString.indexOf("projectTitle:") ;
				String projectPartOfQueryString = queryString.substring(startIndex);
				params[3] = projectPartOfQueryString.substring("projectTitle:".length());
			}
		}
		return params;
	}

}
