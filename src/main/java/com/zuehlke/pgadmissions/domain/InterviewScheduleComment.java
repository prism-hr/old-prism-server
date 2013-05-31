package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Entity(name = "INTERVIEW_SCHEDULE_COMMENT")
public class InterviewScheduleComment extends Comment {

    private static final long serialVersionUID = -3138212534729565852L;

    @Enumerated(EnumType.STRING)
    @Column(name = "comment_type")
    private CommentType type = CommentType.INTERVIEW_SCHEDULE;

    @Column(name = "further_details")
    private String furtherDetails;

    @Column(name = "further_interviewer_details")
    private String furtherInterviewerDetails;

    public CommentType getType() {
        return type;
    }

    public void setType(CommentType type) {
    }

    public String getFurtherDetails() {
        return furtherDetails;
    }

    public void setFurtherDetails(String furtherDetails) {
        this.furtherDetails = furtherDetails;
    }

    public String getFurtherInterviewerDetails() {
        return furtherInterviewerDetails;
    }

    public void setFurtherInterviewerDetails(String furtherInterviewerDetails) {
        this.furtherInterviewerDetails = furtherInterviewerDetails;
    }

}
