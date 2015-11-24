package uk.co.alumeni.prism.domain.application;

import org.joda.time.DateTime;

public abstract class ApplicationSection {

    public abstract DateTime getLastUpdatedTimestamp();

    public abstract void setLastUpdatedTimestamp(DateTime lastUpdatedTimestamp);

}
