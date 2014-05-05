package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "APPLICATION_REFEREE")
public class Referee implements Serializable, FormSectionObject {

    private static final long serialVersionUID = 4591043630090924738L;

    @Id
    @GeneratedValue
    private Integer id;

    @Transient
    private boolean acceptedTerms;

    @Column(name = "last_notified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastNotified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private ApplicationForm application;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private ReferenceComment comment;

    @ESAPIConstraint(rule = "PhoneNumber", maxLength = 35, message = "{text.field.notphonenumber}")
    @Column(name = "phone")
    private String phoneNumber;

    @Column(name = "skype")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 50)
    private String messenger;

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

    @Column(name = "send_to_ucl")
    private Boolean sendToUCL;

    private boolean declined = false;

    public ApplicationForm getApplication() {
        return application;
    }

    public void setApplication(ApplicationForm application) {
        this.application = application;
    }

    public ReferenceComment getComment() {
        return comment;
    }

    public void setComment(ReferenceComment comment) {
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

    public String getMessenger() {
        return messenger;
    }

    public void setMessenger(String messenger) {
        this.messenger = messenger;
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

    public boolean isDeclined() {
        return declined;
    }

    public void setDeclined(boolean declined) {
        this.declined = declined;
    }

    public Date getLastNotified() {
        return lastNotified;
    }

    public void setLastNotified(Date lastNotified) {
        // TODO reimplement that using new actions
        this.lastNotified = lastNotified;
    }

    public boolean isAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }

    public void setSendToUCL(Boolean sendToUCL) {
        this.sendToUCL = sendToUCL;
    }

    public Boolean getSendToUCL() {
        return sendToUCL;
    }

}
