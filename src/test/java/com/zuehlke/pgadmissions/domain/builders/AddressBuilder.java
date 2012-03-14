package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.enums.AddressStatus;

public class AddressBuilder {

	
	private ApplicationForm application;
	
	private String location;
	private String postCode;
	private String country;
	private String purpose;
	
	private Date startDate;
	private Date endDate;
	
	private AddressStatus contactAddress;
	
	
	public AddressBuilder application(ApplicationForm application) {
		this.application = application;
		return this;
	}
	
	public AddressBuilder contactAddress(AddressStatus contactAddress) {
		this.contactAddress = contactAddress;
		return this;
	}
	
	public AddressBuilder location(String location) {
		this.location = location;
		return this;
	}
	
	public AddressBuilder purpose(String purpose) {
		this.purpose = purpose;
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
		address.setLocation(location);
		address.setPostCode(postCode);
		address.setCountry(country);
		address.setStartDate(startDate);
		address.setEndDate(endDate);
		address.setPurpose(purpose);
		address.setContactAddress(contactAddress);
		return address;
	}
	
}
