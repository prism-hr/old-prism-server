package com.zuehlke.pgadmissions.domain.enums;

public enum DirectURLsEnum {
	
	ADD_REVIEW("/reviewFeedback?applicationId="), // 
	ADD_INTERVIEW("/interviewFeedback?applicationId="), // 
	ADD_REFERENCE("/referee/addReferences?application="), //
	;
	
	private final String displayValue;

	private DirectURLsEnum(String displayValue) {
		this.displayValue = displayValue;
	}

	public String displayValue() {

		return displayValue;
	}
}
