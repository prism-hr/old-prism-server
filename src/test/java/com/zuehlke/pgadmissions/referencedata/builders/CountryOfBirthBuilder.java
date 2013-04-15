package com.zuehlke.pgadmissions.referencedata.builders;

import com.zuehlke.pgadmissions.referencedata.jaxb.Countries;

public class CountryOfBirthBuilder {
    private String name;
	private String code;
	
	public CountryOfBirthBuilder name(String name){
		this.name = name;
		return this;
	}
	
	public CountryOfBirthBuilder code(String code) {
		this.code = code;
		return this;
	}
	
	public Countries.Country toCountry(){
		Countries.Country country = new Countries.Country();
		country.setName(name);
		country.setCode(code);
		return country;
	}
}
