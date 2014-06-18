package com.zuehlke.pgadmissions.domain.enums;

public enum DurationUnit {
	DAYS("Days", "DAY"), 
	WEEKS("Weeks", "WEEK"),
	MONTHS("Months", "MONTH"),
	YEARS("Years", "YEAR");
	
	private final String displayValue;
	
	private final String sqlValue;

	private DurationUnit(String displayValue, String sqlValue) {
		this.displayValue = displayValue;
		this.sqlValue = sqlValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}
	
	public String getSqlValue() {
        return sqlValue;
    }

}
