package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;

public class NotificationTemplateModelDTO {

    private User user;

    private Resource resource;

    private User author;

    private Comment comment;

    private PrismAction transitionAction;

    private String dataImportErrorMessage;

    private String applicationRecommendation;

    private String newPassword;

    public NotificationTemplateModelDTO(User user, Resource resource, User author) {
        this.user = user;
        this.resource = resource;
        this.author = author;
    }

    public User getUser() {
        return user;
    }

    public Resource getResource() {
        return resource;
    }

    public User getAuthor() {
        return author;
    }

    public Comment getComment() {
        return comment;
    }

    public PrismAction getTransitionAction() {
        return transitionAction;
    }

    public String getDataImportErrorMessage() {
        return dataImportErrorMessage;
    }

    public String getApplicationRecommendation() {
        return applicationRecommendation;
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

    public NotificationTemplateModelDTO withDataImportErrorMessage(final String dataImportErrorMessage) {
        this.dataImportErrorMessage = dataImportErrorMessage;
        return this;
    }

    public NotificationTemplateModelDTO withApplicationRecommendation(final String applicationRecommendation) {
        this.applicationRecommendation = applicationRecommendation;
        return this;
    }

    public NotificationTemplateModelDTO withNewPassword(final String newPassword) {
        this.newPassword = newPassword;
        return this;
    }

}
