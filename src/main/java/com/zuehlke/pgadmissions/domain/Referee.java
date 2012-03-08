package com.zuehlke.pgadmissions.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;



@Entity(name="APPLICATION_FORM_REFEREE")
@Access(AccessType.FIELD)
public class Referee extends DomainObject<Integer>{


	/**
	 * 
	 */
	private static final long serialVersionUID = 4591043630090924738L;

	
	@ManyToOne
	@JoinColumn(name="application_form_id")
	private ApplicationForm application;
	
	@OneToMany(cascade={javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE})
	@org.hibernate.annotations.Cascade( {org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@Access(AccessType.PROPERTY)
	@JoinColumn(name = "telephone_id")
	private List<Telephone> telephones = new ArrayList<Telephone>();
	
	

	@OneToMany(cascade={javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE})
	@org.hibernate.annotations.Cascade( {org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@Access(AccessType.PROPERTY)
	@JoinColumn(name = "messenger_id")
	private List<Messenger> messengers = new ArrayList<Messenger>();
	
	
	@Column(name="firstname")
	private String firstname;
	
	@Column(name="lastname")
	private String lastname;
	
	@Column(name="relationship")
	private String relationship;
	
	@Column(name="job_employer")
	private String jobEmployer;
	
	@Column(name="job_title")
	private String jobTitle;
	
	@Column(name="address_location")
	private String addressLocation;
	
	@Column(name="address_postcode")
	private String addressPostcode;
	
	@Column(name="address_country")
	private String addressCountry;
	
	@Column(name="email")
	private String email;
	
	public List<Telephone> getTelephones() {
		return telephones;
	}
	
	public void setTelephones(List<Telephone> telephones) {
		if(this.telephones.size() == telephones.size() && this.telephones.containsAll(telephones)){
			return;
		}
		this.telephones.clear();
		this.telephones.addAll(telephones);
	}
	
	public List<Messenger> getMessengers() {
		return messengers;
	}
	
	public void setMessengers(List<Messenger> messengers) {
		if(this.messengers.size() == messengers.size() && this.messengers.containsAll(messengers)){
			return;
		}
		this.messengers.clear();
		this.messengers.addAll(messengers);
	}
	public ApplicationForm getApplication() {
		return application;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public String getJobEmployer() {
		return jobEmployer;
	}

	public void setJobEmployer(String jobEmployer) {
		this.jobEmployer = jobEmployer;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getAddressLocation() {
		return addressLocation;
	}

	public void setAddressLocation(String addressLocation) {
		this.addressLocation = addressLocation;
	}

	public String getAddressPostcode() {
		return addressPostcode;
	}

	public void setAddressPostcode(String addressPostcode) {
		this.addressPostcode = addressPostcode;
	}

	public String getAddressCountry() {
		return addressCountry;
	}

	public void setAddressCountry(String addressCountry) {
		this.addressCountry = addressCountry;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
	

}
