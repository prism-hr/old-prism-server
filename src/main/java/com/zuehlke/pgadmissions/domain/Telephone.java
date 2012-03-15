package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.PhoneType;

@Entity(name = "TELEPHONE")
@Access(AccessType.FIELD)
public class Telephone extends DomainObject<Integer>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3758468813312267227L;


	@Column(name = "telephone_type")
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.PhoneTypeEnumUserType")
	private PhoneType telephoneType;
	
	@Column(name = "number")
	private String telephoneNumber;
	
	public PhoneType getTelephoneType() {
		return telephoneType;
	}

	public void setTelephoneType(PhoneType telephoneType) {
		this.telephoneType = telephoneType;
	}

	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	@Id
	@GeneratedValue
	@Access(AccessType.PROPERTY)
	public Integer getId() {
		return id;
	}

	public String getAsJson() {	
		return "{\"type\": \"" + this.getTelephoneType() + "\", \"number\": \"" + this.getTelephoneNumber() + "\"}";
	}
}
