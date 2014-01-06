package com.zuehlke.pgadmissions.domain.enums;

public enum CommentPropertyValue {

	YES("Yes"), 
	NO("No"), 
	UNSURE("Unsure"),
	HOME("Home/EU"), 
	OVERSEAS("Overseas");

	private final String displayValue;

	private CommentPropertyValue(String displayValue){	
		this.displayValue = displayValue;
	}

	public String display() {
		return displayValue;
	}
	
}