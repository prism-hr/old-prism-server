package com.zuehlke.pgadmissions.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "APPLICATION_REFEREE")
public class Referee {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ESAPIConstraint(rule = "PhoneNumber", maxLength = 35, message = "{text.field.notphonenumber}")
    @Column(name = "phone")
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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    @Valid
    private Address address;

    @Column(name = "include_in_export")
    private Boolean includeInExport;

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

    public void setIncludeInExport(Boolean includeInExport) {
        this.includeInExport = includeInExport;
    }

    public Boolean getIncludeInExport() {
        return includeInExport;
    }

    public Referee withId(Integer id) {
        this.id = id;
        return this;
    }
    
    public Referee withUser(User user) {
        this.user = user;
        return this;
    }
    
    public Referee withApplication(Application application) {
        this.application = application;
        return this;
    }
    
    public Referee withComment(Comment comment) {
        this.comment = comment;
        return this;
    }
    
    public Referee withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }
    
    public Referee withSkype(String skype) {
        this.skype = skype;
        return this;
    }
    
    public Referee withJobEmployer(String jobEmployer) {
        this.jobEmployer = jobEmployer;
        return this;
    }
    
    public Referee withJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
        return this;
    }
    
    public Referee withAddress(Address address) {
        this.address = address;
        return this;
    }
    
    public Referee withIncludeInExport(Boolean includeInExport) {
        this.includeInExport = includeInExport;
        return this;
    }
    
}
