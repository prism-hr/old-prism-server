package com.zuehlke.pgadmissions.rest.representation.resource.application;

import com.zuehlke.pgadmissions.domain.definitions.PrismRefereeType;
import com.zuehlke.pgadmissions.rest.representation.address.AddressApplicationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

public class ApplicationRefereeRepresentation extends ApplicationSectionRepresentation {

    private Integer id;

    private UserRepresentationSimple user;

    private PrismRefereeType refereeType;

    private String jobEmployer;

    private String jobTitle;

    private AddressApplicationRepresentation address;

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

    public PrismRefereeType getRefereeType() {
        return refereeType;
    }

    public void setRefereeType(PrismRefereeType refereeType) {
        this.refereeType = refereeType;
    }

    public String getJobEmployer() {
        return jobEmployer;
    }

    public void setJobEmployer(String jobEmployer) {
        this.jobEmployer = jobEmployer;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public AddressApplicationRepresentation getAddress() {
        return address;
    }

    public void setAddress(AddressApplicationRepresentation address) {
        this.address = address;
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

    public ApplicationRefereeRepresentation withRefereeType(PrismRefereeType refereeType) {
        this.refereeType = refereeType;
        return this;
    }

    public ApplicationRefereeRepresentation withJobEmployer(String jobEmployer) {
        this.jobEmployer = jobEmployer;
        return this;
    }

    public ApplicationRefereeRepresentation withJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
        return this;
    }

    public ApplicationRefereeRepresentation withAddress(AddressApplicationRepresentation address) {
        this.address = address;
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

}
