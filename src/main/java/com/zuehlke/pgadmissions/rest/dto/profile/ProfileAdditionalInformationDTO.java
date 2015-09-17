package com.zuehlke.pgadmissions.rest.dto.profile;


import javax.validation.constraints.Size;

public class ProfileAdditionalInformationDTO {

    @Size(max = 400)
    private String convictionsText;

    public String getConvictionsText() {
        return convictionsText;
    }

    public void setConvictionsText(String convictionsText) {
        this.convictionsText = convictionsText;
    }
}
