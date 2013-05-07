package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.enums.InterviewStage;

public class InterviewBuilder {

    private Integer id;
    private ApplicationForm application;
    private Date lastNotified;
    private Date dueDate;
    private String interviewTime;
    private String furtherDetails;
    private String furtherInterviewerDetails;
    private String locationURL;
    private Integer duration;
    private TimeZone timeZone = TimeZone.getTimeZone("GMT");
    private Boolean takenPlace;
    private InterviewStage stage = InterviewStage.INITIAL;
    private List<Interviewer> interviewers = new ArrayList<Interviewer>();

    public InterviewBuilder lastNotified(Date lastNotified) {
        this.lastNotified = lastNotified;
        return this;
    }

    public InterviewBuilder interviewers(Interviewer... interviewers) {
        for (Interviewer interviewer : interviewers) {
            this.interviewers.add(interviewer);
        }
        return this;
    }

    public InterviewBuilder dueDate(Date dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public InterviewBuilder application(ApplicationForm application) {
        this.application = application;
        return this;
    }

    public InterviewBuilder furtherDetails(String furtherDetails) {
        this.furtherDetails = furtherDetails;
        return this;
    }

    public InterviewBuilder furtherInterviewerDetails(String furtherInterviewerDetails) {
        this.furtherInterviewerDetails = furtherInterviewerDetails;
        return this;
    }

    public InterviewBuilder locationURL(String locationURL) {
        this.locationURL = locationURL;
        return this;
    }

    public InterviewBuilder duration(Integer duration) {
        this.duration = duration;
        return this;
    }


    public InterviewBuilder timeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
        return this;
    }
    
    public InterviewBuilder interviewTime(String interviewTime) {
        this.interviewTime = interviewTime;
        return this;
    }

    public InterviewBuilder stage(InterviewStage stage) {
        this.stage = stage;
        return this;
    }
    public InterviewBuilder takenPlace(Boolean takenPlace) {
        this.takenPlace = takenPlace;
        return this;
    }

    public InterviewBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public Interview build() {
        Interview interview = new Interview();
        interview.setId(id);
        interview.setApplication(application);
        interview.setFurtherDetails(furtherDetails);
        interview.setFurtherInterviewerDetails(furtherInterviewerDetails);
        interview.setLastNotified(lastNotified);
        interview.setLocationURL(locationURL);
        interview.setInterviewDueDate(dueDate);
        interview.setDuration(duration);
        interview.setTimeZone(timeZone);
        interview.getInterviewers().addAll(interviewers);
        interview.setInterviewTime(interviewTime);
        interview.setStage(stage);
        interview.setTakenPlace(takenPlace);
        return interview;
    }

}
