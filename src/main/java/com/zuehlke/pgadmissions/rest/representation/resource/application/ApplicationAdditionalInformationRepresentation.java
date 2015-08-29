package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.DateTime;

public class ApplicationAdditionalInformationRepresentation extends ApplicationSectionRepresentation {

    private String convictionsText;

    public String getConvictionsText() {
        return convictionsText;
    }

    public void setConvictionsText(String convictionsText) {
        this.convictionsText = convictionsText;
    }

    public ApplicationAdditionalInformationRepresentation withConvictionsText(String convictionsText) {
        this.convictionsText = convictionsText;
        return this;
    }
    
    public ApplicationAdditionalInformationRepresentation withLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        setLastUpdatedTimestamp(lastUpdatedTimestamp);
        return this;
    }
    

}
