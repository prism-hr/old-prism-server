package com.zuehlke.pgadmissions.dto;


public class Address {

	private String currentAddressLocation;
	private Integer currentAddressCountry;
	private String contactAddressLocation;
	private Integer contactAddressCountry;
	private Integer currentAddressId;
	private Integer contactAddressId;
	
	public String getCurrentAddressLocation() {
		return currentAddressLocation;
	}
	
	public void setCurrentAddressLocation(String currentAddressLocation) {
		this.currentAddressLocation = currentAddressLocation;
	}
	
	public Integer getCurrentAddressCountry() {
		return currentAddressCountry;
	}
	
	public void setCurrentAddressCountry(Integer currentAddressCountry) {
		this.currentAddressCountry = currentAddressCountry;
	}
	
	public Integer getCurrentAddressId() {
		return currentAddressId;
	}
	
	public void setCurrentAddressId(Integer currentAddressId) {
		this.currentAddressId = currentAddressId;
	}
	
	public String getContactAddressLocation() {
		return contactAddressLocation;
	}
	
	public void setContactAddressLocation(String contactAddressLocation) {
		this.contactAddressLocation = contactAddressLocation;
	}
	
	public Integer getContactAddressCountry() {
		return contactAddressCountry;
	}
	
	public void setContactAddressCountry(Integer contactAddressCountry) {
		this.contactAddressCountry = contactAddressCountry;
	}
	
	public Integer getContactAddressId() {
		return contactAddressId;
	}
	
	public void setContactAddressId(Integer contactAddressId) {
		this.contactAddressId = contactAddressId;
	}
}
