package com.zuehlke.pgadmissions.dto;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;

@Component
public class PersonalDetails {

	private String firstName;
	private String lastName;
	private String email;
	private String gender;
	private Date dateOfBirth;
	private String country;
	private String residenceCountry;
	private ResidenceStatus residenceStatus;
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public ResidenceStatus getResidenceStatus() {
		return residenceStatus;
	}
	
	public void setResidenceStatus(ResidenceStatus residenceStatus) {
		this.residenceStatus = residenceStatus;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getResidenceCountry() {
		return residenceCountry;
	}
	
	public void setResidenceCountry(String residenceCountry) {
		this.residenceCountry = residenceCountry;
	}
	
}
