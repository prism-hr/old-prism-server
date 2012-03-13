package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.LanguageProficiency;
import com.zuehlke.pgadmissions.domain.Nationality;
import com.zuehlke.pgadmissions.domain.PersonalDetail;
import com.zuehlke.pgadmissions.domain.Telephone;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.ResidenceStatus;

public class PersonalDetailsBuilder {

	private Integer id;
	private String firstName;
	private String lastName;
	private Gender gender;
	private String email;
	private Date dateOfBirth;
	private Country country;
	private Country residenceCountry;
	private ResidenceStatus residenceStatus;
	private ApplicationForm applicationForm;
	private List<Telephone> phoneNumbers = new ArrayList<Telephone>();;
	private List<Nationality> candiateNationalities = new ArrayList<Nationality>();

	private List<LanguageProficiency> languageProficiencies = new ArrayList<LanguageProficiency>();

	public PersonalDetailsBuilder id(Integer id) {
		this.id = id;
		return this;
	}

	public PersonalDetailsBuilder languageProficiencies(LanguageProficiency... languageProficiencies) {
		this.languageProficiencies.addAll(Arrays.asList(languageProficiencies));
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

	public PersonalDetailsBuilder residenceStatus(ResidenceStatus residenceStatus) {
		this.residenceStatus = residenceStatus;
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
		personalDetails.setResidenceStatus(residenceStatus);
		personalDetails.setPhoneNumbers(phoneNumbers);

		personalDetails.setCandidateNationalities(candiateNationalities);
		personalDetails.setLanguageProficiencies(languageProficiencies);
		return personalDetails;
	}
}
