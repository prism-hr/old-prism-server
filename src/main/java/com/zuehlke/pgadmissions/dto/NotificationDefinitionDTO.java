package com.zuehlke.pgadmissions.dto;

import java.util.List;

import com.zuehlke.pgadmissions.domain.advert.AdvertTarget;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismMotivationContext;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;

public class NotificationDefinitionDTO {

    private User user;

    private User author;

    private User invoker;

    private Resource resource;

    private Comment comment;
    
    private AdvertTarget advertTarget;
    
    private ResourceParent parentResource;
    
    private ResourceParent targetResource;
    
    private PrismMotivationContext targetContext;

    private PrismAction transitionAction;

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

    public AdvertTarget getAdvertTarget() {
        return advertTarget;
    }

    public ResourceParent getParentResource() {
        return parentResource;
    }

    public ResourceParent getTargetResource() {
        return targetResource;
    }

    public PrismMotivationContext getTargetContext() {
        return targetContext;
    }

    public PrismAction getTransitionAction() {
        return transitionAction;
    }

    public void setTransitionAction(PrismAction transitionAction) {
        this.transitionAction = transitionAction;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public List<AdvertRecommendationDTO> getAdvertRecommendations() {
        return advertRecommendations;
    }

    public NotificationDefinitionDTO withUser(User user) {
        this.user = user;
        return this;
    }

    public NotificationDefinitionDTO withAuthor(User author) {
        this.author = author;
        return this;
    }

    public NotificationDefinitionDTO withInvoker(User invoker) {
        this.invoker = invoker;
        return this;
    }

    public NotificationDefinitionDTO withResource(Resource resource) {
        this.resource = resource;
        return this;
    }

    public NotificationDefinitionDTO withComment(Comment comment) {
        this.comment = comment;
        return this;
    }
    
    public NotificationDefinitionDTO withAdvertTarget(AdvertTarget advertTarget) {
        this.advertTarget = advertTarget;
        return this;
    }
    
    public NotificationDefinitionDTO withParentResource(ResourceParent parentResource) {
        this.parentResource = parentResource;
        return this;
    }
    
    public NotificationDefinitionDTO withTargetResource(ResourceParent targetResource) {
        this.targetResource = targetResource;
        return this;
    }
    
    public NotificationDefinitionDTO withTargetContext(PrismMotivationContext targetContext) {
        this.targetContext = targetContext;
        return this;
    }

    public NotificationDefinitionDTO withTransitionAction(PrismAction transitionAction) {
        this.transitionAction = transitionAction;
        return this;
    }

    public NotificationDefinitionDTO withNewPassword(String newPassword) {
        this.newPassword = newPassword;
        return this;
    }

    public NotificationDefinitionDTO withAdvertRecommendations(List<AdvertRecommendationDTO> advertRecommendations) {
        this.advertRecommendations = advertRecommendations;
        return this;
    }

}
