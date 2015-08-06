package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.address.AddressApplication;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.PrismRefereeType;
import com.zuehlke.pgadmissions.domain.user.User;

public class ApplicationReferenceDTO {

    private Integer id;
    
    private User user;
    
    private PrismRefereeType refereeType;

    private String jobEmployer;
    
    private String jobTitle;

    private AddressApplication address;

    private String phone;
    
    private String skype;

    private Comment comment;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public final User getUser() {
        return user;
    }

    public final void setUser(User user) {
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

    public final String getJobTitle() {
        return jobTitle;
    }

    public final void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public AddressApplication getAddress() {
        return address;
    }

    public void setAddress(AddressApplication address) {
        this.address = address;
    }

    public final String getPhone() {
        return phone;
    }

    public final void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public final Comment getComment() {
        return comment;
    }

    public final void setComment(Comment comment) {
        this.comment = comment;
    }

}
