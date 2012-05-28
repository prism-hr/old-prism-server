package com.zuehlke.pgadmissions.domain.enums;

public enum DirectURLsEnum {
	
	ADD_REVIEW("/pgadmissions/reviewFeedback?applicationId="), // 
	ADD_INTERVIEW("/pgadmissions/interviewFeedback?applicationId="), // 
	ADD_REFERENCE("/pgadmissions/referee/addReferences?application="), //
	;
	
	private final String displayValue;

	private DirectURLsEnum(String displayValue) {
		this.displayValue = displayValue;
	}

	public String displayValue() {

		return displayValue;
	}
}
