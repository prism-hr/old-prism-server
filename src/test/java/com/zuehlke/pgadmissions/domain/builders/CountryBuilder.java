package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Country;

public class CountryBuilder {
	
    private Integer id;
	
    private String name;
	
    private Boolean enabled;

	private String code;
	
    public CountryBuilder code(String code){
        this.code = code;
        return this;
    }
    
	public CountryBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public CountryBuilder name(String name){
		this.name = name;
		return this;
	}
		
	public CountryBuilder enabled(Boolean enabled){
	    this.enabled = enabled;
	    return this;
	}
	
	public Country build(){
		Country country = new Country();
		country.setId(id);
		country.setName(name);
		country.setEnabled(enabled);
		country.setCode(code);
		return country;
	}
}
