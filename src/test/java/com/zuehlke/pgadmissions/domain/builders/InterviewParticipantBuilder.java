package com.zuehlke.pgadmissions.domain.builders;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.InterviewTimeslot;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class InterviewParticipantBuilder {

    private Integer id;

    private RegisteredUser user;

    private Boolean responded = false;
    
    private Date lastNotified;

    private Set<InterviewTimeslot> acceptedTimeslots = new HashSet<InterviewTimeslot>();
    
    private Interview interview;
    
    private Boolean canMakeIt = false;

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

    public InterviewParticipantBuilder lastNotified(Date lastNotified) {
        this.lastNotified = lastNotified;
        return this;
    }

    public InterviewParticipantBuilder acceptedTimeslots(InterviewTimeslot... acceptedTimeslots) {
        this.acceptedTimeslots.addAll(Arrays.asList(acceptedTimeslots));
        return this;
    }

    public InterviewParticipantBuilder interview(Interview interview) {
        this.interview = interview;
        return this;
    }

    public InterviewParticipantBuilder canMakeIt(Boolean canMakeIt) {
        this.canMakeIt = canMakeIt;
        return this;
    }

    public InterviewParticipant build() {
        InterviewParticipant interviewParticipant = new InterviewParticipant();
        interviewParticipant.setId(id);
        interviewParticipant.setUser(user);
        interviewParticipant.setResponded(responded);
        interviewParticipant.setLastNotified(lastNotified);
        interviewParticipant.setAcceptedTimeslots(acceptedTimeslots);
        interviewParticipant.setInterview(interview);
        interviewParticipant.setCanMakeIt(canMakeIt);
        return interviewParticipant;
    }
}