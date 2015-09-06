package com.zuehlke.pgadmissions.rest.representation.user;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class UserRepresentationExtended extends UserRepresentationSimple {

    private Boolean sendApplicationRecommendationNotification;

    private PrismScope permissionScope;

    private String parentUser;

    private List<String> linkedUsers;

    private List<String> oauthProviders;

    private PrismRoleCategory requiredFeedbackRoleCategory;

    public Boolean getSendApplicationRecommendationNotification() {
        return sendApplicationRecommendationNotification;
    }

    public void setSendApplicationRecommendationNotification(Boolean sendApplicationRecommendationNotification) {
        this.sendApplicationRecommendationNotification = sendApplicationRecommendationNotification;
    }

    public PrismScope getPermissionScope() {
        return permissionScope;
    }

    public void setPermissionScope(PrismScope permissionScope) {
        this.permissionScope = permissionScope;
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
