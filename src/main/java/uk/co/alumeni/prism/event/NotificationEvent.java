package uk.co.alumeni.prism.event;

import org.springframework.context.ApplicationEvent;

import uk.co.alumeni.prism.domain.definitions.PrismResourceContext;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition;
import uk.co.alumeni.prism.rest.dto.resource.ResourceDTO;
import uk.co.alumeni.prism.rest.representation.advert.AdvertListRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation;

public class NotificationEvent extends ApplicationEvent {

    private static final long serialVersionUID = -6201013453673728033L;

    private PrismNotificationDefinition notificationDefinition;

    private Integer initiator;

    private Integer recipient;

    private Integer signatory;

    private Integer candidate;

    private ResourceDTO resource;

    private Integer comment;

    private Integer message;

    private Integer advertTarget;

    private ResourceDTO invitedResource;

    private PrismResourceContext invitedResourceContext;

    private String invitationMessage;

    private PrismAction transitionAction;

    private String newPassword;

    private UserActivityRepresentation userActivityRepresentation;

    private AdvertListRepresentation advertListRepresentation;

    private Boolean buffered;

    public NotificationEvent(Object source) {
        super(source);
    }

    public PrismNotificationDefinition getNotificationDefinition() {
        return notificationDefinition;
    }

    public void setNotificationDefinition(PrismNotificationDefinition notificationDefinition) {
        this.notificationDefinition = notificationDefinition;
    }

    public Integer getInitiator() {
        return initiator;
    }

    public void setInitiator(Integer initiator) {
        this.initiator = initiator;
    }

    public Integer getRecipient() {
        return recipient;
    }

    public void setRecipient(Integer recipient) {
        this.recipient = recipient;
    }

    public Integer getSignatory() {
        return signatory;
    }

    public void setSignatory(Integer signatory) {
        this.signatory = signatory;
    }

    public Integer getCandidate() {
        return candidate;
    }

    public void setCandidate(Integer candidate) {
        this.candidate = candidate;
    }

    public ResourceDTO getResource() {
        return resource;
    }

    public void setResource(ResourceDTO resource) {
        this.resource = resource;
    }

    public Integer getComment() {
        return comment;
    }

    public void setComment(Integer comment) {
        this.comment = comment;
    }

    public Integer getMessage() {
        return message;
    }

    public void setMessage(Integer message) {
        this.message = message;
    }

    public Integer getAdvertTarget() {
        return advertTarget;
    }

    public void setAdvertTarget(Integer advertTarget) {
        this.advertTarget = advertTarget;
    }

    public ResourceDTO getInvitedResource() {
        return invitedResource;
    }

    public void setInvitedResource(ResourceDTO invitedResource) {
        this.invitedResource = invitedResource;
    }

    public PrismResourceContext getInvitedResourceContext() {
        return invitedResourceContext;
    }

    public void setInvitedResourceContext(PrismResourceContext invitedResourceContext) {
        this.invitedResourceContext = invitedResourceContext;
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

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public UserActivityRepresentation getUserActivityRepresentation() {
        return userActivityRepresentation;
    }

    public void setUserActivityRepresentation(UserActivityRepresentation userActivityRepresentation) {
        this.userActivityRepresentation = userActivityRepresentation;
    }

    public AdvertListRepresentation getAdvertListRepresentation() {
        return advertListRepresentation;
    }

    public void setAdvertListRepresentation(AdvertListRepresentation advertListRepresentation) {
        this.advertListRepresentation = advertListRepresentation;
    }

    public Boolean getBuffered() {
        return buffered;
    }

    public void setBuffered(Boolean buffered) {
        this.buffered = buffered;
    }

    public NotificationEvent withNotificationDefinition(PrismNotificationDefinition notificationDefinition) {
        this.notificationDefinition = notificationDefinition;
        return this;
    }

    public NotificationEvent withInitiator(Integer initiator) {
        this.initiator = initiator;
        return this;
    }

    public NotificationEvent withRecipient(Integer recipient) {
        this.recipient = recipient;
        return this;
    }

    public NotificationEvent withCandidate(Integer candidate) {
        this.candidate = candidate;
        return this;
    }

    public NotificationEvent withResource(ResourceDTO resource) {
        this.resource = resource;
        return this;
    }

    public NotificationEvent withComment(Integer comment) {
        this.comment = comment;
        return this;
    }

    public NotificationEvent withMessage(Integer message) {
        this.message = message;
        return this;
    }

    public NotificationEvent withAdvertTarget(Integer advertTarget) {
        this.advertTarget = advertTarget;
        return this;
    }

    public NotificationEvent withInvitedResource(ResourceDTO invitedResource) {
        this.invitedResource = invitedResource;
        return this;
    }

    public NotificationEvent withInvitedResourceContext(PrismResourceContext invitedResourceContext) {
        this.invitedResourceContext = invitedResourceContext;
        return this;
    }

    public NotificationEvent withInvitationMessage(String invitationMessage) {
        this.invitationMessage = invitationMessage;
        return this;
    }

    public NotificationEvent withTransitionAction(PrismAction transitionAction) {
        this.transitionAction = transitionAction;
        return this;
    }

    public NotificationEvent withNewPassword(String newPassword) {
        this.newPassword = newPassword;
        return this;
    }

    public NotificationEvent withUserActivityRepresentation(UserActivityRepresentation userActivityRepresentation) {
        this.userActivityRepresentation = userActivityRepresentation;
        return this;
    }

    public NotificationEvent withAdvertListRepresentation(AdvertListRepresentation advertListRepresentation) {
        this.advertListRepresentation = advertListRepresentation;
        return this;
    }

    public NotificationEvent withBuffered(Boolean buffered) {
        this.buffered = buffered;
        return this;
    }

}
