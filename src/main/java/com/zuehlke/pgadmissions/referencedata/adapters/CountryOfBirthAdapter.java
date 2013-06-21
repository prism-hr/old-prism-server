package com.zuehlke.pgadmissions.referencedata.adapters;

import java.util.List;

import com.zuehlke.pgadmissions.domain.CodeObject;
import com.zuehlke.pgadmissions.referencedata.v2.jaxb.Countries.Country;

public class CountryOfBirthAdapter implements ImportData {

	private Country country;
	
	public String getName() {
		return country.getName();
	}

	public CountryOfBirthAdapter(Country country) {
		this.country = country;
	}

	@Override
	public String getStringCode() {
		return country.getCode();
	}

	@Override
	public com.zuehlke.pgadmissions.domain.Country createDomainObject(List<? extends CodeObject> currentData, List<? extends CodeObject> changes) {
		com.zuehlke.pgadmissions.domain.Country result = new com.zuehlke.pgadmissions.domain.Country();
		result.setCode(country.getCode());
		result.setName(country.getName());
		result.setEnabled(true);
		return result;
	}
}
