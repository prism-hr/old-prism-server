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
import javax.persistence.OneToOne;



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
	
	@OneToMany(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "referee_id")
	private List<Telephone> phoneNumbers ;
	
	
	@OneToMany(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@Access(AccessType.PROPERTY)
	@JoinColumn(name = "referee_id")
	private List<Messenger> messengers = new ArrayList<Messenger>();
	
	@Column(name="comment")
	private String comment;
	
	@Column(name="firstname")
	private String firstname;

	@Column(name="activationCode")
	private String activationCode;
	
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
	
	@OneToOne
	@JoinColumn(name = "country_id")
	private Country addressCountry;
	
	@OneToOne(cascade = javax.persistence.CascadeType.ALL, mappedBy="referee")
	private Document document;
	
	@Column(name="email")
	private String email;
	
	public List<Telephone> getPhoneNumbers() {
		return phoneNumbers;
	}
	
	public void setPhoneNumbers(List<Telephone> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
		if(phoneNumbers != null && !phoneNumbers.isEmpty()){
			int size = phoneNumbers.size();
			for (int i = size -1; i >= 0 ;i--){
				Telephone telephone = phoneNumbers.get(i);
				if(telephone == null){
					phoneNumbers.remove(i);
				}
			}
		}
	}
	
	public List<Messenger> getMessengers() {
		return messengers;
	}
	
	public void setMessengers(List<Messenger> messengers) {
		this.messengers = messengers;
		if(messengers != null && !messengers.isEmpty()){
			int size = messengers.size();
			for (int i = size -1; i >= 0 ;i--){
				Messenger messenger = messengers.get(i);
				if(messenger == null){
					messengers.remove(i);
				}
			}
		}
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

	public Country getAddressCountry() {
		return addressCountry;
	}

	public void setAddressCountry(Country addressCountry) {
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

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public boolean hasProvidedReference() {
		return comment != null || document != null;
	}

}
