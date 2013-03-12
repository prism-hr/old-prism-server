package com.zuehlke.pgadmissions.domain.enums;

import org.apache.commons.lang.StringUtils;

public enum ApplicationFormStatus {
	// attention: order of the enum values is also the logical ordering of application-status
	UNSUBMITTED("Not Submitted"), // 
	VALIDATION("Validation"), // 
	REVIEW("Review"), //
	INTERVIEW("Interview"), //
	APPROVAL("Approval"), //
	APPROVED("Approved"), //
	WITHDRAWN("Withdrawn"), //
	REJECTED("Rejected"), //
	REQUEST_RESTART_APPROVAL("Restart of approval"); //

	private final String displayValue;

	private ApplicationFormStatus(String displayValue) {
		this.displayValue = displayValue;
	}

	public String displayValue() {
		return displayValue;
	}

	public static ApplicationFormStatus[] getAvailableNextStati(ApplicationFormStatus status) {
		if (status == VALIDATION || status == REVIEW) {
			return new ApplicationFormStatus[] { REVIEW,  INTERVIEW, APPROVAL, REJECTED };
		}
		if (status == INTERVIEW) {
			return new ApplicationFormStatus[] {    INTERVIEW, APPROVAL, REJECTED };
		}
		if (status == APPROVAL) {
			return new ApplicationFormStatus[] {APPROVED, REJECTED, REQUEST_RESTART_APPROVAL};
		}
		return new ApplicationFormStatus[] {};
	}

	public static ApplicationFormStatus[] getConfigurableStages() {
		return new ApplicationFormStatus[] { VALIDATION, REVIEW,  INTERVIEW ,APPROVAL};
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
	    if (StringUtils.containsIgnoreCase(ApplicationFormStatus.REQUEST_RESTART_APPROVAL.displayValue, searchStr)) {
	        return REQUEST_RESTART_APPROVAL;
	    }
	    return null;
	}
}
