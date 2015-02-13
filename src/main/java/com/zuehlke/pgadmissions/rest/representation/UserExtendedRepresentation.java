package com.zuehlke.pgadmissions.rest.representation;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.resource.FileRepresentation;

public class UserExtendedRepresentation extends UserRepresentation {

    private PrismLocale locale;

    private FileRepresentation portraitDocument;

    private String linkedinUri;

    private String twitterUri;

    private Boolean sendApplicationRecommendationNotification;

    private PrismScope latestCreationScope;

    private Integer permissionPrecedence;

    private String parentUser;

    private List<String> linkedUsers;

    public PrismLocale getLocale() {
        return locale;
    }

    public void setLocale(PrismLocale locale) {
        this.locale = locale;
    }

    public FileRepresentation getPortraitDocument() {
        return portraitDocument;
    }

    public void setPortraitDocument(FileRepresentation portraitDocument) {
        this.portraitDocument = portraitDocument;
    }

    public String getLinkedinUri() {
        return linkedinUri;
    }

    public void setLinkedinUri(String linkedinUri) {
        this.linkedinUri = linkedinUri;
    }

    public String getTwitterUri() {
        return twitterUri;
    }

    public void setTwitterUri(String twitterUri) {
        this.twitterUri = twitterUri;
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
}
