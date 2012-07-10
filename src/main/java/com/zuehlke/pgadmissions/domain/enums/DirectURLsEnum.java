package com.zuehlke.pgadmissions.domain.enums;

public enum DirectURLsEnum {
	
	ADD_REVIEW("/reviewFeedback?applicationId="), // 
	VIEW_APPLIATION_PRIOR_TO_INTERVIEW("/application?view=view&applicationId="), //
	VIEW_APPLIATION_AS_SUPERVISOR("/application?view=view&applicationId="), //
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
