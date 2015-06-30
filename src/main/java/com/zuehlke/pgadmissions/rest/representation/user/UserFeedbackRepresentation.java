package com.zuehlke.pgadmissions.rest.representation.user;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;

public class UserFeedbackRepresentation {

    private ResourceRepresentationSimple resource;
    
    private UserRepresentationSimple user;
    
    private PrismRoleCategory roleCategory;
    
    private Integer rating;

    private String content;

    private String featureRequest;

    private Boolean recommended;

    private DateTime createdTimestamp;

    public ResourceRepresentationSimple getResource() {
        return resource;
    }

    public void setResource(ResourceRepresentationSimple resource) {
        this.resource = resource;
    }
    
    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    public PrismRoleCategory getRoleCategory() {
        return roleCategory;
    }

    public void setRoleCategory(PrismRoleCategory roleCategory) {
        this.roleCategory = roleCategory;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFeatureRequest() {
        return featureRequest;
    }

    public void setFeatureRequest(String featureRequest) {
        this.featureRequest = featureRequest;
    }

    public Boolean getRecommended() {
        return recommended;
    }

    public void setRecommended(Boolean recommended) {
        this.recommended = recommended;
    }

    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
    
    public UserFeedbackRepresentation withResource(ResourceRepresentationSimple resource) {
        this.resource = resource;
        return this;
    }
    
    public UserFeedbackRepresentation withUser(UserRepresentationSimple user) {
        this.user = user;
        return this;
    }

    public UserFeedbackRepresentation withRoleCategory(PrismRoleCategory roleCategory) {
        this.roleCategory = roleCategory;
        return this;
    }
    
}
