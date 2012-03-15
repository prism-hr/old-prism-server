package com.zuehlke.pgadmissions.domain.enums;

import org.apache.commons.lang.StringUtils;



public enum AddressStatus {
	
	YES, NO;

	public String displayValue() {		
			return StringUtils.capitalize(this.toString().toLowerCase()); 
	}
	
	public static AddressStatus fromString(String text) {
	    if (text != null) {
	      for (AddressStatus b : AddressStatus.values()) {
	        if (text.equalsIgnoreCase(b.displayValue())) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }


}
