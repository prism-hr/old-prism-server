package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.Reference;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Telephone;

public class RefereeBuilder {


	private Integer id;
	
	private ApplicationForm application;
	private List<Telephone> phoneNumbers = new ArrayList<Telephone>();
	private String firstname;
	private String lastname;
	private String relationship;
	private String jobEmployer;
	private String jobTitle;
	private String addressLocation;
	private String addressPostcode;
	private String activationCode;
	private Country addressCountry;
	private Reference reference;
	private String email;
	private String messenger;
	private RegisteredUser user;
	
	public RefereeBuilder id(Integer id){
		this.id = id;
		return this;
	}
	
	public RefereeBuilder application(ApplicationForm application){
		this.application = application;
		return this;
	}
	
	public RefereeBuilder user(RegisteredUser user){
		this.user = user;
		return this;
	}
	
	public RefereeBuilder reference(Reference reference){
		this.reference = reference;
		return this;
	}

	
	
	public RefereeBuilder activationCode(String activationCode){
		this.activationCode = activationCode;
		return this;
	}
	
	public RefereeBuilder firstname(String firstname){
		this.firstname = firstname;
		return this;
	}
	
	public RefereeBuilder lastname(String lastname){
		this.lastname = lastname;
		
		return this;
	}
	public RefereeBuilder relationship(String relationship){
		this.relationship = relationship;
		return this;
	}
	public RefereeBuilder jobEmployer(String jobEmployer){
		this.jobEmployer = jobEmployer;
		return this;
	}
	public RefereeBuilder jobTitle(String jobTitle){
		this.jobTitle = jobTitle;
		return this;
	}
	public RefereeBuilder addressLocation(String addressLocation){
		this.addressLocation = addressLocation;
		return this;
	}
	public RefereeBuilder addressPostcode(String addressPostcode){
		this.addressPostcode = addressPostcode;
		return this;
	}
	public RefereeBuilder addressCountry(Country addressCountry){
		this.addressCountry = addressCountry;
		return this;
	}
	public RefereeBuilder email(String email){
		this.email = email;
		return this;
	}

	
	public RefereeBuilder phoneNumbers(Telephone...phoneNumbers) {
		this.phoneNumbers.addAll(Arrays.asList(phoneNumbers));
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
		referee.setAddressPostcode(addressPostcode);
		referee.setApplication(application);
		referee.setEmail(email);
		referee.setFirstname(firstname);
		referee.setId(id);
		referee.setJobEmployer(jobEmployer);
		referee.setJobTitle(jobTitle);
		referee.setLastname(lastname);
		referee.setMessenger(messenger);
		referee.setPhoneNumbers(phoneNumbers);
		referee.setActivationCode(activationCode);
		referee.setRelationship(relationship);
		referee.setReference(reference);
		referee.setUser(user);
		return referee;
	}
}
