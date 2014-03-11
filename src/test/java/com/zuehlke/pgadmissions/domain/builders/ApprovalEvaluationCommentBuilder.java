package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.CompleteApprovalComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class ApprovalEvaluationCommentBuilder {

    private RegisteredUser user;
    private ApplicationForm application;
    private Date createdTimestamp;
    private String comment;
    private Integer id;
    private ApplicationFormStatus nextStatus;

    public ApprovalEvaluationCommentBuilder nextStatus(ApplicationFormStatus nextStatus) {
        this.nextStatus = nextStatus;
        return this;
    }

    public ApprovalEvaluationCommentBuilder user(RegisteredUser user) {
        this.user = user;
        return this;
    }

    public ApprovalEvaluationCommentBuilder application(ApplicationForm application) {
        this.application = application;
        return this;
    }

    public ApprovalEvaluationCommentBuilder createdTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

    public ApprovalEvaluationCommentBuilder comment(String comment) {
        this.comment = comment;
        return this;
    }

    public ApprovalEvaluationCommentBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public CompleteApprovalComment build() {
        CompleteApprovalComment approvalEaluationComment = new CompleteApprovalComment();
        approvalEaluationComment.setApplication(application);
        approvalEaluationComment.setContent(comment);
        approvalEaluationComment.setCreatedTimestamp(createdTimestamp);
        approvalEaluationComment.setId(id);
        approvalEaluationComment.setUser(user);
        approvalEaluationComment.setNextStatus(nextStatus);
        return approvalEaluationComment;
    }
}
