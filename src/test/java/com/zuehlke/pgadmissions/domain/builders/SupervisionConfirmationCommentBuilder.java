package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.SupervisionConfirmationComment;
import com.zuehlke.pgadmissions.domain.User;

public class SupervisionConfirmationCommentBuilder {

    private Application applicationForm;
    private String comment;
    private Integer id;
    private Date createdTimeStamp;
    private User user;

    private String projectTitle;
    private String projectAbstract;
    private Date recommendedStartDate;
    private Boolean recommendedConditionsAvailable;
    private String recommendedConditions;

    public SupervisionConfirmationCommentBuilder createdTimeStamp(Date createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
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

    public SupervisionConfirmationCommentBuilder application(Application application) {
        this.applicationForm = application;
        return this;
    }

    public SupervisionConfirmationCommentBuilder comment(String comment) {
        this.comment = comment;
        return this;
    }

    public SupervisionConfirmationCommentBuilder user(User user) {
        this.user = user;
        return this;
    }

    public SupervisionConfirmationComment build() {
        SupervisionConfirmationComment supervisionConfirmationComment = new SupervisionConfirmationComment();
        supervisionConfirmationComment.setApplication(applicationForm);
        supervisionConfirmationComment.setContent(comment);
        supervisionConfirmationComment.setCreatedTimestamp(createdTimeStamp);
        supervisionConfirmationComment.setId(id);
        supervisionConfirmationComment.setUser(user);
        supervisionConfirmationComment.setProjectTitle(projectTitle);
        supervisionConfirmationComment.setProjectAbstract(projectAbstract);
        supervisionConfirmationComment.setRecommendedStartDate(recommendedStartDate);
        supervisionConfirmationComment.setRecommendedConditionsAvailable(recommendedConditionsAvailable);
        supervisionConfirmationComment.setRecommendedConditions(recommendedConditions);
        return supervisionConfirmationComment;
    }
}
