package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.SupervisionConfirmationComment;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class SupervisionConfirmationCommentBuilder {

    private ApplicationForm applicationForm;
    private String comment;
    private Integer id;
    private Date createdTimeStamp;
    private RegisteredUser user;

    private Supervisor supervisor;
    private String projectTitle;
    private String projectAbstract;
    private Date recommendedStartDate;
    private Boolean recommendedConditionsAvailable;
    private String recommendedConditions;
    private CommentType commentType;

    public SupervisionConfirmationCommentBuilder supervisor(Supervisor supervisor) {
        this.supervisor = supervisor;
        return this;
    }

    public SupervisionConfirmationCommentBuilder projectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
        return this;
    }

    public SupervisionConfirmationCommentBuilder projectAbstract(String projectAbstract) {
        this.projectAbstract = projectAbstract;
        return this;
    }

    public SupervisionConfirmationCommentBuilder recommendedStartDate(Date recommendedStartDate) {
        this.recommendedStartDate = recommendedStartDate;
        return this;
    }

    public SupervisionConfirmationCommentBuilder recommendedConditionsAvailable(boolean recommendedConditionsAvailable) {
        this.recommendedConditionsAvailable = recommendedConditionsAvailable;
        return this;
    }

    public SupervisionConfirmationCommentBuilder recommendedConditions(String recommendedConditions) {
        this.recommendedConditions = recommendedConditions;
        return this;
    }

    public SupervisionConfirmationCommentBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public SupervisionConfirmationCommentBuilder application(ApplicationForm application) {
        this.applicationForm = application;
        return this;
    }

    public SupervisionConfirmationCommentBuilder comment(String comment) {
        this.comment = comment;
        return this;
    }

    public SupervisionConfirmationCommentBuilder commentType(CommentType commentType) {
        this.commentType = commentType;
        return this;
    }

    public SupervisionConfirmationCommentBuilder user(RegisteredUser user) {
        this.user = user;
        return this;
    }

    public SupervisionConfirmationComment build() {
        SupervisionConfirmationComment supervisionConfirmationComment = new SupervisionConfirmationComment();
        supervisionConfirmationComment.setApplication(applicationForm);
        supervisionConfirmationComment.setComment(comment);
        supervisionConfirmationComment.setType(commentType);
        supervisionConfirmationComment.setDate(createdTimeStamp);
        supervisionConfirmationComment.setId(id);
        supervisionConfirmationComment.setUser(user);
        supervisionConfirmationComment.setSupervisor(supervisor);
        supervisionConfirmationComment.setProjectTitle(projectTitle);
        supervisionConfirmationComment.setProjectAbstract(projectAbstract);
        supervisionConfirmationComment.setRecommendedStartDate(recommendedStartDate);
        supervisionConfirmationComment.setRecommendedConditionsAvailable(recommendedConditionsAvailable);
        supervisionConfirmationComment.setRecommendedConditions(recommendedConditions);
        return supervisionConfirmationComment;
    }
}
