package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name="COUNTRIES")
@Access(AccessType.FIELD) 
public class Country extends DomainObject<Integer>{

	private static final long serialVersionUID = 2746228908173552617L;

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
	
	private String code;
	
	@ESAPIConstraint(rule = "ExtendedAscii", allowNull = true, maxLength = 100)
	private String name;
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}

