package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.dto.UserDTO;

public class ApplicationPersonalDetailDTO {

    @NotNull
    private UserDTO user;

    private String messenger;

    @NotEmpty
    private String phoneNumber;

    @NotNull
    private Boolean firstLanguageEnglish;

    @Valid
    private ApplicationLanguageQualificationDTO languageQualification;

    @NotNull
    private Boolean visaRequired;

    @Valid
    private ApplicationPassportDTO passport;

    @NotNull
    private Integer firstNationality;

    private Integer secondNationality;

    private Integer title;

    @NotNull
    private Integer gender;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    private Integer country;

    @NotNull
    private Integer ethnicity;

    @NotNull
    private Integer disability;

    @NotNull
    private Integer residenceCountry;

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String getMessenger() {
        return messenger;
    }

    public void setMessenger(String messenger) {
        this.messenger = messenger;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getFirstLanguageEnglish() {
        return firstLanguageEnglish;
    }

    public void setFirstLanguageEnglish(Boolean firstLanguageEnglish) {
        this.firstLanguageEnglish = firstLanguageEnglish;
    }

    public ApplicationLanguageQualificationDTO getLanguageQualification() {
        return languageQualification;
    }

    public void setLanguageQualification(ApplicationLanguageQualificationDTO languageQualification) {
        this.languageQualification = languageQualification;
    }

    public Boolean getVisaRequired() {
        return visaRequired;
    }

    public void setVisaRequired(Boolean visaRequired) {
        this.visaRequired = visaRequired;
    }

    public ApplicationPassportDTO getPassport() {
        return passport;
    }

    public void setPassport(ApplicationPassportDTO passport) {
        this.passport = passport;
    }

    public Integer getFirstNationality() {
        return firstNationality;
    }

    public void setFirstNationality(Integer firstNationality) {
        this.firstNationality = firstNationality;
    }

    public Integer getSecondNationality() {
        return secondNationality;
    }

    public void setSecondNationality(Integer secondNationality) {
        this.secondNationality = secondNationality;
    }

    public Integer getTitle() {
        return title;
    }

    public void setTitle(Integer title) {
        this.title = title;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getCountry() {
        return country;
    }

    public void setCountry(Integer country) {
        this.country = country;
    }

    public Integer getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(Integer ethnicity) {
        this.ethnicity = ethnicity;
    }

    public Integer getDisability() {
        return disability;
    }

    public void setDisability(Integer disability) {
        this.disability = disability;
    }

    public Integer getResidenceCountry() {
        return residenceCountry;
    }

    public void setResidenceCountry(Integer residenceCountry) {
        this.residenceCountry = residenceCountry;
    }
}
