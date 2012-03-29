package com.zuehlke.pgadmissions.domain.enums;

import org.apache.commons.lang.StringUtils;



public enum PrimaryStatus {
	
	YES, NO;

	public String displayValue() {		
			return StringUtils.capitalize(this.toString().toLowerCase()); 
	}
	
	public static PrimaryStatus fromString(String text) {
	    if (text != null) {
	      for (PrimaryStatus b : PrimaryStatus.values()) {
	        if (text.equalsIgnoreCase(b.displayValue())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }


}
