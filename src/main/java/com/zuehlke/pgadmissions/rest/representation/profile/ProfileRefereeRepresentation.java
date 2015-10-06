package com.zuehlke.pgadmissions.rest.representation.profile;

import com.zuehlke.pgadmissions.rest.representation.comment.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRelationInvitationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationSectionRepresentation;

public class ProfileRefereeRepresentation extends ApplicationSectionRepresentation {

    private Integer id;

    private ResourceRelationInvitationRepresentation resource;

    private String phone;

    private String skype;

    private CommentRepresentation comment;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ResourceRelationInvitationRepresentation getResource() {
        return resource;
    }

    public void setResource(ResourceRelationInvitationRepresentation resource) {
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

    public ProfileRefereeRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public ProfileRefereeRepresentation withResource(final ResourceRelationInvitationRepresentation resource) {
        this.resource = resource;
        return this;
    }

    public ProfileRefereeRepresentation withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public ProfileRefereeRepresentation withSkype(String skype) {
        this.skype = skype;
        return this;
    }

    public ProfileRefereeRepresentation withComment(CommentRepresentation comment) {
        this.comment = comment;
        return this;
    }

}
