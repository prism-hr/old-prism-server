package com.zuehlke.pgadmissions.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.base.Objects;

@Entity
@Table(name = "COMMENT_APPOINTMENT_PREFERENCE", uniqueConstraints = {@UniqueConstraint(columnNames = {"comment_id", "comment_appointment_timeslot_id"})})
public class CommentAppointmentPreference {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false, insertable = false, updatable = false)
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

    public CommentAppointmentPreference withAppointmentTimeslot(final CommentAppointmentTimeslot appointmentTimeslot) {
        this.appointmentTimeslot = appointmentTimeslot;
        return this;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(comment, appointmentTimeslot);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CommentAppointmentPreference other = (CommentAppointmentPreference) obj;
        return Objects.equal(comment, other.getComment()) && Objects.equal(appointmentTimeslot, other.getAppointmentTimeslot());
    }
    
}
