package com.zuehlke.pgadmissions.domain.enums;

public enum ResidenceStatus {

	NATIONALITY("Nationality"), RIGHT_OF_ABODE("Right of abode"), INDEFINITE_RIGHT_TO_REMAIN("Indefinite right to remain"), 
				EXCEPTIONAL_LEAVE_TO_REMAIN("Exceptional leave to remain"), REFUGEE_STATUS("Refugee Status");
	
	private final String displayValue;
	private String freeVal;
	
	private ResidenceStatus(String displayValue) {
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
	
	public static ResidenceStatus fromString(String text) {
	    if (text != null) {
	      for (ResidenceStatus b : ResidenceStatus.values()) {
	        if (text.equalsIgnoreCase(b.displayValue)) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }
	
	
}
