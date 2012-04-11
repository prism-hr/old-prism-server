package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;

public class AddressBuilder {

	
	private Integer id;
	private ApplicationForm application;
	
	private String location;
	private Country country;
	
	public AddressBuilder id(Integer id) {
		this.id = id;
		return this;
	}
	
	public AddressBuilder application(ApplicationForm application) {
		this.application = application;
		return this;
	}
	
	public AddressBuilder location(String location) {
		this.location = location;
		return this;
	}
	
	public AddressBuilder country(Country country) {
		this.country = country;
		return this;
	}
	
	public Address toAddress() {
		Address address = new Address();
		address.setId(id);
		address.setApplication(application);
		address.setLocation(location);
		address.setCountry(country);
		return address;
	}
	
}
