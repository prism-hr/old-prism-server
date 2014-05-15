package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.CompleteInterviewComment;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

public class InterviewEvaluationCommentBuilder {

    private User user;
    private ApplicationForm application;
    private Date date;
    private String comment;
    private Integer id;
    private PrismState nextStatus;

    public InterviewEvaluationCommentBuilder nextStatus(PrismState nextStatus) {
        this.nextStatus = nextStatus;
        return this;
    }

    public InterviewEvaluationCommentBuilder user(User user) {
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
