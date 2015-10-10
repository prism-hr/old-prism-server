package com.zuehlke.pgadmissions.rest.representation.user;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationConnection;

public class UserRepresentationExtended extends UserRepresentationSimple {

    private Boolean sendApplicationRecommendationNotification;

    private List<PrismScope> visibleScopes;

    private String parentUser;

    private List<String> linkedUsers;

    private Boolean connectedWithLinkedin;

    private PrismRoleCategory requiredFeedbackRoleCategory;

    private List<ResourceRepresentationConnection> resourcesForWhichUserCanCreateConnections;

    public Boolean getSendApplicationRecommendationNotification() {
        return sendApplicationRecommendationNotification;
    }

    public void setSendApplicationRecommendationNotification(Boolean sendApplicationRecommendationNotification) {
        this.sendApplicationRecommendationNotification = sendApplicationRecommendationNotification;
    }

    public List<PrismScope> getVisibleScopes() {
        return visibleScopes;
    }

    public void setVisibleScopes(List<PrismScope> visibleScopes) {
        this.visibleScopes = visibleScopes;
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
