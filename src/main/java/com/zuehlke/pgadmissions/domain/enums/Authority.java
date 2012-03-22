package com.zuehlke.pgadmissions.domain.enums;

public enum Authority {
	APPLICANT("Applicant"), REVIEWER("Reviewer"), ADMINISTRATOR("Administrator"), APPROVER("Approver"), SUPERADMINISTRATOR(
			"Superadministrator");

	private final String displayValue;

	private Authority(String displayValue) {
		this.displayValue = displayValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public static Authority getValueAsAuthority(String value) {
		if (value != null) {
			if (value.equals("Administrator")) {
				return ADMINISTRATOR;
			}
			if (value.equals("Applicant")) {
				return APPLICANT;
			}
			if (value.equals("Reviewer")) {
				return REVIEWER;
			}
			if (value.equals("Approver")) {
				return APPROVER;
			}
			if (value.equals("Superadministrator")) {
				return SUPERADMINISTRATOR;
			}
		}
		return null;
	}

}
