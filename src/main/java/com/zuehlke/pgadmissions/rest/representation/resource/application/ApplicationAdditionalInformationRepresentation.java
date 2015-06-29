package com.zuehlke.pgadmissions.rest.representation.resource.application;

public class ApplicationAdditionalInformationRepresentation extends ApplicationSectionRepresentation {

    private String convictionsText;

    public String getConvictionsText() {
        return convictionsText;
    }

    public void setConvictionsText(String convictionsText) {
        this.convictionsText = convictionsText;
    }
}
