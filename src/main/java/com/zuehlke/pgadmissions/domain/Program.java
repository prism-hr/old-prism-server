package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity(name="PROGRAM")
@Access(AccessType.FIELD)
public class Program extends DomainObject<Integer> {

	private String code;
	private String title;
	private String description;

	@Override
	@Id
	@GeneratedValue
	@Access(AccessType.PROPERTY)
	public Integer getId() {	
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
		
	}

	public void setCode(String code) {
		this.code = code;		
	}

	public void setDescription(String description) {
		this.description = description;	
		
	}
	
	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
