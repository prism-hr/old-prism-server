package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;

public class AdvertDTO {

    @NotEmpty
    @Size(max = 255)
    private String title;
    
    @NotNull
    private PrismLocale locale;

    @NotNull
    private Boolean defaultLocale;
    
    @NotEmpty
    private String currency;

    @NotEmpty
    @Size(max = 1000)
    private String summary;

    private String description;
    
    private AdvertImageDTO images;

    @NotEmpty
    private String homepage;
    
    @Valid
    private InstitutionAddressDTO address;
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PrismLocale getLocale() {
        return locale;
    }

    public void setLocale(PrismLocale locale) {
        this.locale = locale;
    }

    public Boolean getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(Boolean defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AdvertImageDTO getImages() {
        return images;
    }

    public void setImages(AdvertImageDTO images) {
        this.images = images;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public InstitutionAddressDTO getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddressDTO address) {
        this.address = address;
    }
    
}
