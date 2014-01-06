package com.zuehlke.pgadmissions.domain.enums;

public enum DurationUnitEnum {
	DAYS("Days", "DAY"), 
	WEEKS("Weeks", "WEEK");
	
	private final String displayValue;
	
	private final String sqlValue;

	private DurationUnitEnum(String displayValue, String sqlValue) {
		this.displayValue = displayValue;
		this.sqlValue = sqlValue;
	}

	public String displayValue() {
		return displayValue;
	}
	
	public String sqlValue() {
        return sqlValue;
    }

}
