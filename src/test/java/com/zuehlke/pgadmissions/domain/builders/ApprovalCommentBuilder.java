package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class ApprovalCommentBuilder {

    private ApplicationForm applicationForm;
    private String comment;
    private Integer id;
    private Date createdTimeStamp;
    private RegisteredUser user;

    private Boolean projectDescriptionAvailable;
    private String projectTitle;
    private String projectAbstract;
    private Date recommendedStartDate;
    private Boolean recommendedConditionsAvailable;
    private String recommendedConditions;
    private CommentType commentType;

    public ApprovalCommentBuilder createdTimeStamp(Date createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
        return this;
    }
    
    public ApprovalCommentBuilder projectDescriptionAvailable(Boolean projectDescriptionAvailable) {
        this.projectDescriptionAvailable = projectDescriptionAvailable;
        return this;
    }

    public ApprovalCommentBuilder projectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
        return this;
    }

    public ApprovalCommentBuilder projectAbstract(String projectAbstract) {
        this.projectAbstract = projectAbstract;
        return this;
    }

    public ApprovalCommentBuilder recommendedStartDate(Date recommendedStartDate) {
        this.recommendedStartDate = recommendedStartDate;
        return this;
    }

    public ApprovalCommentBuilder recommendedConditionsAvailable(boolean recommendedConditionsAvailable) {
        this.recommendedConditionsAvailable = recommendedConditionsAvailable;
        return this;
    }

    public ApprovalCommentBuilder recommendedConditions(String recommendedConditions) {
        this.recommendedConditions = recommendedConditions;
        return this;
    }

    public ApprovalCommentBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ApprovalCommentBuilder application(ApplicationForm application) {
        this.applicationForm = application;
        return this;
    }

    public ApprovalCommentBuilder comment(String comment) {
        this.comment = comment;
        return this;
    }

    public ApprovalCommentBuilder commentType(CommentType commentType) {
        this.commentType = commentType;
        return this;
    }

    public ApprovalCommentBuilder user(RegisteredUser user) {
        this.user = user;
        return this;
    }

    public ApprovalComment build() {
        ApprovalComment approvalComment = new ApprovalComment();
        approvalComment.setApplication(applicationForm);
        approvalComment.setComment(comment);
        approvalComment.setType(commentType);
        approvalComment.setDate(createdTimeStamp);
        approvalComment.setId(id);
        approvalComment.setUser(user);
        approvalComment.setProjectDescriptionAvailable(projectDescriptionAvailable);
        approvalComment.setProjectTitle(projectTitle);
        approvalComment.setProjectAbstract(projectAbstract);
        approvalComment.setRecommendedStartDate(recommendedStartDate);
        approvalComment.setRecommendedConditionsAvailable(recommendedConditionsAvailable);
        approvalComment.setRecommendedConditions(recommendedConditions);
        return approvalComment;
    }
}
