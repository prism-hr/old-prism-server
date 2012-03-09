package com.zuehlke.pgadmissions.domain.enums;

public enum Gender {

	FEMALE("Female"), MALE("Male"), PREFER_NOT_TO_SAY("Prefer not to say"); 
	
	private final String displayValue;
	
	private Gender(String displayValue) {
		this.displayValue = displayValue;
	}

	public String displayValue() {

		return displayValue;
	}
	
	 public static Gender fromString(String text) {
		    if (text != null) {
		      for (Gender b : Gender.values()) {
		        if (text.equalsIgnoreCase(b.displayValue)) {
		          return b;
		        }
		      }
		    }
		    return null;
		  }
}
