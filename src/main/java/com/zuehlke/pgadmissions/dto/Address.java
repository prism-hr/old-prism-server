package com.zuehlke.pgadmissions.dto;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.enums.AddressStatus;

public class Address {

	private String location;
	private String postCode;
	private String country;
	private Date startDate;
	private Date endDate;
	private String purpose;
	
	private AddressStatus contactAddress;
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getPostCode() {
		return postCode;
	}
	
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getPurpose() {
		return purpose;
	}
	
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public AddressStatus getContactAddress() {
		return contactAddress;
	}
	
	public void setContactAddress(AddressStatus contactAddress) {
		this.contactAddress = contactAddress;
	}
}
