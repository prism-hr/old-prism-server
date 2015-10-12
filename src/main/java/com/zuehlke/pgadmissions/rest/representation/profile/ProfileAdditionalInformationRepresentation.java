package com.zuehlke.pgadmissions.rest.representation.profile;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationSectionRepresentation;

public class ProfileAdditionalInformationRepresentation extends ApplicationSectionRepresentation {

    private String requirements;

    private String convictions;

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getConvictions() {
        return convictions;
    }

    public void setConvictions(String convictions) {
        this.convictions = convictions;
    }

    public ProfileAdditionalInformationRepresentation withRequirements(String requirements) {
        this.requirements = requirements;
        return this;
    }

    public ProfileAdditionalInformationRepresentation withConvictions(String convictions) {
        this.convictions = convictions;
        return this;
    }

    public ProfileAdditionalInformationRepresentation withLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        setLastUpdatedTimestamp(lastUpdatedTimestamp);
        return this;
    }

}
