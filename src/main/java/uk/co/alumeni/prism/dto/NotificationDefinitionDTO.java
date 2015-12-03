package uk.co.alumeni.prism.dto;

import uk.co.alumeni.prism.domain.advert.AdvertTarget;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.definitions.PrismResourceContext;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.rest.representation.advert.AdvertListRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation;

public class NotificationDefinitionDTO {

    private User initiator;

    private User recipient;

    private User signatory;

    private Resource resource;

    private Comment comment;

    private AdvertTarget advertTarget;

    private ResourceParent invitedResource;

    private PrismResourceContext invitedResourceContext;

    private String invitationMessage;

    private PrismAction transitionAction;

    private String newPassword;

    private UserActivityRepresentation userActivityRepresentation;

    private AdvertListRepresentation advertListRepresentation;

    private Boolean buffered;

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

    public PrismResourceContext getInvitedResourceContext() {
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

    public UserActivityRepresentation getUserActivityRepresentation() {
        return userActivityRepresentation;
    }

    public AdvertListRepresentation getAdvertListRepresentation() {
        return advertListRepresentation;
    }

    public Boolean getBuffered() {
        return buffered;
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

    public NotificationDefinitionDTO withInvitedResourceContext(PrismResourceContext invitedResourceContext) {
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

    public NotificationDefinitionDTO withUserActivityRepresentation(UserActivityRepresentation userActivityRepresentation) {
        this.userActivityRepresentation = userActivityRepresentation;
        return this;
    }

    public NotificationDefinitionDTO withAdvertListRepresentation(AdvertListRepresentation advertListRepresentation) {
        this.advertListRepresentation = advertListRepresentation;
        return this;
    }

    public NotificationDefinitionDTO withBuffered(Boolean buffered) {
        this.buffered = buffered;
        return this;
    }

}
