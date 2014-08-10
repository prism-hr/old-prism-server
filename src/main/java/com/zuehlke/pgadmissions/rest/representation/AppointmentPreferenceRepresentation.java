package com.zuehlke.pgadmissions.rest.representation;

import org.joda.time.DateTime;

public class AppointmentPreferenceRepresentation {

    private DateTime dateTime;

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }
}
