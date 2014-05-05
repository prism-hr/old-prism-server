package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.User;

public class InterviewCommentBuilder {

    private Boolean willingToSupervice;
    private Boolean suitableCandidateForUCL;
    private Boolean suitableCandidateForProgramme;
    private Integer applicantRating;
    private boolean decline;
    private ApplicationForm applicationForm;
    private String content;
    private Integer id;
    private Date createdTimeStamp;
    private User user;

    public InterviewCommentBuilder createdTimeStamp(Date createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
        return this;
    }

    public InterviewCommentBuilder willingToSupervise(Boolean willingToSupervice) {
        this.willingToSupervice = willingToSupervice;
        return this;
    }

    public InterviewCommentBuilder suitableCandidateForUcl(Boolean suitableCandidate) {
        this.suitableCandidateForUCL = suitableCandidate;
        return this;
    }

    public InterviewCommentBuilder suitableCandidateForProgramme(Boolean suitableCandidateForProgramme) {
        this.suitableCandidateForProgramme = suitableCandidateForProgramme;
        return this;
    }

    public InterviewCommentBuilder applicantRating(Integer applicantRating) {
        this.applicantRating = applicantRating;
        return this;
    }

    public InterviewCommentBuilder decline(boolean decline) {
        this.decline = decline;
        return this;
    }

    public InterviewCommentBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public InterviewCommentBuilder application(ApplicationForm application) {
        this.applicationForm = application;
        return this;
    }

    public InterviewCommentBuilder content(String content) {
        this.content = content;
        return this;
    }

    public InterviewCommentBuilder user(User user) {
        this.user = user;
        return this;
    }

    public InterviewComment build() {
        InterviewComment interviewComment = new InterviewComment();
        interviewComment.setApplication(applicationForm);
        interviewComment.setContent(content);
        interviewComment.setCreatedTimestamp(createdTimeStamp);
        interviewComment.setDeclined(decline);
        interviewComment.setId(id);
        interviewComment.setSuitableForInstitution(suitableCandidateForUCL);
        interviewComment.setUser(user);
        interviewComment.setWillingToSupervise(willingToSupervice);
        interviewComment.setSuitableForProgramme(suitableCandidateForProgramme);
        interviewComment.setRating(applicantRating);
        return interviewComment;
    }
}
