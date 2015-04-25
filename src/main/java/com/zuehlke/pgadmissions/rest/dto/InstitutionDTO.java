package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;

public class InstitutionDTO {

    @NotEmpty
    private String domicile;

    @NotNull
    private AdvertDTO advert;
    
    @Size(min = 1)
    private PrismStudyOption[] studyOptions;

    @NotNull
    private InstitutionAddressDTO address;
    
    @NotNull
    private String currency;
    
    @NotNull
    private Integer businessYearStartMonth;
    
    private String googleIdentifier;

    public String getDomicile() {
        return domicile;
    }

    public void setDomicile(String domicile) {
        this.domicile = domicile;
    }

    public AdvertDTO getAdvert() {
        return advert;
    }

    public void setAdvert(AdvertDTO advert) {
        this.advert = advert;
    }

    public PrismStudyOption[] getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(PrismStudyOption[] studyOptions) {
        this.studyOptions = studyOptions;
    }

    public InstitutionAddressDTO getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddressDTO address) {
        this.address = address;
    }
    
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public Integer getBusinessYearStartMonth() {
        return businessYearStartMonth;
    }

    public void setBusinessYearStartMonth(Integer businessYearStartMonth) {
        this.businessYearStartMonth = businessYearStartMonth;
    }

    public  String getGoogleIdentifier() {
        return googleIdentifier;
    }

    public  void setGoogleIdentifier(String googleIdentifier) {
        this.googleIdentifier = googleIdentifier;
    }

}
