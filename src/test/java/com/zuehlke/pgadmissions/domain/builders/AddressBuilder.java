package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;

public class AddressBuilder {

	
	private ApplicationForm application;
	
	private String street;
	private String city;
	private String postCode;
	private String country;
	
	private Date startDate;
	private Date endDate;
	
	public AddressBuilder application(ApplicationForm application) {
		this.application = application;
		return this;
	}
	
	public AddressBuilder street(String street) {
		this.street = street;
		return this;
	}
	
	public AddressBuilder city(String city) {
		this.city = city;
		return this;
	}
	
	public AddressBuilder postCode(String postCode) {
		this.postCode = postCode;
		return this;
	}
	
	public AddressBuilder country(String country) {
		this.country = country;
		return this;
	}
	
	public AddressBuilder startDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	
	public AddressBuilder endDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
	
	public Address toAddress() {
		Address address = new Address();
		address.setApplication(application);
		address.setStreet(street);
		address.setCity(city);
		address.setPostCode(postCode);
		address.setCountry(country);
		address.setStartDate(startDate);
		address.setEndDate(endDate);
		
		return address;
	}
	
}
