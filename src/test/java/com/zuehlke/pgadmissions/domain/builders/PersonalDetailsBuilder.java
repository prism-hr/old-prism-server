package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Nationality;
import com.zuehlke.pgadmissions.domain.PersonalDetail;
import com.zuehlke.pgadmissions.domain.Telephone;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.Gender;

public class PersonalDetailsBuilder {

	private Integer id;
	private String firstName;
	private String lastName;
	private Gender gender;
	private String email;
	private Date dateOfBirth;
	private Country country;
	private Country residenceCountry;
	private ApplicationForm applicationForm;
	private List<Telephone> phoneNumbers = new ArrayList<Telephone>();
	private String messenger;
	private List<Nationality> candiateNationalities = new ArrayList<Nationality>();
	private List<Nationality> maternalGuardianNationalities= new ArrayList<Nationality>();
	private List<Nationality> paternalGuardianNationalities= new ArrayList<Nationality>();
	private CheckedStatus englishFirstLanguage;
	private CheckedStatus requiresVisa;
	

	public PersonalDetailsBuilder id(Integer id) {
		this.id = id;
		return this;
	}
	
	public PersonalDetailsBuilder englishFirstLanguage(CheckedStatus englishFirstLanguage) {
		this.englishFirstLanguage = englishFirstLanguage;
		return this;
	}
	
	public PersonalDetailsBuilder requiresVisa(CheckedStatus requiresVisa) {
		this.requiresVisa = requiresVisa;
		return this;
	}
	
	
	public PersonalDetailsBuilder paternalGuardianNationalities(Nationality... nationalities) {
		this.maternalGuardianNationalities.addAll(Arrays.asList(nationalities));
		return this;
	}
	
	public PersonalDetailsBuilder maternalGuardianNationalities(Nationality... nationalities) {
		this.paternalGuardianNationalities.addAll(Arrays.asList(nationalities));
		return this;
	}
	
	public PersonalDetailsBuilder candiateNationalities(Nationality... nationalities) {
		this.candiateNationalities.addAll(Arrays.asList(nationalities));
		return this;
	}

	public PersonalDetailsBuilder phoneNumbers(Telephone... phoneNumbers) {
		this.phoneNumbers.addAll(Arrays.asList(phoneNumbers));
		return this;
	}
	
	public PersonalDetailsBuilder messengers(String messenger) {
		this.messenger = messenger;
		return this;
	}
	public PersonalDetailsBuilder firstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public PersonalDetailsBuilder lastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public PersonalDetailsBuilder gender(Gender gender) {
		this.gender = gender;
		return this;
	}

	public PersonalDetailsBuilder email(String email) {
		this.email = email;
		return this;
	}

	public PersonalDetailsBuilder dateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
		return this;
	}

	public PersonalDetailsBuilder country(Country country) {
		this.country = country;
		return this;
	}

	public PersonalDetailsBuilder residenceCountry(Country residenceCountry) {
		this.residenceCountry = residenceCountry;
		return this;
	}


	public PersonalDetailsBuilder applicationForm(ApplicationForm applicationForm) {
		this.applicationForm = applicationForm;
		return this;
	}

	public PersonalDetail toPersonalDetails() {
		PersonalDetail personalDetails = new PersonalDetail();
		personalDetails.setId(id);
		personalDetails.setApplication(applicationForm);
		personalDetails.setCountry(country);
		personalDetails.setDateOfBirth(dateOfBirth);
		personalDetails.setEmail(email);
		personalDetails.setFirstName(firstName);
		personalDetails.setGender(gender);
		personalDetails.setLastName(lastName);
		personalDetails.setResidenceCountry(residenceCountry);
		personalDetails.setPhoneNumbers(phoneNumbers);
		personalDetails.setMaternalGuardianNationalities(maternalGuardianNationalities);
		personalDetails.setPaternalGuardianNationalities(paternalGuardianNationalities);
		personalDetails.setCandidateNationalities(candiateNationalities);
		personalDetails.setMessenger(messenger);
		personalDetails.setEnglishFirstLanguage(englishFirstLanguage);
		personalDetails.setRequiresVisa(requiresVisa);
		return personalDetails;
	}
}
