package com.zuehlke.pgadmissions.domain.enums;

public enum DirectURLsEnum {
	
	ADD_REVIEW("/reviewFeedback?applicationId="), // 
	ADD_INTERVIEW("/interviewFeedback?applicationId="), //
	ADD_REFERENCE("/referee/addReferences?applicationId=")
	;
	//ToDo: add ADD_APPROVAL_FEEDBACK when implemented
	private final String displayValue;

	private DirectURLsEnum(String displayValue) {
		this.displayValue = displayValue;
	}

	public String displayValue() {

		return displayValue;
	}
}
