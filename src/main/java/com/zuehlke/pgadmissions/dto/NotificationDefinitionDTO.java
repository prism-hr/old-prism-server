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

    private ResourceParent invitedResource;

    private PrismMotivationContext invitedResourceContext;
    
    private String invitationMessage;

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

    public void setSignatory(User signatory) {
        this.signatory = signatory;
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

    public ResourceParent getInvitedResource() {
        return invitedResource;
    }

    public PrismMotivationContext getInvitedResourceContext() {
        return invitedResourceContext;
    }

    public String getInvitationMessage() {
        return invitationMessage;
    }

    public void setInvitationMessage(String invitationMessage) {
        this.invitationMessage = invitationMessage;
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

    public NotificationDefinitionDTO withInvitedResource(ResourceParent invitedResource) {
        this.invitedResource = invitedResource;
        return this;
    }

    public NotificationDefinitionDTO withInvitedResourceContext(PrismMotivationContext invitedResourceContext) {
        this.invitedResourceContext = invitedResourceContext;
        return this;
    }
    
    public NotificationDefinitionDTO withInvitationMessage(String invitationMessage) {
        this.invitationMessage = invitationMessage;
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
