package com.zuehlke.pgadmissions.rest.dto.application;


import javax.validation.constraints.Size;

public class ApplicationAdditionalInformationDTO {

    @Size(max = 400)
    private String convictionsText;

    public String getConvictionsText() {
        return convictionsText;
    }

    public void setConvictionsText(String convictionsText) {
        this.convictionsText = convictionsText;
    }
}
