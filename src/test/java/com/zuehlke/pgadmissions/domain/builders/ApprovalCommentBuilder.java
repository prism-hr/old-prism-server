package com.zuehlke.pgadmissions.domain.builders;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.AssignSupervisorsComment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.User;

public class ApprovalCommentBuilder {

    private Application applicationForm;
    private String content;
    private Integer id;
    private DateTime createdTimeStamp;
    private User user;

    private Boolean projectDescriptionAvailable;
    private String projectTitle;
    private String projectAbstract;
    private LocalDate recommendedStartDate;
    private Boolean recommendedConditionsAvailable;
    private String recommendedConditions;
    private List<CommentAssignedUser> assignedUsers;

    public ApprovalCommentBuilder createdTimeStamp(DateTime createdTimeStamp) {
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

    public ApprovalCommentBuilder recommendedStartDate(LocalDate recommendedStartDate) {
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

    public ApprovalCommentBuilder application(Application application) {
        this.applicationForm = application;
        return this;
    }

    public ApprovalCommentBuilder content(String content) {
        this.content = content;
        return this;
    }

    public ApprovalCommentBuilder user(User user) {
        this.user = user;
        return this;
    }
    
    public ApprovalCommentBuilder assignedUsers(CommentAssignedUser... assignedUsers) {
        this.assignedUsers = Arrays.asList(assignedUsers);
        return this;
    }

    public AssignSupervisorsComment build() {
        AssignSupervisorsComment approvalComment = new AssignSupervisorsComment();
        approvalComment.setApplication(applicationForm);
        approvalComment.setContent(content);
        approvalComment.setCreatedTimestamp(createdTimeStamp);
        approvalComment.setId(id);
        approvalComment.setUser(user);
        approvalComment.setProjectDescriptionAvailable(projectDescriptionAvailable);
        approvalComment.setProjectTitle(projectTitle);
        approvalComment.setProjectAbstract(projectAbstract);
        approvalComment.setRecommendedStartDate(recommendedStartDate);
        approvalComment.setRecommendedConditionsAvailable(recommendedConditionsAvailable);
        approvalComment.setRecommendedConditions(recommendedConditions);
        approvalComment.getAssignedUsers().addAll(assignedUsers);
        return approvalComment;
    }
}
