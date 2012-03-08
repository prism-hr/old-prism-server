package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Telephone;

public class TelephoneBuilder {


	private Integer id;
	private String telephoneType;
	
	private String telephoneNumber;
	
	public TelephoneBuilder id(Integer id) {
		this.id = id;
		return this;
	}
	
	public TelephoneBuilder telephoneType(String telephoneType) {
		this.telephoneType = telephoneType;
		return this;
	}
	
	public TelephoneBuilder telephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
		return this;
	}
	
	public Telephone toTelephone(){
		Telephone telephone = new Telephone();
		telephone.setId(id);
		telephone.setTelephoneType(telephoneType);
		telephone.setTelephoneNumber(telephoneNumber);
		return telephone;
	}
}
