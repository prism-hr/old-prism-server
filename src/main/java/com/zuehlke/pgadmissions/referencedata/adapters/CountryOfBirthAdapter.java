package com.zuehlke.pgadmissions.referencedata.adapters;

import com.zuehlke.pgadmissions.referencedata.jaxb.Countries.Country;

public class CountryOfBirthAdapter implements ImportData {

	private Country country;
	
	public String getName() {
		return country.getName();
	}

	public CountryOfBirthAdapter(Country country) {
		this.country = country;
	}

	@Override
	public String getCode() {
		return country.getCode();
	}

	@Override
	public com.zuehlke.pgadmissions.domain.Country createDomainObject() {
		com.zuehlke.pgadmissions.domain.Country result = new com.zuehlke.pgadmissions.domain.Country();
		result.setCode(country.getCode());
		result.setName(country.getName());
		result.setEnabled(true);
		return result;
	}
}
