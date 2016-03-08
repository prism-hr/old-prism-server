package uk.co.alumeni.prism.rest.representation.user;

import java.util.List;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationConnection;

public class UserRepresentationExtended extends UserRepresentationSimple {

    private Boolean sendActivityNotification;

    private List<UserRolesRepresentation> userRoles;

    private String parentUser;

    private List<String> linkedUsers;

    private Boolean connectedWithLinkedin;

    private PrismRoleCategory requiredFeedbackRoleCategory;

    private List<ResourceRepresentationConnection> resourcesForWhichUserCanCreateConnections;

    public Boolean getSendActivityNotification() {
        return sendActivityNotification;
    }

    public void setSendActivityNotification(Boolean sendActivityNotification) {
        this.sendActivityNotification = sendActivityNotification;
    }

    public List<UserRolesRepresentation> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRolesRepresentation> userRoles) {
        this.userRoles = userRoles;
    }

    public String getParentUser() {
        return parentUser;
    }

    public void setParentUser(String parentUser) {
        this.parentUser = parentUser;
    }

    public List<String> getLinkedUsers() {
        return linkedUsers;
    }

    public void setLinkedUsers(List<String> linkedUsers) {
        this.linkedUsers = linkedUsers;
    }

    public Boolean getConnectedWithLinkedin() {
        return connectedWithLinkedin;
    }

    public void setConnectedWithLinkedin(Boolean connectedWithLinkedin) {
        this.connectedWithLinkedin = connectedWithLinkedin;
    }

    public PrismRoleCategory getRequiredFeedbackRoleCategory() {
        return requiredFeedbackRoleCategory;
    }

    public void setRequiredFeedbackRoleCategory(PrismRoleCategory requiredFeedbackRoleCategory) {
        this.requiredFeedbackRoleCategory = requiredFeedbackRoleCategory;
    }

    public List<ResourceRepresentationConnection> getResourcesForWhichUserCanCreateConnections() {
        return resourcesForWhichUserCanCreateConnections;
    }

    public void setResourcesForWhichUserCanCreateConnections(List<ResourceRepresentationConnection> resourcesForWhichUserCanCreateConnections) {
        this.resourcesForWhichUserCanCreateConnections = resourcesForWhichUserCanCreateConnections;
    }

}
