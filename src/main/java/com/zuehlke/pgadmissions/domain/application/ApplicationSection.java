package com.zuehlke.pgadmissions.domain.application;

import org.joda.time.DateTime;

public abstract class ApplicationSection {
    
    public abstract DateTime getLastEditedTimestamp();
    
    public abstract void setLastEditedTimestamp(DateTime lastEditedTimestamp);
    
}
