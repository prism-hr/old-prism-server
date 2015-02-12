package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

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

    public  PrismLocale getLocale() {
        return locale;
    }

    public  void setLocale(PrismLocale locale) {
        this.locale = locale;
    }

    public  String getSummary() {
        return summary;
    }

    public  void setSummary(String summary) {
        this.summary = summary;
    }

    public  String getDescription() {
        return description;
    }

    public  void setDescription(String description) {
        this.description = description;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public  PrismProgramType getDefaultProgramType() {
        return defaultProgramType;
    }

    public  void setDefaultProgramType(PrismProgramType defaultProgramType) {
        this.defaultProgramType = defaultProgramType;
    }

    public  PrismStudyOption getDefaultStudyOption() {
        return defaultStudyOption;
    }

    public  void setDefaultStudyOption(PrismStudyOption defaultStudyOption) {
        this.defaultStudyOption = defaultStudyOption;
    }

    public  FileDTO getLogoDocument() {
        return logoDocument;
    }

    public  void setLogoDocument(FileDTO logoDocument) {
        this.logoDocument = logoDocument;
    }

    public  String getLogoUri() {
        return logoUri;
    }

    public  void setLogoUri(String logoUri) {
        this.logoUri = logoUri;
    }

    public  String getGoogleIdentifier() {
        return googleIdentifier;
    }

    public  void setGoogleIdentifier(String googleIdentifier) {
        this.googleIdentifier = googleIdentifier;
    }

    public InstitutionAddressDTO getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddressDTO address) {
        this.address = address;
    }

}
