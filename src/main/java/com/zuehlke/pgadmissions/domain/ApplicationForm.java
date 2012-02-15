package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.CascadeType;

@Entity(name = "APPLICATION_FORM")
@Access(AccessType.FIELD) 
public class ApplicationForm extends DomainObject<Integer> {
	
	private static final long serialVersionUID = 1L;

	//toDo: change to Enum
	private String title;
	//toDo: change to Enum
	private String gender;
	
	private String dob;
	
	private String country_ob;
	
	private String nationality;
	
	private String descriptionOfResearch;
	
	@ManyToOne
	@JoinColumn(name="registered_user_id")
	private RegisteredUser user = null;

	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getCob() {
		return country_ob;
	}

	public void setCob(String cob) {
		this.country_ob = cob;
	}

	public String getNat() {
		return nationality;
	}

	public void setNat(String nat) {
		this.nationality = nat;
	}

	public String getDescriptionOfResearch() {
		return descriptionOfResearch;
	}

	public void setDescriptionOfResearch(String res) {
		this.descriptionOfResearch = res;
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

	public RegisteredUser getUser() {
		return user;
	}

	public void setUser(RegisteredUser user) {
		this.user = user;
	}
}
