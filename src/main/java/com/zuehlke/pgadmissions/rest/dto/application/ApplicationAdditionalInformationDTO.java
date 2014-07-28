package com.zuehlke.pgadmissions.rest.dto.application;

public class ApplicationAdditionalInformationDTO {

    private Boolean hasConvictions;

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