package com.zuehlke.pgadmissions.dto;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Countries;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;

@Component
public class PersonalDetails {

	private String firstName;
	private String lastName;
	private String email;
	private Gender gender;
	private Date dateOfBirth;
	private Countries country;
	private Countries residenceCountry;
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
	
	public Gender getGender() {
		return gender;
	}
	
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	public Countries getCountry() {
		return country;
	}
	
	public void setCountry(Countries country) {
		this.country = country;
	}
	
	public Countries getResidenceCountry() {
		return residenceCountry;
	}
	
	public void setResidenceCountry(Countries residenceCountry) {
		this.residenceCountry = residenceCountry;
	}
	
	public ResidenceStatus getResidenceStatus() {
		return residenceStatus;
	}
	
	public void setResidenceStatus(ResidenceStatus residenceStatus) {
		this.residenceStatus = residenceStatus;
	}
	
}
