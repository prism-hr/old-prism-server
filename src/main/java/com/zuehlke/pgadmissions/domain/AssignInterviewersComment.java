package com.zuehlke.pgadmissions.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.google.common.collect.Lists;

@Entity
@Table
@DiscriminatorValue(value = "ASSIGN_INTERVIEWERS")
public class AssignInterviewersComment extends Comment {

    private static final long serialVersionUID = 9120577563568889651L;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id")
    @OrderBy("dateTime")
    private List<AppointmentTimeslot> availableAppointmentTimeslots = Lists.newArrayList();

    public List<AppointmentTimeslot> getAvailableAppointmentTimeslots() {
        return availableAppointmentTimeslots;
    }
}
