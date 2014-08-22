package com.zuehlke.pgadmissions.rest.representation.comment;

import org.joda.time.DateTime;

public class AppointmentTimeslotRepresentation {
    
    public Integer id;
    
    public DateTime dateTime;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final DateTime getDateTime() {
        return dateTime;
    }

    public final void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }
    
    public AppointmentTimeslotRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }
    
    public AppointmentTimeslotRepresentation withDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }
    
}
