package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;

public class InstitutionDTO {

    @NotEmpty
    private String domicile;

    @NotEmpty
    private String title;

    private String currency;

    @NotEmpty
    private String homepage;
    
    @NotNull
    private PrismProgramType defaultProgramType;
    
    @NotNull
    private PrismStudyOption defaultStudyOption;

    @NotNull
    private Integer logoDocumentId;

    @NotNull
    private InstitutionAddressDTO address;

    public String getDomicile() {
        return domicile;
    }

    public void setDomicile(String domicile) {
        this.domicile = domicile;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public final PrismProgramType getDefaultProgramType() {
        return defaultProgramType;
    }

    public final void setDefaultProgramType(PrismProgramType defaultProgramType) {
        this.defaultProgramType = defaultProgramType;
    }

    public final PrismStudyOption getDefaultStudyOption() {
        return defaultStudyOption;
    }

    public final void setDefaultStudyOption(PrismStudyOption defaultStudyOption) {
        this.defaultStudyOption = defaultStudyOption;
    }

    public final Integer getLogoDocumentId() {
        return logoDocumentId;
    }

    public final void setLogoDocumentId(Integer logoDocumentId) {
        this.logoDocumentId = logoDocumentId;
    }

    public InstitutionAddressDTO getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddressDTO address) {
        this.address = address;
    }

}
