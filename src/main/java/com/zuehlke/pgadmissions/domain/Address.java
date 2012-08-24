package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name="APPLICATION_FORM_ADDRESS")
@Access(AccessType.FIELD) 
public class Address extends DomainObject<Integer>{

	private static final long serialVersionUID = 2746228908173552617L;

	@ManyToOne
	@JoinColumn(name = "country_id")
	private Country country;

	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 500)
	private String location;
	
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

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
}

