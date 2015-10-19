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

    private User initiator;
    
    private User recipient;
    
    private User signatory;

    private Resource resource;

    private Comment comment;
    
    private AdvertTarget advertTarget;
    
    private ResourceParent parentResource;
    
    private ResourceParent targetResource;
    
    private PrismMotivationContext targetContext;

    private PrismAction transitionAction;

    private String newPassword;

    List<AdvertRecommendationDTO> advertRecommendations;

    public User getInitiator() {
        return initiator;
    }

    public User getRecipient() {
        return recipient;
    }
    
    public User getSignatory() {
        return signatory;
    }
    
    public Resource getResource() {
        return resource;
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

    public NotificationDefinitionDTO withInitiator(User initiator) {
        this.initiator = initiator;
        return this;
    }
    
    public NotificationDefinitionDTO withRecipient(User user) {
        this.recipient = user;
        return this;
    }
    
    public NotificationDefinitionDTO withSignatory(User signatory) {
        this.signatory = signatory;
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
