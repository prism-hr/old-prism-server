package com.zuehlke.pgadmissions.dto;

import java.util.ArrayList;
import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;

public class Referee {

	private Integer refereeId;

	private ApplicationForm application;

	private List<Telephone> telephones = new ArrayList<Telephone>();

	private List<Messenger> messengers = new ArrayList<Messenger>();

	private String firstname;

	private String lastname;

	private String relationship;

	private String jobEmployer;

	private String jobTitle;


	private String addressPostcode;

	private String addressCountry;

	private String email;

//	private String telephoneType;
//
//	private String telephoneNumber;
//
//	private String messengerType;
//
//	private String messengerAddress;

//	public String getTelephoneType() {
//		return telephoneType;
//	}
//
//	public void setTelephoneType(String telephoneType) {
//		this.telephoneType = telephoneType;
//	}
//
//	public String getTelephoneNumber() {
//		return telephoneNumber;
//	}
//
//	public void setTelephoneNumber(String telephoneNumber) {
//		this.telephoneNumber = telephoneNumber;
//	}
//
//	public String getMessengerType() {
//		return messengerType;
//	}
//
//	public void setMessengerType(String messengerType) {
//		this.messengerType = messengerType;
//	}
//
//	public String getMessengerAddress() {
//		return messengerAddress;
//	}
//
//	public void setMessengerAddress(String messengerAddress) {
//		this.messengerAddress = messengerAddress;
//	}

	private String addressLocation;

	public ApplicationForm getApplication() {
		return application;
	}

	public void setApplication(ApplicationForm application) {
		this.application = application;
	}

	public List<Telephone> getTelephones() {
		return telephones;
	}

	public void setTelephones(List<Telephone> telephones) {
		this.telephones = telephones;
	}

	public List<Messenger> getMessengers() {
		return messengers;
	}

	public void setMessengers(List<Messenger> messengers) {
		this.messengers = messengers;
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

	public Integer getRefereeId() {
		return refereeId;
	}

	public void setRefereeId(Integer refereeId) {
		this.refereeId = refereeId;
	}

}
