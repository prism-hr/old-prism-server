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

    @NotNull
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

    private FileDTO logoDocument;

    private String logoUri;

    private String googleIdentifier;

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

    public final FileDTO getLogoDocument() {
        return logoDocument;
    }

    public final void setLogoDocument(FileDTO logoDocument) {
        this.logoDocument = logoDocument;
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

    public InstitutionAddressDTO getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddressDTO address) {
        this.address = address;
    }

}
