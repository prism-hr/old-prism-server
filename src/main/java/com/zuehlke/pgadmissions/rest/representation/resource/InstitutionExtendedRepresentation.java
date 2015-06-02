package com.zuehlke.pgadmissions.rest.representation.resource;

import java.math.BigDecimal;

import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertRepresentation;

public class InstitutionExtendedRepresentation extends AbstractResourceRepresentation {

    private String title;

    private String currency;

    private BigDecimal minimumWage;

    private Integer businessYearStartMonth;

    private FileRepresentation logoImage;

    private FileRepresentation backgroundImage;

    private AdvertRepresentation advert;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getMinimumWage() {
        return minimumWage;
    }

    public void setMinimumWage(BigDecimal minimumWage) {
        this.minimumWage = minimumWage;
    }

    public Integer getBusinessYearStartMonth() {
        return businessYearStartMonth;
    }

    public void setBusinessYearStartMonth(Integer businessYearStartMonth) {
        this.businessYearStartMonth = businessYearStartMonth;
    }

    public FileRepresentation getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(FileRepresentation logoImage) {
        this.logoImage = logoImage;
    }

    public FileRepresentation getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(FileRepresentation backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public AdvertRepresentation getAdvert() {
        return advert;
    }

    public void setAdvert(AdvertRepresentation advert) {
        this.advert = advert;
    }

}
