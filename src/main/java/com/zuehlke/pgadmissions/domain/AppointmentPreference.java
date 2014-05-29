package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "COMMENT_APPOINTMENT_PREFERENCE")
public class AppointmentPreference implements Serializable {

    private static final long serialVersionUID = -730673777949846236L;

    @Id
    @GeneratedValue
    private Integer id;

    @JoinColumn(name = "comment_appointment_timeslot_id")
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
