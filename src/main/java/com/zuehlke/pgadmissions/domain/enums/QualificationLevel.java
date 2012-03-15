package com.zuehlke.pgadmissions.domain.enums;

public enum QualificationLevel {

	SCHOOL("School"), COLLEGE("College"), UNIVERSITY("University"), PROFESSIONAL(
			"Professional");

	private final String displayValue;

	private QualificationLevel(String displayValue) {
		this.displayValue = displayValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}
	
	public static QualificationLevel fromString(String text) {
	    if (text != null) {
	      for (QualificationLevel b : QualificationLevel.values()) {
	        if (text.equalsIgnoreCase(b.displayValue)) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }

	
}
