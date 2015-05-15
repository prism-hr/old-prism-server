package com.zuehlke.pgadmissions.rest.dto;

import java.math.BigDecimal;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class InstitutionDTO extends ResourceParentDTO {

    @NotNull
    private String currency;

    @NotNull
    private Integer businessYearStartMonth;

    @NotNull
    private BigDecimal minimumWage;

    @Valid
    private FileDTO logoImage;

    @Valid
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

    public BigDecimal getMinimumWage() {
        return minimumWage;
    }

    public void setMinimumWage(BigDecimal minimumWage) {
        this.minimumWage = minimumWage;
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
