package com.zuehlke.pgadmissions.dto;


public class Address {

	private String addressLocation;
	private Integer addressCountry;
	private Integer addressId;
	
	private String addressContactAddress;

	public String getAddressLocation() {
		return addressLocation;
	}
	
	public void setAddressLocation(String addressLocation) {
		this.addressLocation = addressLocation;
	}
	
	public Integer getAddressCountry() {
		return addressCountry;
	}
	
	public void setAddressCountry(Integer addressCountry) {
		this.addressCountry = addressCountry;
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
