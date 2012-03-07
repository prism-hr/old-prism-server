package com.zuehlke.pgadmissions.domain.enums;




public enum AddressPurpose {
	
	SCHOLARSHIP("Scholarship/grant"), EMPLOYER("Employer"), INDUSTRIAL_SPONSOR("Industrial Sponsor");
	
	private final String displayValue;
	private String val;
	private String valName;
	
	private AddressPurpose(String displayValue) {
		this.displayValue = displayValue;
		this.val = displayValue;
		valName = this.name();
	}

	public String displayValue() {
		return displayValue;
	}
	
	public String getVal() {
		return val;
	}
	
	public String getValName() {
		return valName;
	}
	
	public void setVal(String val) {
		this.val = val;
	}
	
	
	
}
