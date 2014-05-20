package com.zuehlke.pgadmissions.domain.builders;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.User;

public class ReviewCommentBuilder {

    private Boolean willingToInterview;
    private Boolean suitableCandidateForUcl;
    private Boolean suitableCandidateForProgramme;
    private Boolean willingToWorkWithApplicant;
    private Integer applicantRating;
    private boolean decline;
    private Application applicationForm;
    private String content;
    private Integer id;
    private DateTime createdTimeStamp;
    private User user;

    public ReviewCommentBuilder willingToWorkWithApplicant(Boolean willingToWorkWithApplicant) {
        this.willingToWorkWithApplicant = willingToWorkWithApplicant;
        return this;
    }

    public ReviewCommentBuilder createdTimeStamp(DateTime createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
        return this;
    }

    public ReviewCommentBuilder willingToInterview(Boolean willingToInterview) {
        this.willingToInterview = willingToInterview;
        return this;
    }

    public ReviewCommentBuilder suitableCandidateForUCL(Boolean suitableCandidate) {
        this.suitableCandidateForUcl = suitableCandidate;
        return this;
    }

    public ReviewCommentBuilder suitableCandidateForProgramme(Boolean suitableCandidateForProgramme) {
        this.suitableCandidateForProgramme = suitableCandidateForProgramme;
        return this;
    }

    public ReviewCommentBuilder applicantRating(Integer applicantRating) {
        this.applicantRating = applicantRating;
        return this;
    }

    public ReviewCommentBuilder decline(boolean decline) {
        this.decline = decline;
        return this;
    }

    public ReviewCommentBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ReviewCommentBuilder application(Application application) {
        this.applicationForm = application;
        return this;
    }

    public ReviewCommentBuilder content(String content) {
        this.content = content;
        return this;
    }

    public ReviewCommentBuilder user(User user) {
        this.user = user;
        return this;
    }

    public ReviewComment build() {
        ReviewComment reviewComment = new ReviewComment();
        reviewComment.setApplication(applicationForm);
        reviewComment.setContent(content);
        reviewComment.setCreatedTimestamp(createdTimeStamp);
        reviewComment.setDeclined(decline);
        reviewComment.setId(id);
        reviewComment.setSuitableForInstitution(suitableCandidateForUcl);
        reviewComment.setUser(user);
        reviewComment.setWillingToInterview(willingToInterview);
        reviewComment.setSuitableForProgramme(suitableCandidateForProgramme);
        reviewComment.setWillingToSupervise(willingToWorkWithApplicant);
        reviewComment.setRating(applicantRating);
        return reviewComment;
    }
}
