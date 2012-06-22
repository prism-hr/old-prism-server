package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class RefereeBuilder {

	private Integer id;

	private ApplicationForm application;
	private String firstname;
	private String lastname;

	private String jobEmployer;
	private String jobTitle;
	private String addressLocation;

	private String activationCode;
	private Country addressCountry;
	private ReferenceComment reference;
	private String email;
	private String messenger;
	private RegisteredUser user;

	private String phoneNumber;
	private boolean declined;
	
	private Date lastNotified;
	
	
	public RefereeBuilder lastNotified(Date lastNotified){
		this.lastNotified = lastNotified;
		return this;
	}
	
	public RefereeBuilder declined(boolean declined){
		this.declined = declined;
		return this;
	}
	public RefereeBuilder phoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		return this;
	}

	public RefereeBuilder id(Integer id) {
		this.id = id;
		return this;
	}

	public RefereeBuilder application(ApplicationForm application) {
		this.application = application;
		return this;
	}

	public RefereeBuilder user(RegisteredUser user) {
		this.user = user;
		return this;
	}

	public RefereeBuilder reference(ReferenceComment reference) {
		this.reference = reference;
		return this;
	}

	public RefereeBuilder activationCode(String activationCode) {
		this.activationCode = activationCode;
		return this;
	}

	public RefereeBuilder firstname(String firstname) {
		this.firstname = firstname;
		return this;
	}

	public RefereeBuilder lastname(String lastname) {
		this.lastname = lastname;

		return this;
	}

	public RefereeBuilder jobEmployer(String jobEmployer) {
		this.jobEmployer = jobEmployer;
		return this;
	}

	public RefereeBuilder jobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
		return this;
	}

	public RefereeBuilder addressLocation(String addressLocation) {
		this.addressLocation = addressLocation;
		return this;
	}

	public RefereeBuilder addressCountry(Country addressCountry) {
		this.addressCountry = addressCountry;
		return this;
	}

	public RefereeBuilder email(String email) {
		this.email = email;
		return this;
	}

	public RefereeBuilder messenger(String messenger) {
		this.messenger = messenger;
		return this;
	}

	public Referee toReferee() {
		Referee referee = new Referee();
		referee.setAddressCountry(addressCountry);
		referee.setAddressLocation(addressLocation);
		referee.setApplication(application);
		referee.setEmail(email);
		referee.setFirstname(firstname);
		referee.setId(id);
		referee.setJobEmployer(jobEmployer);
		referee.setJobTitle(jobTitle);
		referee.setLastname(lastname);
		referee.setMessenger(messenger);
		referee.setActivationCode(activationCode);
		referee.setReference(reference);
		referee.setUser(user);
		referee.setPhoneNumber(phoneNumber);
		referee.setDeclined(declined);
		referee.setLastNotified(lastNotified);
		return referee;
	}
}
