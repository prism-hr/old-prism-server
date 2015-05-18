package com.zuehlke.pgadmissions.dto;

import java.util.List;

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
    
    private String applicationRecommendations;

    private String newPassword;
    
    List<AdvertRecommendationDTO> advertRecommendations;

    public User getUser() {
        return user;
    }

    public Resource getResource() {
        return resource;
    }

    public User getAuthor() {
        return author;
    }

    public User getInvoker() {
        return invoker;
    }

    public void setInvoker(User invoker) {
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

    public String getApplicationRecommendations() {
        return applicationRecommendations;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public List<AdvertRecommendationDTO> getAdvertRecommendations() {
        return advertRecommendations;
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

    public NotificationDefinitionModelDTO withComment(Comment comment) {
        this.comment = comment;
        return this;
    }

    public NotificationDefinitionModelDTO withTransitionAction(PrismAction transitionAction) {
        this.transitionAction = transitionAction;
        return this;
    }

    public NotificationDefinitionModelDTO withDataImportErrorMessage(String dataImportErrorMessage) {
        this.dataImportErrorMessage = dataImportErrorMessage;
        return this;
    }
    
    public NotificationDefinitionModelDTO withApplicationRecommendations(String applicationRecommendations) {
        this.applicationRecommendations = applicationRecommendations;
        return this;
    }

    public NotificationDefinitionModelDTO withNewPassword(String newPassword) {
        this.newPassword = newPassword;
        return this;
    }
    
    public NotificationDefinitionModelDTO withAdvertRecommendations(List<AdvertRecommendationDTO> advertRecommendations) {
        this.advertRecommendations = advertRecommendations;
        return this;
    }

}
