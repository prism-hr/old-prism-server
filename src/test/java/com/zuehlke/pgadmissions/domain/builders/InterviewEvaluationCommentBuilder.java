package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.CompleteInterviewComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class InterviewEvaluationCommentBuilder {

    private RegisteredUser user;
    private ApplicationForm application;
    private Date date;
    private String comment;
    private Integer id;
    private ApplicationFormStatus nextStatus;

    public InterviewEvaluationCommentBuilder nextStatus(ApplicationFormStatus nextStatus) {
        this.nextStatus = nextStatus;
        return this;
    }

    public InterviewEvaluationCommentBuilder user(RegisteredUser user) {
        this.user = user;
        return this;
    }

    public InterviewEvaluationCommentBuilder application(ApplicationForm application) {
        this.application = application;
        return this;
    }

    public InterviewEvaluationCommentBuilder date(Date date) {
        this.date = date;
        return this;
    }

    public InterviewEvaluationCommentBuilder comment(String comment) {
        this.comment = comment;
        return this;
    }

    public InterviewEvaluationCommentBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public CompleteInterviewComment build() {
        CompleteInterviewComment reviewEaluationComment = new CompleteInterviewComment();
        reviewEaluationComment.setApplication(application);
        reviewEaluationComment.setContent(comment);
        reviewEaluationComment.setCreatedTimestamp(date);
        reviewEaluationComment.setId(id);
        reviewEaluationComment.setUser(user);
        reviewEaluationComment.setNextStatus(nextStatus);
        return reviewEaluationComment;
    }
}
