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
import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.domain.PassportInformation;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.Title;

public class PersonalDetailsBuilder {

    private Integer id;
    private Title title;
    private Gender gender;
    private Date dateOfBirth;
    private Country country;
    private Domicile residenceCountry;
    private Ethnicity ethnicity;
    private Disability disability;
    private ApplicationForm applicationForm;

    private String messenger;
    private Language firstNationality;
    private Language secondNationality;
    private String phoneNumber;

    private Boolean englishFirstLanguage;
    private Boolean languageQualificationAvailable;
    private LanguageQualification languageQualification;

    private Boolean passportAvailable;
    private Boolean requiresVisa;
    private PassportInformation passportInformation;

    public PersonalDetailsBuilder passportAvailable(Boolean passportAvailable) {
        this.passportAvailable = passportAvailable;
        return this;
    }

    public PersonalDetailsBuilder languageQualification(LanguageQualification languageQualification) {
        this.languageQualification = languageQualification;
        return this;
    }

    public PersonalDetailsBuilder languageQualificationAvailable(Boolean flag) {
        this.languageQualificationAvailable = flag;
        return this;
    }

    public PersonalDetailsBuilder passportInformation(PassportInformation passportInformation) {
        this.passportInformation = passportInformation;
        return this;
    }

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

    public PersonalDetailsBuilder firstNationality(Language firstNationality) {
        this.firstNationality = firstNationality;
        return this;
    }

    public PersonalDetailsBuilder secondNationality(Language secondNationality) {
        this.secondNationality = secondNationality;
        return this;
    }

    public PersonalDetailsBuilder messengers(String messenger) {
        this.messenger = messenger;
        return this;
    }

    public PersonalDetailsBuilder gender(Gender gender) {
        this.gender = gender;
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

    public PersonalDetails build() {
        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setId(id);
        personalDetails.setTitle(title);
        personalDetails.setApplication(applicationForm);
        personalDetails.setCountry(country);
        personalDetails.setDateOfBirth(dateOfBirth);
        personalDetails.setGender(gender);
        personalDetails.setResidenceCountry(residenceCountry);
        personalDetails.setEthnicity(ethnicity);
        personalDetails.setDisability(disability);
        personalDetails.setFirstNationality(firstNationality);
        personalDetails.setSecondNationality(secondNationality);
        personalDetails.setMessenger(messenger);
        personalDetails.setEnglishFirstLanguage(englishFirstLanguage);
        personalDetails.setRequiresVisa(requiresVisa);
        personalDetails.setPhoneNumber(phoneNumber);
        personalDetails.setLanguageQualification(languageQualification);
        personalDetails.setPassportInformation(passportInformation);
        personalDetails.setLanguageQualificationAvailable(languageQualificationAvailable);
        personalDetails.setPassportAvailable(passportAvailable);
        return personalDetails;
    }
}
