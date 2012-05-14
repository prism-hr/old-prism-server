package com.zuehlke.pgadmissions.domain.enums;

import org.apache.commons.lang.StringUtils;



public enum AwareStatus {
	
	YES, NO;

	public String displayValue() {		
			return StringUtils.capitalize(this.toString().toLowerCase()); 
	}
	
	public static AwareStatus fromString(String text) {
	    if (text != null) {
	      for (AwareStatus b : AwareStatus.values()) {
	        if (text.equalsIgnoreCase(b.displayValue())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }


}
