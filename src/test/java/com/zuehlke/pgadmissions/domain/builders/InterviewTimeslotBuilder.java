package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.InterviewTimeslot;

public class InterviewTimeslotBuilder {
    private Integer id;
    private Date dueDate;
    private String startTime;
    private Set<InterviewParticipant> acceptedParticipants = new HashSet<InterviewParticipant>();

    public InterviewTimeslotBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public InterviewTimeslotBuilder dueDate(Date dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public InterviewTimeslotBuilder startTime(String startTime) {
        this.startTime = startTime;
        return this;
    }


    public InterviewTimeslotBuilder acceptedParticipants(Set<InterviewParticipant> acceptedParticipants) {
        this.acceptedParticipants = acceptedParticipants;
        return this;
    }

    public InterviewTimeslot build() {
        InterviewTimeslot interviewTimeslot = new InterviewTimeslot();
        interviewTimeslot.setId(id);
        interviewTimeslot.setDueDate(dueDate);
        interviewTimeslot.setStartTime(startTime);
        interviewTimeslot.setAcceptedParticipants(acceptedParticipants);
        return interviewTimeslot;
    }
}