package com.zuehlke.pgadmissions.domain.builders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.OfferRecommendedComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class OfferRecommendedCommentBuilder {

    private ApplicationForm applicationForm;
    private String comment;
    private Integer id;
    private Date createdTimeStamp;
    private RegisteredUser user;

    private String projectTitle;
    private String projectAbstract;
    private Date recommendedStartDate;
    private Boolean recommendedConditionsAvailable;
    private String recommendedConditions;
    private CommentType commentType;
    private List<Supervisor> supervisors = Lists.newArrayList();

    public OfferRecommendedCommentBuilder createdTimeStamp(Date createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
        return this;
    }
    
    public OfferRecommendedCommentBuilder projectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
        return this;
    }

    public OfferRecommendedCommentBuilder projectAbstract(String projectAbstract) {
        this.projectAbstract = projectAbstract;
        return this;
    }

    public OfferRecommendedCommentBuilder recommendedStartDate(Date recommendedStartDate) {
        this.recommendedStartDate = recommendedStartDate;
        return this;
    }

    public OfferRecommendedCommentBuilder recommendedConditionsAvailable(boolean recommendedConditionsAvailable) {
        this.recommendedConditionsAvailable = recommendedConditionsAvailable;
        return this;
    }

    public OfferRecommendedCommentBuilder recommendedConditions(String recommendedConditions) {
        this.recommendedConditions = recommendedConditions;
        return this;
    }

    public OfferRecommendedCommentBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public OfferRecommendedCommentBuilder application(ApplicationForm application) {
        this.applicationForm = application;
        return this;
    }

    public OfferRecommendedCommentBuilder comment(String comment) {
        this.comment = comment;
        return this;
    }

    public OfferRecommendedCommentBuilder commentType(CommentType commentType) {
        this.commentType = commentType;
        return this;
    }

    public OfferRecommendedCommentBuilder user(RegisteredUser user) {
        this.user = user;
        return this;
    }
    
    public OfferRecommendedCommentBuilder supervisors(Supervisor... supervisors) {
        this.supervisors.addAll(Arrays.asList(supervisors));
        return this;
    }

    public OfferRecommendedComment build() {
        OfferRecommendedComment supervisionConfirmationComment = new OfferRecommendedComment();
        supervisionConfirmationComment.setApplication(applicationForm);
        supervisionConfirmationComment.setComment(comment);
        supervisionConfirmationComment.setType(commentType);
        supervisionConfirmationComment.setDate(createdTimeStamp);
        supervisionConfirmationComment.setId(id);
        supervisionConfirmationComment.setUser(user);
        supervisionConfirmationComment.setProjectTitle(projectTitle);
        supervisionConfirmationComment.setProjectAbstract(projectAbstract);
        supervisionConfirmationComment.setRecommendedStartDate(recommendedStartDate);
        supervisionConfirmationComment.setRecommendedConditionsAvailable(recommendedConditionsAvailable);
        supervisionConfirmationComment.setRecommendedConditions(recommendedConditions);
        supervisionConfirmationComment.setSupervisors(supervisors);
        return supervisionConfirmationComment;
    }
}
