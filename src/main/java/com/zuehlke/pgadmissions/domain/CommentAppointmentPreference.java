package com.zuehlke.pgadmissions.domain;

import javax.persistence.*;

@Entity
@Table(name = "COMMENT_APPOINTMENT_PREFERENCE", uniqueConstraints = {@UniqueConstraint(columnNames = {"comment_id", "comment_appointment_timeslot_id"})})
public class CommentAppointmentPreference {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "comment_id", insertable = false, updatable = false)
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "comment_appointment_timeslot_id", nullable = false)
    private CommentAppointmentTimeslot appointmentTimeslot;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public CommentAppointmentTimeslot getAppointmentTimeslot() {
        return appointmentTimeslot;
    }

    public void setAppointmentTimeslot(CommentAppointmentTimeslot appointmentTimeslot) {
        this.appointmentTimeslot = appointmentTimeslot;
    }

}
