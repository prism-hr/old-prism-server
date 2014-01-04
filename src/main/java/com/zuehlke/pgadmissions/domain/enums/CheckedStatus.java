package com.zuehlke.pgadmissions.domain.enums;

import org.apache.commons.lang.StringUtils;

@Deprecated
public enum CheckedStatus {

	YES, 
	NO;

	public String displayValue() {
		return StringUtils.capitalize(this.toString().toLowerCase());
	}

	public static CheckedStatus fromString(String text) {
		if (text != null) {
			for (CheckedStatus b : CheckedStatus.values()) {
				if (text.equalsIgnoreCase(b.displayValue())) {
					return b;
				}
			}
		}
		return null;
	}

}