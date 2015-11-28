package uk.co.alumeni.prism.rest.dto.profile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import uk.co.alumeni.prism.domain.definitions.PrismDisability;
import uk.co.alumeni.prism.domain.definitions.PrismDomicile;
import uk.co.alumeni.prism.domain.definitions.PrismEthnicity;
import uk.co.alumeni.prism.domain.definitions.PrismGender;
import uk.co.alumeni.prism.rest.dto.user.UserSimpleDTO;
import uk.co.alumeni.prism.utils.validation.DatePast;
import uk.co.alumeni.prism.utils.validation.PhoneNumber;

public class ProfilePersonalDetailDTO {

    private PrismGender gender;

    @NotNull
    @Valid
    private UserSimpleDTO user;

    @DatePast
    private LocalDate dateOfBirth;

    @NotNull
    private PrismDomicile domicile;

    @NotNull
    private PrismDomicile nationality;

    @NotNull
    private Boolean visaRequired;

    @NotEmpty
    @PhoneNumber
    private String phone;

    @Size(min = 6, max = 32)
    private String skype;
    
    private PrismEthnicity ethnicity;
    
    private PrismDisability disability;

    public PrismGender getGender() {
        return gender;
    }

    public void setGender(PrismGender gender) {
        this.gender = gender;
    }

    public UserSimpleDTO getUser() {
        return user;
    }

    public void setUser(UserSimpleDTO user) {
        this.user = user;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public PrismDomicile getDomicile() {
        return domicile;
    }

    public void setDomicile(PrismDomicile domicile) {
        this.domicile = domicile;
    }

    public PrismDomicile getNationality() {
        return nationality;
    }

    public void setNationality(PrismDomicile nationality) {
        this.nationality = nationality;
    }

    public Boolean getVisaRequired() {
        return visaRequired;
    }

    public void setVisaRequired(Boolean visaRequired) {
        this.visaRequired = visaRequired;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public PrismEthnicity getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(PrismEthnicity ethnicity) {
        this.ethnicity = ethnicity;
    }

    public PrismDisability getDisability() {
        return disability;
    }

    public void setDisability(PrismDisability disability) {
        this.disability = disability;
    }

}
