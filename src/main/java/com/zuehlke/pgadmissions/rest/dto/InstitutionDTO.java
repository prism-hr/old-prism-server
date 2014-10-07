package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;

public class InstitutionDTO {

    @NotEmpty
    private String domicile;

    @NotEmpty
    private String title;
    
    @NotEmpty
    private PrismLocale locale;

    @NotNull
    private String currency;

    @NotNull
    private PrismProgramType defaultProgramType;

    @NotNull
    private PrismStudyOption defaultStudyOption;

    @NotNull
    private String summary;
    
    private String description;
    
    @NotEmpty
    private String homepage;

    private Integer logoDocumentId;
    
    private String logoUri;

    private String googleIdentifier;
    
    private String linkedinIdentifier;
    
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

    public final PrismLocale getLocale() {
        return locale;
    }

    public final void setLocale(PrismLocale locale) {
        this.locale = locale;
    }

    public final String getSummary() {
        return summary;
    }

    public final void setSummary(String summary) {
        this.summary = summary;
    }

    public final String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        this.description = description;
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

    public final String getLogoUri() {
        return logoUri;
    }

    public final void setLogoUri(String logoUri) {
        this.logoUri = logoUri;
    }

    public final String getGoogleIdentifier() {
        return googleIdentifier;
    }

    public final void setGoogleIdentifier(String googleIdentifier) {
        this.googleIdentifier = googleIdentifier;
    }

    public final String getLinkedinIdentifier() {
        return linkedinIdentifier;
    }

    public final void setLinkedinIdentifier(String linkedinIdentifier) {
        this.linkedinIdentifier = linkedinIdentifier;
    }

    public InstitutionAddressDTO getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddressDTO address) {
        this.address = address;
    }

}
