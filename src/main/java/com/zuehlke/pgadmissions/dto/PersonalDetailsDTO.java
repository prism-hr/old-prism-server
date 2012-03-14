package com.zuehlke.pgadmissions.dto;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;

@Component
public class PersonalDetailsDTO {

	private String firstName;
	private String lastName;
	private String email;
	private String gender;
	private Date dateOfBirth;
	private Country country;
	private Country residenceCountry;
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
	
	public Country getCountry() {
		return country;
	}
	
	public void setCountry(Country country) {
		this.country = country;
	}
	
	public Country getResidenceCountry() {
		return residenceCountry;
	}
	
	public void setResidenceCountry(Country residenceCountry) {
		this.residenceCountry = residenceCountry;
	}
	
}
