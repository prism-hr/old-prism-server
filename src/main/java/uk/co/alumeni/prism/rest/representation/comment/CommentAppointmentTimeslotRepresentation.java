package uk.co.alumeni.prism.rest.representation.comment;

import org.joda.time.LocalDateTime;

public class CommentAppointmentTimeslotRepresentation {

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

    public CommentAppointmentTimeslotRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public CommentAppointmentTimeslotRepresentation withDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

}
