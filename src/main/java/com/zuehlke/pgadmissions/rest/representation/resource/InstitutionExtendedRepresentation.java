package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ResourceSummaryRepresentation;

public class InstitutionExtendedRepresentation extends AbstractResourceRepresentation {

    private String domicile;

    private String title;

    private String summary;

    private PrismLocale locale;

    private InstitutionAddressRepresentation address;

    private String currency;

    private PrismAdvertType defaultAdvertType;

    private PrismStudyOption defaultStudyOption;

    private String homepage;

    private FileRepresentation logoDocument;
    
    private ResourceSummaryRepresentation resourceSummary;

    public String getDomicile() {
        return domicile;
    }

    public void setDomicile(String domicile) {
        this.domicile = domicile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public PrismLocale getLocale() {
        return locale;
    }

    public void setLocale(PrismLocale locale) {
        this.locale = locale;
    }

    public InstitutionAddressRepresentation getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddressRepresentation address) {
        this.address = address;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PrismAdvertType getDefaultAdvertType() {
        return defaultAdvertType;
    }

    public void setDefaultAdvertType(PrismAdvertType defaultAdvertType) {
        this.defaultAdvertType = defaultAdvertType;
    }

    public PrismStudyOption getDefaultStudyOption() {
        return defaultStudyOption;
    }

    public void setDefaultStudyOption(PrismStudyOption defaultStudyOption) {
        this.defaultStudyOption = defaultStudyOption;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public FileRepresentation getLogoDocument() {
        return logoDocument;
    }

    public void setLogoDocument(FileRepresentation logoDocument) {
        this.logoDocument = logoDocument;
    }
    
    public final ResourceSummaryRepresentation getResourceSummary() {
        return resourceSummary;
    }

    public final void setResourceSummary(ResourceSummaryRepresentation resourceSummary) {
        this.resourceSummary = resourceSummary;
    }
    
}
