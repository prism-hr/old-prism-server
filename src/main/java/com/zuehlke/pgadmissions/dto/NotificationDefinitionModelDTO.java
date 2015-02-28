package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;

public class NotificationDefinitionModelDTO {

    private User user;

    private User author;

    private User invoker;

    private Resource resource;

    private Comment comment;

    private PrismAction transitionAction;

    private String dataImportErrorMessage;

    private String newPassword;

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

    public String getNewPassword() {
        return newPassword;
    }

    public NotificationDefinitionModelDTO withUser(User user) {
        this.user = user;
        return this;
    }

    public NotificationDefinitionModelDTO withAuthor(User author) {
        this.author = author;
        return this;
    }

    public NotificationDefinitionModelDTO withInvoker(User invoker) {
        this.invoker = invoker;
        return this;
    }
    
    public NotificationDefinitionModelDTO withResource(Resource resource) {
        this.resource = resource;
        return this;
    }

    public NotificationDefinitionModelDTO withComment(final Comment comment) {
        this.comment = comment;
        return this;
    }

    public NotificationDefinitionModelDTO withTransitionAction(final PrismAction transitionAction) {
        this.transitionAction = transitionAction;
        return this;
    }

    public NotificationDefinitionModelDTO withDataImportErrorMessage(final String dataImportErrorMessage) {
        this.dataImportErrorMessage = dataImportErrorMessage;
        return this;
    }

    public NotificationDefinitionModelDTO withNewPassword(final String newPassword) {
        this.newPassword = newPassword;
        return this;
    }

}
