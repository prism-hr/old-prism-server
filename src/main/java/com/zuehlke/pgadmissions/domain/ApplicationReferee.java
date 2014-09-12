package com.zuehlke.pgadmissions.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.rest.validation.annotation.ESAPIConstraint;

@Entity
@Table(name = "APPLICATION_REFEREE", uniqueConstraints = { @UniqueConstraint(columnNames = { "application_id", "user_id" }) })
public class ApplicationReferee {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false, updatable = false, insertable = false)
    private Application application;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ESAPIConstraint(rule = "PhoneNumber", maxLength = 35, message = "{text.field.notphonenumber}")
    @Column(name = "phone", nullable = false)
    private String phoneNumber;

    @Column(name = "skype")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
    private String skype;

    @Column(name = "job_employer")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 200)
    private String jobEmployer;

    @Column(name = "job_title")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 200)
    private String jobTitle;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ApplicationReferee withId(Integer id) {
        this.id = id;
        return this;
    }
    
    public ApplicationReferee withUser(User user) {
        this.user = user;
        return this;
    }
    
    public ApplicationReferee withApplication(Application application) {
        this.application = application;
        return this;
    }
    
    public ApplicationReferee withComment(Comment comment) {
        this.comment = comment;
        return this;
    }
    
    public ApplicationReferee withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }
    
    public ApplicationReferee withSkype(String skype) {
        this.skype = skype;
        return this;
    }
    
    public ApplicationReferee withJobEmployer(String jobEmployer) {
        this.jobEmployer = jobEmployer;
        return this;
    }
    
    public ApplicationReferee withJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
        return this;
    }
    
    public ApplicationReferee withAddress(Address address) {
        this.address = address;
        return this;
    }
    
}
