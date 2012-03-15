package com.zuehlke.pgadmissions.domain.enums;

public enum ResidenceStatus {

	NATIONALITY("Nationality"), RIGHT_OF_ABODE("Right of abode"), INDEFINITE_RIGHT_TO_REMAIN("Indefinite right to remain"), EXCEPTIONAL_LEAVE_TO_REMAIN(
			"Exceptional leave to remain"), REFUGEE_STATUS("Refugee Status");

	private final String displayValue;

	private ResidenceStatus(String displayValue) {
		this.displayValue = displayValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	
}
