package com.zuehlke.pgadmissions.domain.application;

import org.joda.time.DateTime;

public abstract class ApplicationSection {

    public abstract DateTime getLastUpdatedTimestamp();

    public abstract void setLastUpdatedTimestamp(DateTime lastUpdatedTimestamp);

}
