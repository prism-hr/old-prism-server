package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;

public class NotificationTemplateModelDTO {

    private User user;

    private User author;
    
    private User invoker;
    
    private Resource resource;

    private Comment comment;

    private PrismAction transitionAction;

    private String dataImportErrorMessage;

    private String applicationRecommendation;

    private String newPassword;

    public NotificationTemplateModelDTO(User user, User author, Resource resource) {
        this.user = user;
        this.author = author;
        this.resource = resource;
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

    public final User getInvoker() {
        return invoker;
    }

    public final void setInvoker(User invoker) {
        this.invoker = invoker;
    }

    public Comment getComment() {
        return comment;
    }

    public PrismAction getTransitionAction() {
        return transitionAction;
    }

    public void setTransitionAction(PrismAction transitionAction) {
        this.transitionAction = transitionAction;
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
