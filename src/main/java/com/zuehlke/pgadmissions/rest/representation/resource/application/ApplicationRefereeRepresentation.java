package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.rest.representation.comment.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationActivity;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

public class ApplicationRefereeRepresentation extends ApplicationSectionRepresentation {

    private Integer id;

    private UserRepresentationSimple user;

    private ResourceRepresentationActivity resource;

    private String phone;

    private String skype;

    private CommentRepresentation comment;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    public ResourceRepresentationActivity getResource() {
        return resource;
    }

    public void setResource(ResourceRepresentationActivity resource) {
        this.resource = resource;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public CommentRepresentation getComment() {
        return comment;
    }

    public void setComment(CommentRepresentation comment) {
        this.comment = comment;
    }

    public ApplicationRefereeRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationRefereeRepresentation withUser(UserRepresentationSimple user) {
        this.user = user;
        return this;
    }

    public ApplicationRefereeRepresentation withResource(ResourceRepresentationActivity resource) {
        this.resource = resource;
        return this;
    }

    public ApplicationRefereeRepresentation withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public ApplicationRefereeRepresentation withSkype(String skype) {
        this.skype = skype;
        return this;
    }

    public ApplicationRefereeRepresentation withComment(CommentRepresentation comment) {
        this.comment = comment;
        return this;
    }

    public ApplicationRefereeRepresentation withLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        setLastUpdatedTimestamp(lastUpdatedTimestamp);
        return this;
    }

}
