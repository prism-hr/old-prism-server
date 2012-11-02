package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "APPLICATION_FORM_REFEREE")
@Access(AccessType.FIELD)
public class Referee extends DomainObject<Integer> implements FormSectionObject{

	private static final long serialVersionUID = 4591043630090924738L;

	@Transient
	private boolean acceptedTerms;
	
	@Column(name = "last_notified")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastNotified;

	@ManyToOne
	@JoinColumn(name = "registered_user_id")
	private RegisteredUser user;

	@OneToOne(mappedBy = "referee")
	private ReferenceComment reference;

	@ManyToOne
	@JoinColumn(name = "application_form_id")
	private ApplicationForm application;

	@ESAPIConstraint(rule = "PhoneNumber", maxLength = 35, message = "{text.field.notphonenumber}")
	@Column(name = "phone")
	private String phoneNumber;

	@Column(name = "skype")
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
	private String messenger;

	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 30)
	@Column(name = "firstname")
	private String firstname;

	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 40)
	@Column(name = "lastname")
	private String lastname;

	@Column(name = "job_employer")
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 200)
	private String jobEmployer;

	@Column(name = "job_title")
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 200)
	private String jobTitle;

	@Column(name = "address_location")
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 200)
	private String addressLocation;

	@OneToOne
	@JoinColumn(name = "country_id")
	private Country addressCountry;

	@Column(name = "email")
	@ESAPIConstraint(rule = "Email", maxLength = 255, message = "{text.email.notvalid}")
	private String email;

	private boolean declined = false;

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


	public boolean hasProvidedReference() {
		return reference != null;
	}

	public ReferenceComment getReference() {
		return reference;
	}

	public void setReference(ReferenceComment reference) {
		this.reference = reference;
	}

	public String getMessenger() {
		return messenger;
	}

	public void setMessenger(String messenger) {
		if (StringUtils.isBlank(messenger)) {
			this.messenger = null;
		} else {
			this.messenger = messenger;
		}
	}

	public RegisteredUser getUser() {
		return user;
	}

	public void setUser(RegisteredUser user) {
		this.user = user;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean isDeclined() {
		return declined;
	}

	public void setDeclined(boolean declined) {
		this.declined = declined;
	}

	public Date getLastNotified() {
		return lastNotified;
	}

	public void setLastNotified(Date lastNotified) {
		this.lastNotified = lastNotified;
	}

	public boolean isEditable() {
		return !hasProvidedReference() && !declined && (application == null || application.isModifiable());
	}

	public boolean hasResponded() {
		return isDeclined() || hasProvidedReference();
		
	}

	public boolean isAcceptedTerms() {
		return acceptedTerms;
	}

	public void setAcceptedTerms(boolean acceptedTerms) {
		this.acceptedTerms = acceptedTerms;
	}

}
