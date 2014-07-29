package com.zuehlke.pgadmissions.rest.dto.application;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class ApplicationAdditionalInformationDTO {

    @NotNull
    private Boolean hasConvictions;

    @NotEmpty
    private String convictionsText;

    public Boolean getHasConvictions() {
        return hasConvictions;
    }

    public void setHasConvictions(Boolean hasConvictions) {
        this.hasConvictions = hasConvictions;
    }

    public String getConvictionsText() {
        return convictionsText;
    }

    public void setConvictionsText(String convictionsText) {
        this.convictionsText = convictionsText;
    }
}