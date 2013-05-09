package com.zuehlke.pgadmissions.domain.builders;

import java.util.HashSet;
import java.util.Set;

import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.InterviewTimeslot;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class InterviewParticipantBuilder {

    private Integer id;

    private RegisteredUser user;

    private Boolean responded = false;

    private Set<InterviewTimeslot> acceptedTimeslots = new HashSet<InterviewTimeslot>();

    public InterviewParticipantBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public InterviewParticipantBuilder user(RegisteredUser user) {
        this.user = user;
        return this;
    }

    public InterviewParticipantBuilder responded(Boolean responded) {
        this.responded = responded;
        return this;
    }

    public InterviewParticipantBuilder acceptedTimeslots(Set<InterviewTimeslot> acceptedTimeslots) {
        this.acceptedTimeslots = acceptedTimeslots;
        return this;
    }

    public InterviewParticipant build() {
        InterviewParticipant interviewParticipant = new InterviewParticipant();
        interviewParticipant.setId(id);
        interviewParticipant.setUser(user);
        interviewParticipant.setResponded(responded);
        interviewParticipant.setAcceptedTimeslots(acceptedTimeslots);
        return interviewParticipant;
    }
}