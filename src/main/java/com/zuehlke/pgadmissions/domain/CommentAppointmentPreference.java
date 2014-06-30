package com.zuehlke.pgadmissions.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "COMMENT_APPOINTMENT_PREFERENCE", uniqueConstraints = { @UniqueConstraint(columnNames = { "comment_id", "comment_appointment_timeslot_id" }) })
public class CommentAppointmentPreference {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "comment_appointment_timeslot_id", nullable = false)
    private CommentAppointmentTimeslot appointmentTimeslot;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CommentAppointmentTimeslot getAppointmentTimeslot() {
        return appointmentTimeslot;
    }

    public void setAppointmentTimeslot(CommentAppointmentTimeslot appointmentTimeslot) {
        this.appointmentTimeslot = appointmentTimeslot;
    }

}
