package com.zuehlke.pgadmissions.rest.representation.resource.application;

import com.zuehlke.pgadmissions.domain.definitions.PrismRefereeType;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

public class ApplicationRefereeRepresentation extends ApplicationSectionRepresentation {

    private Integer id;

    private UserRepresentationSimple user;

    private PrismRefereeType refereeType;
    
    private String jobEmployer;

    private String jobTitle;

    private ApplicationAddressRepresentationApplication address;
    
    private String phone;

    private String skype;
    
    private Integer commentId;

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
    
    public final PrismRefereeType getRefereeType() {
        return refereeType;
    }

    public final void setRefereeType(PrismRefereeType refereeType) {
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

    public ApplicationAddressRepresentationApplication getAddress() {
        return address;
    }

    public void setAddress(ApplicationAddressRepresentationApplication address) {
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
    
    public final Integer getCommentId() {
        return commentId;
    }

    public final void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

}
