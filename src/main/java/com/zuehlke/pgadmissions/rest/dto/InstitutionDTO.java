package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;

public class InstitutionDTO extends ResourceParentDTO {

    @NotNull
    private String currency;

    @NotNull
    private Integer businessYearStartMonth;

    private String googleIdentifier;

    private FileDTO logoImage;

    private FileDTO backgroundImage;

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

    public String getGoogleIdentifier() {
        return googleIdentifier;
    }

    public void setGoogleIdentifier(String googleIdentifier) {
        this.googleIdentifier = googleIdentifier;
    }

    public FileDTO getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(FileDTO logoImage) {
        this.logoImage = logoImage;
    }

    public FileDTO getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(FileDTO backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

}
