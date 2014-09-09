package com.zuehlke.pgadmissions.rest.representation.comment;

import org.joda.time.LocalDateTime;

public class AppointmentTimeslotRepresentation {

    public Integer id;

    public LocalDateTime dateTime;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final LocalDateTime getDateTime() {
        return dateTime;
    }

    public final void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public AppointmentTimeslotRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public AppointmentTimeslotRepresentation withDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

}
