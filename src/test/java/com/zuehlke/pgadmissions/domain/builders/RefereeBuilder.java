package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Messenger;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.Telephone;

public class RefereeBuilder {


	private Integer refereeId;
	
	private ApplicationForm application;
	
	private List<Telephone> telephones = new ArrayList<Telephone>();
	
	private List<Messenger> messengers = new ArrayList<Messenger>();
	
	private String firstname;
	
	private String lastname;
	
	private String relationship;
	
	private String jobEmployer;
	
	private String jobTitle;
	
	private String addressLocation;
	
	private String addressPostcode;
	
	private String addressCountry;
	
	private String email;
	
	public RefereeBuilder refereeId(Integer id){
		this.refereeId = id;
		return this;
	}
	public RefereeBuilder application(ApplicationForm application){
		this.application = application;
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
	public RefereeBuilder addressCountry(String addressCountry){
		this.addressCountry = addressCountry;
		return this;
	}
	public RefereeBuilder email(String email){
		this.email = email;
		return this;
	}

	public RefereeBuilder telephone(Telephone telephone) {
		this.telephones.add(telephone);
		return this;
	}
	
	public RefereeBuilder telephones(Telephone... telephones) {
		for (Telephone telephone : telephones) {
			this.telephones.add(telephone);
		}
		return this;
	}
	public RefereeBuilder messenger(Messenger messenger) {
		this.messengers.add(messenger);
		return this;
	}
	
	public RefereeBuilder messengers(Messenger... messengers) {
		for (Messenger messenger : messengers) {
			this.messengers.add(messenger);
		}
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
		referee.setId(refereeId);
		referee.setJobEmployer(jobEmployer);
		referee.setJobTitle(jobTitle);
		referee.setLastname(lastname);
		referee.getMessengers().addAll(messengers);
		referee.getTelephones().addAll(telephones);
		referee.setRelationship(relationship);
		return referee;
	}
}
