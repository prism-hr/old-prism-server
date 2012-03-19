package com.zuehlke.pgadmissions.dto;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.enums.AddressPurpose;

public class Address {

	private String addressLocation;
	private String addressPostCode;
	private Integer addressCountry;
	private Date addressStartDate;
	private Date addressEndDate;
	private AddressPurpose addressPurpose;
	private Integer addressId;
	
	private String addressContactAddress;

	public String getAddressLocation() {
		return addressLocation;
	}
	
	public void setAddressLocation(String addressLocation) {
		this.addressLocation = addressLocation;
	}
	
	public String getAddressPostCode() {
		return addressPostCode;
	}
	
	public void setAddressPostCode(String addressPostCode) {
		this.addressPostCode = addressPostCode;
	}
	
	public Integer getAddressCountry() {
		return addressCountry;
	}
	
	public void setAddressCountry(Integer addressCountry) {
		this.addressCountry = addressCountry;
	}
	
	public Date getAddressStartDate() {
		return addressStartDate;
	}
	
	public void setAddressStartDate(Date addressStartDate) {
		this.addressStartDate = addressStartDate;
	}
	
	public Date getAddressEndDate() {
		return addressEndDate;
	}
	
	public void setAddressEndDate(Date addressEndDate) {
		this.addressEndDate = addressEndDate;
	}
	
	public AddressPurpose getAddressPurpose() {
		return addressPurpose;
	}
	
	public void setAddressPurpose(AddressPurpose addressPurpose) {
		this.addressPurpose = addressPurpose;
	}
	
	public Integer getAddressId() {
		return addressId;
	}
	
	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}
	
	public String getAddressContactAddress() {
		return addressContactAddress;
	}
	
	public void setAddressContactAddress(String addressContactAddress) {
		this.addressContactAddress = addressContactAddress;
	}
}
