package com.zuehlke.pgadmissions.domain.enums;

import org.apache.commons.lang.StringUtils;

public enum ApplicationFormStatus {
	
    UNSUBMITTED("Not Submitted"),
	
	VALIDATION("Validation"),
	
	REVIEW("Review"),
	
	INTERVIEW("Interview"), 
	
	APPROVAL("Approval"),
	
	APPROVED("Offer Recommended"),
	
	WITHDRAWN("Withdrawn"),
	
	REJECTED("Rejected");

	private final String displayValue;

	private ApplicationFormStatus(final String displayValue) {
		this.displayValue = displayValue;
	}

	public String displayValue() {
		return displayValue;
	}

	public static ApplicationFormStatus convert(String searchStr) {
	    if (StringUtils.containsIgnoreCase(ApplicationFormStatus.UNSUBMITTED.displayValue, searchStr)) {
	        return UNSUBMITTED;
	    }
	    if (StringUtils.containsIgnoreCase(ApplicationFormStatus.VALIDATION.displayValue, searchStr)) {
	        return VALIDATION;
	    }
	    if (StringUtils.containsIgnoreCase(ApplicationFormStatus.REVIEW.displayValue, searchStr)) {
	        return REVIEW;
	    }
	    if (StringUtils.containsIgnoreCase(ApplicationFormStatus.INTERVIEW.displayValue, searchStr)) {
	        return INTERVIEW;
	    }
	    if (StringUtils.containsIgnoreCase(ApplicationFormStatus.APPROVAL.displayValue, searchStr)) {
	        return APPROVAL;
	    }
	    if (StringUtils.containsIgnoreCase(ApplicationFormStatus.APPROVED.displayValue, searchStr)) {
	        return APPROVED;
	    }
	    if (StringUtils.containsIgnoreCase(ApplicationFormStatus.WITHDRAWN.displayValue, searchStr)) {
	        return WITHDRAWN;
	    }
	    if (StringUtils.containsIgnoreCase(ApplicationFormStatus.REJECTED.displayValue, searchStr)) {
	        return REJECTED;
	    }
	    return null;
	}
	
}
