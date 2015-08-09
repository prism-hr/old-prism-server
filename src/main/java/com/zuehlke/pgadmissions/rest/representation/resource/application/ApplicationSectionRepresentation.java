package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.DateTime;

public class ApplicationSectionRepresentation {
    
    private DateTime lastUpdatedTimestamp;

    public final DateTime getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    public final void setLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }
    
}
