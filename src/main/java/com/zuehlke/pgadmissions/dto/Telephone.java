package com.zuehlke.pgadmissions.dto;


public class Telephone {

	
	private Integer telephoneId;

	private String telephoneType;
	
	private String telephoneNumber;

	public Integer getTelephoneId() {
		return telephoneId;
	}

	public void setTelephoneId(Integer telephoneId) {
		this.telephoneId = telephoneId;
	}

	public String getTelephoneType() {
		return telephoneType;
	}

	public void setTelephoneType(String telephoneType) {
		this.telephoneType = telephoneType;
	}

	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}
}
