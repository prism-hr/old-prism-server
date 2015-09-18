package com.zuehlke.pgadmissions.rest.representation.profile;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationSectionRepresentation;

public class ProfileAdditionalInformationRepresentation extends ApplicationSectionRepresentation {

    private String convictionsText;

    public String getConvictionsText() {
        return convictionsText;
    }

    public void setConvictionsText(String convictionsText) {
        this.convictionsText = convictionsText;
    }

    public ProfileAdditionalInformationRepresentation withConvictionsText(String convictionsText) {
        this.convictionsText = convictionsText;
        return this;
    }
    
    public ProfileAdditionalInformationRepresentation withLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        setLastUpdatedTimestamp(lastUpdatedTimestamp);
        return this;
    }
    

}
