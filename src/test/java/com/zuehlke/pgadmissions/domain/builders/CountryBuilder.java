package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Country;

public class CountryBuilder {
	private Integer id;
	private String code;
	private String name;
	
	
	public CountryBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public CountryBuilder code(String code){
		this.code = code;
		return this;
	}
	
	public CountryBuilder name(String name){
		this.name = name;
		return this;
	}
	
	public Country toCountry(){
		Country country = new Country();
		country.setId(id);
		country.setCode(code);
		country.setName(name);
		return country;
	}
}
