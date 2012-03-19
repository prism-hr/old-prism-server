package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.enums.AddressPurpose;
import com.zuehlke.pgadmissions.domain.enums.AddressStatus;

public class AddressBuilder {

	
	private Integer id;
	private ApplicationForm application;
	
	private String location;
	private String postCode;
	private Country country;
	private AddressPurpose purpose;
	
	private Date startDate;
	private Date endDate;
	
	
	private AddressStatus contactAddress;
	
	public AddressBuilder id(Integer id) {
		this.id = id;
		return this;
	}
	
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
	
	public AddressBuilder purpose(AddressPurpose purpose) {
		this.purpose = purpose;
		return this;
	}
	
	public AddressBuilder postCode(String postCode) {
		this.postCode = postCode;
		return this;
	}
	
	public AddressBuilder country(Country country) {
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
		address.setId(id);
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
