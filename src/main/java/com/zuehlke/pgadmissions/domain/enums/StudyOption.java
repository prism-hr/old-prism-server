package com.zuehlke.pgadmissions.domain.enums;

public enum StudyOption {
	
	FULL_TIME("Full time"), FULL_TIME_DISTANCE("Full time by distance"), PART_TIME("Part time"), PART_TIME_DISTANCE("Part time by distance");

	private final String displayValue;
	private String freeVal;

	private StudyOption(String displayValue) {
		this.displayValue = displayValue;
		this.freeVal = displayValue;
	}

	public String displayValue() {
		return displayValue;
	}
	
	public String getFreeVal() {
		return freeVal;
	}
	
	public void setFreeVal(String freeVal) {
		this.freeVal = freeVal;
	}
	
	public static StudyOption fromString(String text) {
	    if (text != null) {
	      for (StudyOption b : StudyOption.values()) {
	        if (text.equalsIgnoreCase(b.displayValue)) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }
}
