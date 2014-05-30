package com.zuehlke.pgadmissions.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "COMMENT_APPOINTMENT_PREFERENCE")
public class AppointmentPreference {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "comment_appointment_timeslot_id", nullable = false)
    private AppointmentTimeslot appointmentTimeslot;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AppointmentTimeslot getAppointmentTimeslot() {
        return appointmentTimeslot;
    }

    public void setAppointmentTimeslot(AppointmentTimeslot appointmentTimeslot) {
        this.appointmentTimeslot = appointmentTimeslot;
    }

}
