package com.zuehlke.pgadmissions.rest.domain.application;

public class AdditionalInformationRepresentation {

    private boolean hasConvictions;

    private String convictionsText;

    public boolean isHasConvictions() {
        return hasConvictions;
    }

    public void setHasConvictions(boolean hasConvictions) {
        this.hasConvictions = hasConvictions;
    }

    public String getConvictionsText() {
        return convictionsText;
    }

    public void setConvictionsText(String convictionsText) {
        this.convictionsText = convictionsText;
    }
}
