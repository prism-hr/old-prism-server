package com.zuehlke.pgadmissions.rest.representation.comment;

import org.joda.time.LocalDateTime;

public class AppointmentPreferenceRepresentation {

    private LocalDateTime dateTime;

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    
}
