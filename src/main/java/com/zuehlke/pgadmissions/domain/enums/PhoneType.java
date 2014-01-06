package com.zuehlke.pgadmissions.domain.enums;

import org.apache.commons.lang.StringUtils;

public enum PhoneType {
	
	HOME, 
	WORK, 
	MOBILE;

	public String getDisplayValue() {
		return StringUtils.capitalize(this.toString().toLowerCase());
	}

}