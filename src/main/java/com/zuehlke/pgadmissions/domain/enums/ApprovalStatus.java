package com.zuehlke.pgadmissions.domain.enums;

import org.apache.commons.lang.StringUtils;



public enum ApprovalStatus {
	
	APPROVED, REJECTED;

	public String displayValue() {		
			return StringUtils.capitalize(this.toString().toLowerCase()); 
	}


}
