package com.zuehlke.pgadmissions.rest.representation;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.resource.FileRepresentation;

public class UserExtendedRepresentation extends UserRepresentation {

    private FileRepresentation portraitDocument;

    private Boolean sendApplicationRecommendationNotification;

    private PrismScope latestCreationScope;

    private Integer permissionPrecedence;

    private String parentUser;

    private List<String> linkedUsers;

    private List<String> oauthProviders;

    private PrismRoleCategory requiredFeedbackRoleCategory;

    public FileRepresentation getPortraitDocument() {
        return portraitDocument;
    }

    public void setPortraitDocument(FileRepresentation portraitDocument) {
        this.portraitDocument = portraitDocument;
    }

    public Boolean getSendApplicationRecommendationNotification() {
        return sendApplicationRecommendationNotification;
    }

    public void setSendApplicationRecommendationNotification(Boolean sendApplicationRecommendationNotification) {
        this.sendApplicationRecommendationNotification = sendApplicationRecommendationNotification;
    }

    public PrismScope getLatestCreationScope() {
        return latestCreationScope;
    }

    public void setLatestCreationScope(PrismScope latestCreationScope) {
        this.latestCreationScope = latestCreationScope;
    }

    public Integer getPermissionPrecedence() {
        return permissionPrecedence;
    }

    public void setPermissionPrecedence(Integer permissionPrecedence) {
        this.permissionPrecedence = permissionPrecedence;
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

    public List<String> getOauthProviders() {
        return oauthProviders;
    }

    public void setOauthProviders(List<String> oauthProviders) {
        this.oauthProviders = oauthProviders;
    }

    public PrismRoleCategory getRequiredFeedbackRoleCategory() {
        return requiredFeedbackRoleCategory;
    }

    public void setRequiredFeedbackRoleCategory(PrismRoleCategory requiredFeedbackRoleCategory) {
        this.requiredFeedbackRoleCategory = requiredFeedbackRoleCategory;
    }
}
