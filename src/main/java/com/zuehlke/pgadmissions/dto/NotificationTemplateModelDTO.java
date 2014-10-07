package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;

public class NotificationTemplateModelDTO {

    private User user;

    private Resource resource;

    private User sender;

    private Comment comment;

    private PrismAction transitionAction;

    private String errorMessage;

    private String recommendations;

    private String newPassword;

    public NotificationTemplateModelDTO(User user, Resource resource, User sender) {
        this.user = user;
        this.resource = resource;
        this.sender = sender;
    }

    public User getUser() {
        return user;
    }

    public Resource getResource() {
        return resource;
    }

    public User getSender() {
        return sender;
    }

    public Comment getComment() {
        return comment;
    }

    public PrismAction getTransitionAction() {
        return transitionAction;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public NotificationTemplateModelDTO withComment(final Comment comment) {
        this.comment = comment;
        return this;
    }

    public NotificationTemplateModelDTO withTransitionAction(final PrismAction transitionAction) {
        this.transitionAction = transitionAction;
        return this;
    }

    public NotificationTemplateModelDTO withErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public NotificationTemplateModelDTO withRecommendations(final String recommendations) {
        this.recommendations = recommendations;
        return this;
    }

    public NotificationTemplateModelDTO withNewPassword(final String newPassword) {
        this.newPassword = newPassword;
        return this;
    }


}
