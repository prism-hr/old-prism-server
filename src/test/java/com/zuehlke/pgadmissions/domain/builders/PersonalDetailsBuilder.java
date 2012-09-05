package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.Title;

public class PersonalDetailsBuilder {

	private Integer id;
	private Title title;
	private String firstName;
	private String lastName;
	private Gender gender;
	private String email;
	private Date dateOfBirth;
	private Country country;
	private Domicile residenceCountry;
	private Ethnicity ethnicity;
	private Disability disability;
	private ApplicationForm applicationForm;

	private String messenger;
	private List<Language> candiateNationalities = new ArrayList<Language>();
	private Boolean englishFirstLanguage;
	private Boolean requiresVisa;
	private String phoneNumber;

	public PersonalDetailsBuilder title(Title title) {
	    this.title = title;
	    return this;
	}
	
	public PersonalDetailsBuilder phoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		return this;
	}

	public PersonalDetailsBuilder id(Integer id) {
		this.id = id;
		return this;
	}

	public PersonalDetailsBuilder englishFirstLanguage(Boolean englishFirstLanguage) {
		this.englishFirstLanguage = englishFirstLanguage;
		return this;
	}

	public PersonalDetailsBuilder requiresVisa(Boolean requiresVisa) {
		this.requiresVisa = requiresVisa;
		return this;
	}

	public PersonalDetailsBuilder candiateNationalities(Language... nationalities) {
		this.candiateNationalities.addAll(Arrays.asList(nationalities));
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

	public PersonalDetailsBuilder ethnicity(Ethnicity eth) {
		ethnicity = eth;
		return this;
	}
	
	public PersonalDetailsBuilder disability(Disability dis) {
		this.disability = dis;
		return this;
	}

	public PersonalDetailsBuilder residenceDomicile(Domicile residenceCountry) {
		this.residenceCountry = residenceCountry;
		return this;
	}

	public PersonalDetailsBuilder applicationForm(ApplicationForm applicationForm) {
		this.applicationForm = applicationForm;
		return this;
	}

	public PersonalDetails toPersonalDetails() {
		PersonalDetails personalDetails = new PersonalDetails();
		personalDetails.setId(id);
		personalDetails.setTitle(title);
		personalDetails.setApplication(applicationForm);
		personalDetails.setCountry(country);
		personalDetails.setDateOfBirth(dateOfBirth);
		personalDetails.setEmail(email);
		personalDetails.setFirstName(firstName);
		personalDetails.setGender(gender);
		personalDetails.setLastName(lastName);
		personalDetails.setResidenceCountry(residenceCountry);
		personalDetails.setEthnicity(ethnicity);
		personalDetails.setDisability(disability);
		personalDetails.setCandidateNationalities(candiateNationalities);
		personalDetails.setMessenger(messenger);
		personalDetails.setEnglishFirstLanguage(englishFirstLanguage);
		personalDetails.setRequiresVisa(requiresVisa);
		personalDetails.setPhoneNumber(phoneNumber);
		return personalDetails;
	}
}
